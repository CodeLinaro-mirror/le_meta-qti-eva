inherit qprebuilt autotools-brokensep pkgconfig
HOMEPAGE         = "https://source.codeaurora.org/"
DESCRIPTION = "Generates qti_eva_kernel headers"

LICENSE          = "GPLv2.0-with-linux-syscall-note"
LIC_FILES_CHKSUM = "file://${QTI_METADATA_ROOT}/meta-qti-bsp/files/common-licenses/${LICENSE};md5=8afb6abdac9a14cb18a0d6c9c151e9b4"

PACKAGE_ARCH    ?= "${MACHINE_ARCH}"

FILESPATH = "${WORKSPACE}/:"
SRC_URI   = "file://vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

S = "${UNPACKDIR}/vendor/qcom/opensource/eva-kernel/include/uapi/eva/media"

ALLOW_EMPTY:${PN} = "1"
