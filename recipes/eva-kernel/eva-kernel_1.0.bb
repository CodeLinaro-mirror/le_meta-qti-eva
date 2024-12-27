inherit linux-kernel-base deploy

DESCRIPTION = "QTI EVA driver"
LICENSE = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-6.1/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
PR = "r0"

DEPENDS = "bc-native rsync-native mmrm-kernel"
DEPENDS += "synx-kernel synx-kernel-header fastrpc-kernel"

do_compile[depends] += "virtual/kernel:do_shared_workdir"
do_compile[cleandirs] += "${WORKDIR}/out/${KERNEL_DEFCONFIG}"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://vendor/qcom/opensource/eva-kernel/ \
file://${BASEMACHINE}/eva_load.conf"
SRC_URI += "file://vendor/qcom/opensource/eva-kernel/cvp.rules"
SRC_URI += "file://${BASEMACHINE}/start_eva_le"
SRC_URI += "file://${BASEMACHINE}/eva.service"
S = "${WORKDIR}/vendor/qcom/opensource/eva-kernel/"

KERNEL_VERSION = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"
EXT_MODULES = "${@os.path.relpath("${S}","${KERNEL_PLATFORM_PATH}")}"
EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

# Disable parallel make
PARALLEL_MAKE = "-j1"

do_compile() {
    cd ${KERNEL_PLATFORM_PATH}
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
                 EXT_MODULES=${EXT_MODULES} \
                 KERNEL_KIT=${KERNEL_PREBUILT_PATH} \
                 OUT_DIR=${WORKDIR}/out/${KERNEL_DEFCONFIG} \
                 MODULE_OUT=${S} \
                 INPLACE_COMPILE=y \
                 MODULE_SYNX=y \
                 MODULE_IPCLITE=y \
                 ./build/build_module.sh \
                 KERNEL_HEADER+=-I${WORKDIR}/recipe-sysroot/usr/include \
                 KERNEL_HEADER+=-I${WORKDIR}/recipe-sysroot/usr/include/linux \
                 KERNEL_HEADER+=-I${S}/msm/eva \
                 KBUILD_EXTRA_SYMBOLS=${WORKDIR}/recipe-sysroot/lib/modules/${KERNEL_VERSION}/fastrpc-kernel/Module.symvers \
                 KBUILD_EXTRA_SYMBOLS+=${WORKDIR}/recipe-sysroot/lib/modules/${KERNEL_VERSION}/synx-kernel/Module.symvers \
                 KBUILD_EXTRA_SYMBOLS+=${STAGING_DIR_HOST}/lib/modules/${KERNEL_VERSION}/mmrm-kernel/Module.symvers
}


do_install() {
    install -d ${D}${sysconfdir}/initscripts
    install -m 755 ${WORKDIR}/${BASEMACHINE}/start_eva_le ${D}${sysconfdir}/initscripts
    install -d ${D}${systemd_unitdir}/system
    install -d ${D}${systemd_unitdir}/system/multi-user.target.wants/
    install -m 0755 ${WORKDIR}/${BASEMACHINE}/eva.service -D ${D}${systemd_unitdir}/system/eva.service
    ln -sf ${systemd_unitdir}/system/eva.service ${D}${systemd_unitdir}/system/multi-user.target.wants/eva.service

    install -d ${D}${base_libdir}/modules/
    install -m 0644 ${WORKDIR}/${BASEMACHINE}/eva_load.conf -D ${D}${sysconfdir}/modules-load.d/eva_load.conf
    install -m 0644 ${S}/msm/msm-eva.ko -D ${D}${libdir}/modules/msm-eva.ko
    install -m 0755 ${S}/Module.symvers  -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/eva-kernel/Module.symver
    install -m 0644 ${S}/cvp.rules -D ${D}${sysconfdir}/udev/rules.d/cvp.rules
}

do_deploy() {
    # Deploy unstripped kernel modules into ${DEPLOYDIR}/kernel_modules for debugging purposes
    install -d ${DEPLOYDIR}/kernel_modules
    for kmod in $(find ${D} -name "*.ko") ; do
        install -m 0644 $kmod ${DEPLOYDIR}/kernel_modules
    done
}

addtask deploy after do_install before do_package

INSANE_SKIP:${PN} += "installed-vs-shipped"
FILES:${PN}-dev += "/usr/include/*"
FILES:${PN} += "${libdir}/modules/*"
FILES:${PN} += "/etc/initscripts/start_eva_le"
FILES:${PN} += "${systemd_unitdir}/system/eva.service"
FILES:${PN} += "${systemd_unitdir}/system/multi-user.target.wants/eva.service"
FILES:${PN} += "${systemd_unitdir}/system/multi-user.target.wants"
do_rm_work[noexec] = "1"
