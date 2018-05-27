/*
 * probe_sniffer.cpp
 *
 *  Created on: 01 mag 2018
 *      Author: alberto
 */


/*
 * 		Namespaces
 */
using namespace std;


/*
 * 		Includes
 */
#include <memory>
#include <vector>
#include "esp_wifi.h"
#include "esp_log.h"
#include "esp_event_loop.h"
#include "nvs_flash.h"
#include "lwip/err.h"
#include "lwip/sys.h"
#include "lwip/api.h"
#include "lwip/sockets.h"
#include "driver/timer.h"
#include "lwip/err.h"
#include "Packet.h"

/*
 * 		Defines
 */
#define PROBE_REQ_CTRL 64
#define BEACON_CTRL 128
#define N 20
#define TIMER_INTR_SEL TIMER_INTR_LEVEL
#define TIMER_GROUP    TIMER_GROUP_0
#define TIMER_DIVIDER   80
#define TIMER_SCALE    (TIMER_BASE_CLK / TIMER_DIVIDER)
#define TIMER_FINE_ADJ   (0*(TIMER_BASE_CLK / TIMER_DIVIDER)/1000000)
#define TIMER_INTERVAL0_SEC   (60)
/*
 * 		Structs
 */
struct wifi_mac_t {
	uint16_t frame_ctrl:16;
	uint16_t duration_id:16;
	uint8_t addr1[6];
	uint8_t addr2[6];
	uint8_t addr3[6];
	uint16_t sequence_ctrl:16;
};
struct wifi_packet_t {
	wifi_mac_t mac_address;
	uint8_t *payload;
};
struct ssid_t {
	uint8_t id;
	uint8_t lenght;
	uint8_t ssid[0];
};


/*
 * 		Enums
 */
enum modes {
	SERVER,
	SNIFFER,
	CLIENT
};


/*
 * 		Functions prototypes
 */
/* Entry point */
void probe_sniffer(void);
/* Initializations */
static void wifi_init(void);
static void socket_server_udp_init(void);
static void socket_server_tcp_init(void);
static void socket_client_init(void);
static void sniffer_timer_init(void);
/* Handlers */
static esp_err_t event_handler(void *ctx, system_event_t *event);
static void sniffer_callback(void *buffer, wifi_promiscuous_pkt_type_t type);
static void IRAM_ATTR timer_callback(void *arg);
/* Socket communication */
static void socket_send_data(void);
static void socket_receive_data(void);
static void socket_synchronize(void);
/* Sniffer */
static void sniffer_on(void);
static void sniffer_off(void);
/* Utilities */
static void printMac(uint8_t mac[6]);


/*
 * 		Global variables
 */
static const char *TAG = "probe_sniffer";
static vector<Packet*> packets_list;
static int s_fd, c_fd, conn_fd, i = 0;
static enum modes mode = SERVER;
static struct sockaddr_in caddr;
static esp_err_t err;
static bool alert = false;

/* Entry point */
void probe_sniffer(void)
{
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK( ret );

    wifi_init();

    while(1)
	{
    	vTaskDelay(10);
		if(!alert)
			continue;
		printf("\nTimeout\n\n");

		printf("Socket init\n");
		socket_client_init();
		sniffer_off();
		socket_send_data();
		printf("Close socket\n");
		close(c_fd);

		printf("Socket synch\n");
		socket_synchronize();

		sniffer_on();
		printf("Timer init\n");
		sniffer_timer_init();
	}
}

/* Initialize Wi-Fi, called once */
static void wifi_init(void)
{
    tcpip_adapter_init();

    ESP_ERROR_CHECK(esp_event_loop_init(event_handler, NULL));

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
	ESP_ERROR_CHECK(esp_wifi_init(&cfg));

	wifi_config_t wifi_config;
    memset(&wifi_config, 0, sizeof(wifi_config));
    strcpy((char*)wifi_config.sta.ssid, CONFIG_WIFI_SSID);
    strcpy((char*)wifi_config.sta.password, CONFIG_WIFI_PASSWORD);

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));
	ESP_ERROR_CHECK(esp_wifi_start());

    printf("\nSystem ready.\n\n");

}

/* Initialize server socket */
static void socket_server_udp_init(void)
{
	s_fd = socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(1500);
	addr.sin_addr.s_addr = INADDR_ANY;
	bind(s_fd, (struct sockaddr *)&addr, sizeof(addr));
}

static void socket_server_tcp_init(void)
{
	s_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(2000);
	addr.sin_addr.s_addr = INADDR_ANY;
	bind(s_fd, (struct sockaddr *)&addr, sizeof(addr));
	listen(s_fd, 5);
}

/* Initialize client socket */
static void socket_client_init(void)
{
	c_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	caddr.sin_port = htons(1500);
	struct sockaddr_in addr;
	bzero(&addr, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_port = htons(1500);
	addr.sin_addr.s_addr = caddr.sin_addr.s_addr;
	connect(c_fd, (sockaddr *) &addr, sizeof(addr));
}

/* Initialize timer */
static void sniffer_timer_init(void)
{
	timer_group_t timer_group = TIMER_GROUP_0;
	timer_idx_t timer_idx = TIMER_0;
	timer_config_t config;
	config.alarm_en = 1;
	config.auto_reload = 1;
	config.counter_dir = TIMER_COUNT_UP;
	config.divider = TIMER_DIVIDER;
	config.intr_type = TIMER_INTR_LEVEL;
	config.counter_en = TIMER_PAUSE;

	timer_init(timer_group, timer_idx, &config);
	timer_pause(timer_group, timer_idx);
	timer_set_counter_value(timer_group, timer_idx, 0x00000000ULL);
	timer_set_alarm_value(timer_group, timer_idx, (TIMER_INTERVAL0_SEC * TIMER_SCALE) - TIMER_FINE_ADJ);
	timer_enable_intr(timer_group, timer_idx);
	timer_isr_register(timer_group, timer_idx, timer_callback, (void*)timer_idx, ESP_INTR_FLAG_IRAM, NULL);
	timer_start(timer_group, timer_idx);
	alert = false;
}

/* Handler for timer time-out */
static void IRAM_ATTR timer_callback(void *arg)
{
	TIMERG0.int_clr_timers.t0 = 1;
	alert = true;
}

/* Handler for sniffer */
static void sniffer_callback(void *buffer, wifi_promiscuous_pkt_type_t type)
{
	wifi_pkt_rx_ctrl_t ctrl;
	struct wifi_packet_t *packet;
	struct ssid_t *ssid;

	ctrl = ((wifi_promiscuous_pkt_t *)buffer)->rx_ctrl;
	packet = (wifi_packet_t*)((wifi_promiscuous_pkt_t *)buffer)->payload;

	if(packet->mac_address.frame_ctrl == PROBE_REQ_CTRL)
	{
		Packet *p = new Packet();
		ssid = (ssid_t*) &(packet->payload);

		string s((char *) ssid->ssid, ssid->lenght);
		p->setRssi(ctrl.rssi);
		p->setTime(ctrl.timestamp);
		p->setMac(packet->mac_address.addr2);
		p->setSsid(s);
		printf("PROBE REQUEST - %d - rssi: %d timestamp: %u ", i++, p->getRssi(), p->getTime());
		printMac(packet->mac_address.addr2);
		printf(" ssid: %s l: %d id: %d noise: %d channel: %d\n",
				p->getSsid().c_str(), ssid->lenght, ssid->id, ctrl.noise_floor, ctrl.channel);

		packets_list.insert(packets_list.begin(), p);
	}
}

/* General handler */
static esp_err_t event_handler(void *ctx, system_event_t *event)
{
    switch (event->event_id) {

        case SYSTEM_EVENT_STA_START:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_START");
            err = esp_wifi_connect();
            if(err == ESP_ERR_WIFI_SSID || err == ESP_ERR_WIFI_CONN)
            {
            	printf("Invalid SSID or Password, change on make menuconfig/Probe Sniffer Configuration.\n");
            	printf("Current ssid: %s", CONFIG_WIFI_SSID);
            	/* LED */
            }
            break;

        case SYSTEM_EVENT_STA_STOP:
        	ESP_ERROR_CHECK(esp_wifi_start());
        	break;

        case SYSTEM_EVENT_STA_GOT_IP:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP");
            ESP_LOGI(TAG, "Got IP: %s\n",
			ip4addr_ntoa(&event->event_info.got_ip.ip_info.ip));

            if(mode == SERVER)
				socket_receive_data();

            sniffer_on();
			sniffer_timer_init();
            break;

        case SYSTEM_EVENT_STA_DISCONNECTED:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_DISCONNECTED");
            printf("Current ssid: %s\n", CONFIG_WIFI_SSID);
            ESP_ERROR_CHECK(esp_wifi_connect());
           // socket_client_init();
            break;

        case SYSTEM_EVENT_STA_CONNECTED:
			ESP_LOGI(TAG, "SYSTEM_EVENT_STA CONNECTED");
			break;

        default:
            break;
    }
    return ESP_OK;
}

static void sniffer_on(void)
{
	printf("Sniffer on.\n");

	for(int i = 0; i < packets_list.size(); i++)
		delete packets_list[i];

	packets_list.clear();

	ESP_ERROR_CHECK(esp_wifi_set_promiscuous_rx_cb(&sniffer_callback));
	ESP_ERROR_CHECK(esp_wifi_set_promiscuous(true));

	mode = SNIFFER;
}

static void sniffer_off(void)
{
	printf("Sniffer off.\n");
	ESP_ERROR_CHECK(esp_wifi_set_promiscuous(false));
	mode = CLIENT;
}

/* Send data to the desktop application */
static void socket_send_data(void)
{
	printf("\nSEND DATA, size: %d\n\n", packets_list.size());
	uint32_t size = packets_list.size();
	int i;

	send(c_fd, &size, 4, 0);

	for(i = 0; i < packets_list.size(); i++)
	{
		Packet *p = packets_list[i];
		signed rssi = p->getRssi();
		unsigned time = p->getTime();
		int hash = p->getHash();

		printf("%d - Send: %s %d, %d, %d\n", i, p->getSsid().c_str(), p->getSsid().size(), p->getRssi(), p->getTime());

		size = 18 + p->getSsid().size();
		send(c_fd, &size, 4, 0);
		send(c_fd, (uint8_t *) p->getMac(), 6, 0);
		send(c_fd, (char *) p->getSsid().c_str(), packets_list[i]->getSsid().size(), 0);
		send(c_fd, (signed *) &rssi, sizeof(signed), 0);
		send(c_fd, (unsigned *) &time, sizeof(unsigned), 0);
		send(c_fd, (int *) &hash, sizeof(int), 0);
	}
	printf("%d packets send.", i+1);
	printf("\n");
}

/* Receive from desktop application */
static void socket_receive_data(void)
{
	socket_server_udp_init();

	socklen_t l = sizeof(caddr);
	char address[16];
	char *buffer;

	buffer = (char *)calloc(N, sizeof(char));
	recvfrom(s_fd, buffer, N, 0, (sockaddr *)&caddr, &l);

	inet_ntop(AF_INET, &caddr.sin_addr.s_addr, address, 16);
	printf("Got desktop IP: %s\n", address);

	/* Test send back */
	int err = sendto(s_fd, buffer, N, 0, (sockaddr *) &caddr, l);
	printf("Num byte: %d Send back: %s\n", err, buffer);

	close(s_fd);

	socket_server_tcp_init();
	conn_fd = accept(s_fd, (sockaddr*) &caddr, &l);
	while(true)
	{
		free(buffer);
		buffer = (char *)calloc(N, sizeof(char));
		recv(conn_fd, buffer, N, 0);


		if(strcmp(buffer, "START") == 0)
		{
			printf("Starting..\n");
			break;
		}
		else if(strcmp(buffer, "LED") == 0)
		{
			/* Led */
		}
		else
		{
			printf("Packet format not recognized.\n");

		}
	}

	close(conn_fd);
}

static void socket_synchronize(void)
{
	char *buffer = (char*) calloc(6, sizeof(char));
	socklen_t l = sizeof(caddr);

	conn_fd = accept(s_fd, (sockaddr*) &caddr, &l);
	while(true)
	{
		int ret = recv(conn_fd, buffer, 6, MSG_WAITALL);
		if(ret == -1)
		{
			switch(errno)
			{
				case EBADF:
					printf("Err: EBADF");
					break;
				case ENOTSOCK:
					printf("Err: ENOTSOCK");
					break;
				case EWOULDBLOCK:
					printf("Err: EWOULDBLOCK");
					break;
				case EINTR:
					printf("Err: EINTR");
					break;
				case ENOTCONN:
					printf("Err: ENOTCONN");
					break;
			}
		}
		else
		{
			printf(buffer);
			if(strcmp(buffer, "START") == 0)
			{
				printf("Re - starting..\n");
				break;
			}
			if(strcmp(buffer, "LED") == 0)
			{
				/* Led */
			}
			else
			{
				printf("Packet format not recognized.\n");
			}
		}
	}
	close(conn_fd);
	free(buffer);
}

/* Print MAC address */
static void printMac(uint8_t mac[6])
{
	printf("mac: ");
	for(int i = 0; i < 6; i++)
	{
		if(i != 5)
			printf("%02X:", mac[i]);
		else
			printf("%02X", mac[i]);
	}
}
