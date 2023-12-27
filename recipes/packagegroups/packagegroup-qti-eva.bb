SUMMARY = "QTI EVA Package Group"

LICENSE = "BSD-3-Clause-Clear"
PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"

PACKAGES = "\
    packagegroup-qti-eva \
    "
RDEPENDS:packagegroup-qti-eva += " \
    eva-kernel \
    "

