inherit qprebuilt autotools-brokensep pkgconfig
HOMEPAGE         = "https://source.codeaurora.org/"
DESCRIPTION = "Generates qti_eva_kernel headers"

LICENSE          = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${WORKSPACE}/kernel-6.1/kernel_platform/msm-kernel/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

PACKAGE_ARCH    ?= "${MACHINE_ARCH}"

DEFAULT_PREFERENCE = "-1"

FILESPATH = "${WORKSPACE}/:"
SRC_URI   = "file://vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

S = "${WORKDIR}/vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

ALLOW_EMPTY:${PN} = "1"