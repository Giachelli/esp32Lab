/*
 * probe_sniffer.cpp
 *
 *  Created on: 01 mag 2018
 *      Author: alberto
 */
using namespace std;
#include <memory>
#include <vector>
#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"
#include "esp_wifi.h"
#include "esp_log.h"
#include "esp_event_loop.h"
#include "nvs_flash.h"
#include "Packet.h"

#define PROBE_REQ_CTRL 64
#define BEACON_CTRL 128

//static const char *TAG = "probe_sniffer";
static vector<Packet> packets_list;
static int i = 0;

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

void printMac(uint8_t mac[6])
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

void promiscuousCb(void *buffer, wifi_promiscuous_pkt_type_t type)
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
		printf(" ssid: %s %d l: %d id: %d noise: %d channel: %d\n",
				p.getSsid().c_str(), p.getSsid().size(), ssid->lenght, ssid->id, ctrl.noise_floor, ctrl.channel);

		packets_list.insert(packets_list.begin(), p);
	}
	else if(packet->mac_address.frame_ctrl == BEACON_CTRL)
	{

	}

}

static esp_err_t event_handler(void *ctx, system_event_t *event)
{
    /*switch (event->event_id) {
        case SYSTEM_EVENT_STA_START:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_START");
            ESP_ERROR_CHECK(esp_wifi_connect());
            break;
        case SYSTEM_EVENT_STA_GOT_IP:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP");
            ESP_LOGI(TAG, "Got IP: %s\n",
			ip4addr_ntoa(&event->event_info.got_ip.ip_info.ip));
            break;
        case SYSTEM_EVENT_STA_DISCONNECTED:
            ESP_LOGI(TAG, "SYSTEM_EVENT_STA_DISCONNECTED");
            ESP_ERROR_CHECK(esp_wifi_connect());
            break;
        default:
            break;
    }*/
    return ESP_OK;
}


/* Initialize Wi-Fi as sta and set scan method */
static void wifi_init(void)
{
    tcpip_adapter_init();

    //Sender mode
    ESP_ERROR_CHECK(esp_event_loop_init(event_handler, NULL));

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    wifi_config_t wifi_config = {
    	.sta = {
            /*.ssid = 0,
            .password = 0,
            .scan_method = 0,
            .sort_method = WIFI_PS_NONE,
            .threshold.rssi = -100,
            .threshold.authmode = 0,*/
        }
    };

    //Sniffer mode
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());

    /*wifi_promiscuous_filter_t filter;
    filter.filter_mask = WIFI_PROMIS_FILTER_MASK_MGMT;*/

    ESP_ERROR_CHECK(esp_wifi_set_promiscuous_rx_cb(&promiscuousCb));
    ESP_ERROR_CHECK(esp_wifi_set_channel(2, WIFI_SECOND_CHAN_ABOVE));
    //ESP_ERROR_CHECK(esp_wifi_set_promiscuous_filter(&filter));
    ESP_ERROR_CHECK(esp_wifi_set_promiscuous(true));

    printf("System ready..\n\n");
}

void probe_sniffer()
{
    // Initialize NVS
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK( ret );

    wifi_init();
}

extern "C" {
void app_main();
}
void app_main(){
	probe_sniffer();
}


