LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Call_System
LOCAL_SRC_FILES := Call_System.cpp

include $(BUILD_SHARED_LIBRARY)
