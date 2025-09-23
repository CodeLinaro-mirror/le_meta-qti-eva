inherit qprebuilt autotools-brokensep pkgconfig
HOMEPAGE         = "https://source.codeaurora.org/"
DESCRIPTION = "Generates qti_eva_kernel headers"

LICENSE          = "GPLv2.0-with-linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-6.12/kernel_platform/soc-repo/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PACKAGE_ARCH    ?= "${MACHINE_ARCH}"

FILESPATH = "${WORKSPACE}/:"
SRC_URI   = "file://vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

S = "${WORKDIR}//vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

ALLOW_EMPTY:${PN} = "1"
