inherit module

DESCRIPTION = "EVA-kernel driver"
LICENSE          = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-5.10/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS = "bc-native rsync-native mmrm-kernel"
do_configure[depends] += "virtual/kernel:do_shared_workdir"
FILESPATH =+ "${WORKSPACE}/vendor/qcom/opensource/:"
SRC_URI = "file://eva-kernel/"
S = "${WORKDIR}/eva-kernel/"


do_install() {
    install -d ${B}/usr/lib/modules/
    install -Dm0644 ${B}/msm/msm-eva.ko -D ${D}${libdir}/modules/msm-eva.ko
}
FILES_${PN} += "${libdir}/modules*"
