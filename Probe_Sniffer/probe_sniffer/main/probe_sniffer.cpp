/*
 * probe_sniffer.cpp
 *
 *  Created on: 01 mag 2018
 *      Author: alberto
 */

using namespace std;

/* Includes */
#include <memory>
#include <vector>
#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"
#include "esp_wifi.h"
#include "esp_log.h"
#include "esp_event_loop.h"
#include "nvs_flash.h"
#include "lwip/err.h"
#include "lwip/sys.h"
#include "lwip/api.h"
#include "lwip/sockets.h"
#include "lwip/netbuf.h"
#include "lwip/netdb.h"
#include "lwip/netifapi.h"
#include "lwip/pppapi.h"
#include "Packet.h"

/* Defines */
#define PROBE_REQ_CTRL 64
#define BEACON_CTRL 128
#define N 20

/* Structs */
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
	uint8_t *ssid;
};

/* Enums */
enum modes {
	SERVER,
	SNIFFER,
	CLIENT
};

/* Functions prototypes */

void probe_sniffer(void);
/* Initializations */
static void wifi_init(void);
static void socket_server_init(void);
static void socket_client_init(void);
static void timer_init(void);
/* Mode change */
static void set_to_sniffer(void);
static void set_to_client(void);
static void set_to_server(void);
/* Handlers */
static esp_err_t event_handler(void *ctx, system_event_t *event);
static void promiscuousCb(void *buffer, wifi_promiscuous_pkt_type_t type);
/* Socket communication */
static void socket_send_data(void);
static void socket_receive_data(void);
/* Utilities */
static void printMac(uint8_t mac[6]);

/* Global variables */
static const char *TAG = "probe_sniffer";
static vector<Packet> packets_list;
static wifi_config_t wifi_config;
static int s_fd, i = 0;
static enum modes mode = SERVER;
struct sockaddr_in caddr;

/* Main */
extern "C" {
void app_main();
}
void app_main(){
	probe_sniffer();
}

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
}

/* Initialize Wi-Fi, called once */
static void wifi_init(void)
{
    tcpip_adapter_init();

    ESP_ERROR_CHECK(esp_event_loop_init(event_handler, NULL));

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    /* Define the AP */
    memset(&wifi_config, 0, sizeof(wifi_config));
    strcpy((char*)wifi_config.sta.ssid, "AndroidAP");
    strcpy((char*)wifi_config.sta.password, "fprl1297");

    printf("\nSystem ready.\n\n");

    /* First set to server mode and wait */
    set_to_server();

    /* Example */
    /*ESP_ERROR_CHECK(esp_wifi_set_promiscuous_rx_cb(&promiscuousCb));
	ESP_ERROR_CHECK(esp_wifi_set_promiscuous(true));
	ESP_ERROR_CHECK(esp_wifi_set_channel(1, WIFI_SECOND_CHAN_ABOVE));

	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));

	is_sniffer = true;
	ESP_ERROR_CHECK(esp_wifi_start());*/
}

/* Initialize server socket */
static void socket_server_init(void)
{
	s_fd = socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(1500);
	addr.sin_addr.s_addr = INADDR_ANY;
	bind(s_fd, (struct sockaddr *)&addr, sizeof(addr));

}

/* Initialize client socket */
static void socket_client_init(void)
{
	s_fd = socket(AF_INET, SOCK_STREAM, 0);
}

static void timer_init(void)
{

}

/* Sniffer mode activated */
static void set_to_sniffer(void)
{
	//ESP_ERROR_CHECK(esp_wifi_stop());
	packets_list.clear();

	ESP_ERROR_CHECK(esp_wifi_set_promiscuous_rx_cb(&promiscuousCb));
	//ESP_ERROR_CHECK(esp_wifi_set_channel(1, WIFI_SECOND_CHAN_ABOVE));
	ESP_ERROR_CHECK(esp_wifi_set_promiscuous(true));

	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));

	mode = SNIFFER;
	//ESP_ERROR_CHECK(esp_wifi_start());
}

/* Server mode activated, called once during initialization */
static void set_to_server(void)
{
	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));

	socket_server_init();
	mode = SERVER;
	ESP_ERROR_CHECK(esp_wifi_start());
}

/* Client mode activated */
static void set_to_client(void)
{
	ESP_ERROR_CHECK(esp_wifi_stop());

	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));

	socket_client_init();
	mode = CLIENT;
	ESP_ERROR_CHECK(esp_wifi_start());
}

/* Handler for sniffer mode */
static void promiscuousCb(void *buffer, wifi_promiscuous_pkt_type_t type)
{
	wifi_pkt_rx_ctrl_t ctrl;
	struct wifi_packet_t *packet;
	Packet p;
	struct ssid_t *ssid;

	ctrl = ((wifi_promiscuous_pkt_t *)buffer)->rx_ctrl;
	packet = (wifi_packet_t*)((wifi_promiscuous_pkt_t *)buffer)->payload;

	if(packet->mac_address.frame_ctrl == PROBE_REQ_CTRL)
	{
		//Is a PROBE REQUEST CONTROL PACKET
		ssid = (ssid_t*) &(packet->payload);

		string s((char *) ssid->ssid, ssid->lenght);
		p.setRssi(ctrl.rssi);
		p.setTime(ctrl.timestamp);
		p.setMac(packet->mac_address.addr2);
		p.setSsid(s);
		printf("PROBE REQUEST - %d - rssi: %d timestamp: %u ", i++, p.getRssi(), p.getTime());
		printMac(packet->mac_address.addr2);
		printf(" ssid: %s l: %d id: %d noise: %d channel: %d\n",
				p.getSsid().c_str(), ssid->lenght, ssid->id, ctrl.noise_floor, ctrl.channel);

		packets_list.insert(packets_list.begin(), p);
	}
	else if(packet->mac_address.frame_ctrl == BEACON_CTRL)
	{

	}

}

/* General handler */
static esp_err_t event_handler(void *ctx, system_event_t *event)
{
    switch (event->event_id) {
        case SYSTEM_EVENT_STA_START:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_START");
            if(mode != SNIFFER)
            	ESP_ERROR_CHECK(esp_wifi_connect());
            break;
        case SYSTEM_EVENT_STA_GOT_IP:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP");
            ESP_LOGI(TAG, "Got IP: %s\n",
			ip4addr_ntoa(&event->event_info.got_ip.ip_info.ip));
            if(mode == SERVER)
			{
				socket_receive_data();
			}
			else if (mode == CLIENT)
			{
				socket_send_data();
			}
			close(s_fd);
			timer_init();
			set_to_sniffer();
            break;
        /*case SYSTEM_EVENT_STA_DISCONNECTED:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_DISCONNECTED");
            ESP_ERROR_CHECK(esp_wifi_connect());
            break;*/
        case SYSTEM_EVENT_STA_CONNECTED:
			ESP_LOGI(TAG, "SYSTEM_EVENT_STA CONNECTED");
			break;
        default:
            break;
    }
    return ESP_OK;
}

/* Send data to the desktop application */
static void socket_send_data(void)
{

}

/* Receive from desktop application */
static void socket_receive_data(void)
{
	socklen_t l = sizeof(caddr);
	char address[16];
	char buffer[N];

	recvfrom(s_fd, buffer, N, 0, (sockaddr *)&caddr, &l);

	inet_ntop(AF_INET, &caddr.sin_addr.s_addr, address, 16);
	printf("Got desktop IP: %s\n", address);

	/* Test send back */
	int err = sendto(s_fd, buffer, N, 0, (sockaddr *) &caddr, l);
	printf("num byte: %d Send back: %s\n", err, buffer);
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
