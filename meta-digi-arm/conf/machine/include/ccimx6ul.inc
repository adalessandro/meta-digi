#@TYPE: Machine
#@NAME: ConnectCore for i.MX6UL.
#@DESCRIPTION: Machine configuration for Digi's ConnectCore 6UL.

include conf/machine/include/imx-digi-base.inc
include conf/machine/include/tune-cortexa7.inc

DIGI_FAMILY = "ccimx6ul"
SOC_FAMILY  = "mx6ul:${DIGI_FAMILY}"

# Platform u-boot settings
UBOOT_SUFFIX = "imx"
UBOOT_SYMLINK = "u-boot-${MACHINE}.${UBOOT_SUFFIX}"

# Wireless external module
WIRELESS_MODULE ?= ""

# Firmware
MACHINE_FIRMWARE ?= ""

MACHINE_EXTRA_RDEPENDS += " \
    mtd-utils-ubifs \
"
MACHINE_EXTRA_RRECOMMENDS += "${MACHINE_FIRMWARE} ${WIRELESS_MODULE}"
MACHINE_EXTRA_RRECOMMENDS += "fsl-alsa-plugins"

MACHINE_FEATURES += "wifi bluetooth"

KERNEL_IMAGETYPE = "zImage"
# FLASH parameters
MKUBIFS_ARGS ?= "-m 2048 -e 126976 -c 2047"