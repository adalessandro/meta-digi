# Copyright (C) 2012-2016 Freescale Semiconductor
# Copyright (C) 2018 O.S. Systems Software LTDA.
# Copyright 2017-2019 NXP

SUMMARY = "Freescale IMX firmware"
DESCRIPTION = "Freescale IMX firmware such as for the VPU"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd4b227530cd88a82af6a5982cfb724d"

PE = "1"

SRC_URI = "${FSL_MIRROR}/firmware-imx-${PV}.bin;fsl-eula=true "

SRC_URI[md5sum] = "70bdefdd7fb58f782237acf4c648f5cd"
SRC_URI[sha256sum] = "f04b2deb4f126e6b6cd4460462292acd72183b9f799e3b873fb390ff0f18eb55"

inherit fsl-eula-unpack allarch

do_install() {

    install -d ${D}${base_libdir}/firmware/imx

    cp -rfv firmware/* ${D}${base_libdir}/firmware/

    # Install SDMA Firmware: sdma-imx6q.bin & sdma-imx7d.bin into lib/firmware/imx/sdma
    install -d ${D}${base_libdir}/firmware/imx/sdma
    mv ${D}${base_libdir}/firmware/sdma/sdma-imx6q.bin ${D}${base_libdir}/firmware/imx/sdma
    mv ${D}${base_libdir}/firmware/sdma/sdma-imx7d.bin ${D}${base_libdir}/firmware/imx/sdma

    # No need to do install for ddr & hdmi binaries
    if [ -d ${D}${base_libdir}/firmware/ddr ]; then
        rm -rf ${D}${base_libdir}/firmware/ddr
    fi
    if [ -d ${D}${base_libdir}/firmware/hdmi ]; then
        rm -rf ${D}${base_libdir}/firmware/hdmi
    fi

    # Don't install seco related binary
    if [ -d ${D}${base_libdir}/firmware/seco ]; then
        rm -rf ${D}${base_libdir}/firmware/seco
    fi

    mv ${D}${base_libdir}/firmware/epdc/ ${D}${base_libdir}/firmware/imx/epdc/
    mv ${D}${base_libdir}/firmware/imx/epdc/epdc_ED060XH2C1.fw.nonrestricted ${D}${base_libdir}/firmware/imx/epdc/epdc_ED060XH2C1.fw

    mv ${D}${base_libdir}/firmware/easrc/ ${D}${base_libdir}/firmware/imx/easrc/

    find ${D}${base_libdir}/firmware -type f -exec chmod 644 '{}' ';'
    find ${D}${base_libdir}/firmware -type f -exec chown root:root '{}' ';'

    # Remove files not going to be installed
    find ${D}${base_libdir}/firmware/ -name '*.mk' -exec rm '{}' ';'
}

IS_MX8 = "0"
IS_MX8_mx8m = "8m"
IS_MX8_mx8qm = "8qm"
IS_MX8_mx8qxp = "8qx"
inherit deploy
addtask deploy before do_build after do_install
do_deploy () {
    # Deploy i.MX8 related firmware files
    if [ "${IS_MX8}" = "8qm" ]; then
        # Deploy hdmi/cadence
        install -m 0644 ${S}/firmware/hdmi/cadence/hdmitxfw.bin ${DEPLOYDIR}
        install -m 0644 ${S}/firmware/hdmi/cadence/hdmirxfw.bin ${DEPLOYDIR}
        install -m 0644 ${S}/firmware/hdmi/cadence/dpfw.bin ${DEPLOYDIR}

    fi
}

python populate_packages_prepend() {
    vpudir = bb.data.expand('${base_libdir}/firmware/vpu', d)
    do_split_packages(d, vpudir, '^vpu_fw_([^_]*).*\.bin',
                      output_pattern='firmware-imx-vpu-%s',
                      description='Freescale IMX Firmware %s',
                      extra_depends='',
                      prepend=True)
    sdmadir = bb.data.expand('${base_libdir}/firmware/sdma', d)
    do_split_packages(d, sdmadir, '^sdma-([^-]*).*\.bin',
                      output_pattern='firmware-imx-sdma-%s',
                      description='Freescale IMX Firmware %s',
                      extra_depends='',
                      prepend=True)
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES_DYNAMIC = "${PN}-vpu-* ${PN}-sdma-*"

PACKAGES =+ "${PN}-epdc ${PN}-scfw ${PN}-sdma ${PN}-easrc"

FILES_${PN}-epdc = "${base_libdir}/firmware/imx/epdc/"
FILES_${PN}-scfw = "${base_libdir}/firmware/scfw/"
FILES_${PN}-sdma = " ${base_libdir}/firmware/imx/sdma"
FILES_${PN}-easrc = "${base_libdir}/firmware/imx/easrc/"
