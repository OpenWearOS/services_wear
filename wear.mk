# OpenWearOS: include from a watch product with
#   $(call inherit-product, <path-to>/wear/wear.mk)
PRODUCT_CHARACTERISTICS := watch
PRODUCT_PACKAGES += services.wear
PRODUCT_SYSTEM_SERVER_JARS += platform:services.wear
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.type.watch.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.type.watch.xml
PRODUCT_PACKAGES += WearResources  # OpenWearOS
