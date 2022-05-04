inherit linux-kernel-base

DESCRIPTION = "EVA-kernel driver"
LICENSE          = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-5.10/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS = "bc-native rsync-native"
do_configure[depends] += "virtual/kernel:do_shared_workdir"
FILESPATH =+ "${WORKSPACE}/vendor/qcom/opensource/:"
SRC_URI = "file://eva-kernel/"
S = "${WORKDIR}/eva-kernel/"

#EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"


# Disable parallel make
PARALLEL_MAKE = "-j1"

do_compile() {
    cd ${WORKSPACE}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform  && \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=../../vendor/qcom/opensource/eva-kernel/ \
    ROOTDIR=${WORKSPACE}/ \
    MODULE_OUT=${WORKDIR}/eva-kernel \
    OUT_DIR=${KERNEL_PREBUILT_PATH} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    ./build/build_module.sh
}
# INSTALL_MODULE_HEADERS=1 \
# KBUILD_EXTRA_SYMBOLS=${STAGING_LIBDIR}/modules/mmrm-driver/Module.symvers

do_install() {
    install -d ${D}/usr/lib/modules/
    install -Dm0644 ${B}/msm/msm-eva.ko -D ${D}/usr/lib/modules/msm-eva.ko
}
FILES_${PN} += "${libdir}/modules*"
