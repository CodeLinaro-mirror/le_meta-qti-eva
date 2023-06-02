inherit linux-kernel-base deploy

DESCRIPTION = "QTI EVA driver"
LICENSE = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-5.15/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
PR = "r0"

#DEPENDS = "bc-native rsync-native mmrm-kernel"
DEPENDS = "bc-native rsync-native"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://vendor/qcom/opensource/eva-kernel/ \
file://${BASEMACHINE}/eva_load.conf"
SRC_URI +=  "file://kernel-5.15/kernel_platform"
SRC_URI +=  "file://kernel-5.15/out/${KERNEL_DEFCONFIG}"


S = "${WORKDIR}/vendor/qcom/opensource/eva-kernel/"
KERNEL_VERSION = "${@oe.utils.read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"
EXTRA_OEMAKE += "TARGET_SUPPORT=${BASEMACHINE}"

# Disable parallel make
PARALLEL_MAKE = "-j1"

do_compile() {
    echo "WORKDIR path -> " ${WORKDIR}
    echo "D path -> " ${D}
    echo "base_libdir path -> " ${base_libdir}
    echo "STAGING_DIR_HOST path -> " ${STAGING_DIR_HOST}
    echo "KERNEL_VERSION -> " ${KERNEL_VERSION}
    echo "COMPONENTS_DIR -> " ${COMPONENTS_DIR}
    echo "MMRM SYMVERS PATH -> " ${COMPONENTS_DIR}/aarch64/mmrm-kernel${base_libdir}/modules/${KERNEL_VERSION}/
    cd ${WORKDIR}/kernel-${PREFERRED_VERSION_linux-msm}/kernel_platform  && \
    BUILD_CONFIG=msm-kernel/${KERNEL_CONFIG} \
    EXT_MODULES=../../vendor/qcom/opensource/eva-kernel/ \
    ROOTDIR=${WORKDIR}/ \
    MODULE_OUT=${WORKDIR}/vendor/qcom/opensource/eva-kernel \
    OUT_DIR=${WORKDIR}/kernel-5.15/out/${KERNEL_DEFCONFIG} \
    KERNEL_UAPI_HEADERS_DIR=${STAGING_KERNEL_BUILDDIR} \
    ./build/build_module.sh \
    KBUILD_EXTRA_SYMBOLS=${STAGING_DIR_HOST}/lib/modules/${KERNEL_VERSION}/Module.symvers
    KBUILD_EXTRA_SYMBOLS+=${COMPONENTS_DIR}/aarch64/mmrm-kernel${base_libdir}/modules/${KERNEL_VERSION}/Module.symvers
}

do_install() {
    install -d ${D}/usr/lib/modules/
    install -m 0644 ${WORKDIR}/${BASEMACHINE}/eva_load.conf -D ${D}${sysconfdir}/modules-load.d/eva_load.conf
    install -m 0644 ${WORKDIR}/vendor/qcom/opensource/eva-kernel/msm/msm-eva.ko -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/msm-eva.ko
    install -m 0755 ${WORKDIR}/vendor/qcom/opensource/eva-kernel/Module.symvers  -D ${D}${base_libdir}/modules/${KERNEL_VERSION}/Module.symver
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
#FILES_${PN} += "${base_libdir}/modules/*"
FILES:${PN} += "${base_libdir}/modules/*/*.ko"
FILES:${PN}-dev += "/usr/include/*"
do_rm_work[noexec] = "1"
