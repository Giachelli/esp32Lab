/*
 * Packet.h
 *
 *  Created on: 03 mag 2018
 *      Author: alberto
 */
using namespace std;

#ifndef MAIN_PACKET_H_
#define MAIN_PACKET_H_
#include <string>
#include <stdint.h>
#include <cstring>
#include <memory>
#include <ctime>

class Packet {
private:
	uint8_t mac_address[6];
	string ssid;
	signed rssi;
	unsigned hash;
	unsigned time;

public:

	Packet():rssi(0), hash(0), time(0)
	{}
    ~Packet()
    {}

    //Setters
    void setMac(uint8_t mac_address[6])
    {
    	memcpy(this->mac_address, mac_address, 6);
    }
    void setSsid(string ssid)
    {
    	this->ssid = ssid;
    }
    void setRssi(signed rssi)
    {
    	this->rssi = rssi;
    }
    void setHash(unsigned hash)
    {
    	this->hash = hash;
    }
    void setTime(unsigned time)
    {
    	this->time = time;
    }

    //Getters
    uint8_t* getMac(void)
    {
    	return mac_address;
    }
    string getSsid(void)
	{
		return ssid;
	}
    signed getRssi(void)
    {
    	return rssi;
    }
    unsigned getHash(void)
    {
    	return hash;
    }
    unsigned getTime(void)
    {
    	return time;
    }

};

#endif /* MAIN_PACKET_H_ */
