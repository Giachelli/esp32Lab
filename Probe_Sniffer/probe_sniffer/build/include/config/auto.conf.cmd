deps_config := \
	/home/galax/esp/esp-idf/components/app_trace/Kconfig \
	/home/galax/esp/esp-idf/components/aws_iot/Kconfig \
	/home/galax/esp/esp-idf/components/bt/Kconfig \
	/home/galax/esp/esp-idf/components/driver/Kconfig \
	/home/galax/esp/esp-idf/components/esp32/Kconfig \
	/home/galax/esp/esp-idf/components/esp_adc_cal/Kconfig \
	/home/galax/esp/esp-idf/components/ethernet/Kconfig \
	/home/galax/esp/esp-idf/components/fatfs/Kconfig \
	/home/galax/esp/esp-idf/components/freertos/Kconfig \
	/home/galax/esp/esp-idf/components/heap/Kconfig \
	/home/galax/esp/esp-idf/components/libsodium/Kconfig \
	/home/galax/esp/esp-idf/components/log/Kconfig \
	/home/galax/esp/esp-idf/components/lwip/Kconfig \
	/home/galax/esp/esp-idf/components/mbedtls/Kconfig \
	/home/galax/esp/esp-idf/components/openssl/Kconfig \
	/home/galax/esp/esp-idf/components/pthread/Kconfig \
	/home/galax/esp/esp-idf/components/spi_flash/Kconfig \
	/home/galax/esp/esp-idf/components/spiffs/Kconfig \
	/home/galax/esp/esp-idf/components/tcpip_adapter/Kconfig \
	/home/galax/esp/esp-idf/components/wear_levelling/Kconfig \
	/home/galax/esp/esp-idf/components/bootloader/Kconfig.projbuild \
	/home/galax/esp/esp-idf/components/esptool_py/Kconfig.projbuild \
	/home/galax/esp/probe_sniffer/main/Kconfig.projbuild \
	/home/galax/esp/esp-idf/components/partition_table/Kconfig.projbuild \
	/home/galax/esp/esp-idf/Kconfig

include/config/auto.conf: \
	$(deps_config)


$(deps_config): ;
