inherit module deploy

DESCRIPTION = "QTI EVA driver"
LICENSE = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-6.1/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PR = "r0"

DEFAULT_PREFERENCE = "-1"

DEPENDS = "bc-native rsync-native qti-eva-kernel-headers kernel-module-mmrm-kernel mmrm-kernel"
DEPENDS += "synx-kernel-header synx-kernel kernel-module-synx-kernel fastrpc-kernel kernel-module-fastrpc-kernel"

DEPENDS += "virtual/kernel-toolchain-native"
DEPENDS:append:aarch64 = " libgcc"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

RPROVIDES:${PN} += "kernel-module-msm-eva-${KERNEL_VERSION}"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://vendor/qcom/opensource/eva-kernel/"
SRC_URI += "file://vendor/qcom/opensource/eva-kernel/eva.rules"
SRC_URI += "file://${BASEMACHINE}/start_eva_le"
SRC_URI += "file://${BASEMACHINE}/eva.service"

S = "${WORKDIR}/vendor/qcom/opensource/eva-kernel/"

EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"
EXTRA_OEMAKE += "EXTRA_CFLAGS+=-I${STAGING_INCDIR}"

EXTRA_OEMAKE += "WORKDIR=${WORKDIR}"
EXTRA_OEMAKE += "KERNEL_VERSION=${KERNEL_VERSION}"

EXTRA_OEMAKE += "M=${S}"
EXTRA_OEMAKE += "TARGET=${MACHINE}"

# Disable parallel make
PARALLEL_MAKE = "-j1"

KERNEL_MODULES = "msm-eva"

KERNEL_PATH = "${STAGING_KERNEL_BUILDDIR}"

KERNEL_CC = "${STAGING_BINDIR_NATIVE}/clang/bin/clang -target ${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}"

MAKE_OPTIONS += "-I${S}/include/uapi/eva/"

MAKE_TARGETS = "modules"

do_install() {
    install -d ${D}${sysconfdir}/initscripts
    install -m 755 ${WORKDIR}/${BASEMACHINE}/start_eva_le ${D}${sysconfdir}/initscripts
    install -d ${D}${systemd_unitdir}/system
    install -d ${D}${systemd_unitdir}/system/multi-user.target.wants/
    install -m 0755 ${WORKDIR}/${BASEMACHINE}/eva.service -D ${D}${systemd_unitdir}/system/eva.service
    ln -sf ${systemd_unitdir}/system/eva.service ${D}${systemd_unitdir}/system/multi-user.target.wants/eva.service

    install -d ${D}/usr/lib/modules/
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/eva-kernel/msm/msm-eva.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/msm-eva.ko
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/eva-kernel/Module.symvers -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/Module.symvers
    install -m 0644 ${S}/eva.rules -D ${D}${sysconfdir}/udev/rules.d/eva.rules
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
FILES:${PN} += "${base_libdir}/modules/*"
FILES:${PN} += "${base_libdir}/modules/*/*.ko"
FILES:${PN}-dev += "/usr/include/*"
FILES:${PN} += "${sysconfdir}/*"
FILES:${PN} += "/etc/initscripts/start_eva_le"
FILES:${PN} += "${systemd_unitdir}/system/eva.service"
FILES:${PN} += "${systemd_unitdir}/system/multi-user.target.wants/eva.service"
FILES:${PN} += "${systemd_unitdir}/system/multi-user.target.wants"
do_rm_work[noexec] = "1"