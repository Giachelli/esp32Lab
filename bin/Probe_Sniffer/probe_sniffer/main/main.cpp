/*
 * main.cpp
 *
 *  Created on: 02 mag 2018
 *      Author: alberto
 */

extern void probe_sniffer();
extern "C" {
void app_main();
}
void app_main(){
	probe_sniffer();
}


