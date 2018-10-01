/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "gpio_jni native.cpp"
//#include <utils/Log.h>

#include <android/log.h>

#define  ALOGD(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <cerrno>
#include <string.h>
#include <sys/ioctl.h>

#include "jni.h"
#include "cutils/properties.h"

#include "rk_pin_array_ctrl.h"

#define LOCAL_DEBUG (1)

#define GPIO_DEV "/dev/rk_pac"

struct s_rk_pac_pin {
	char * name;
	int pin;
};

static struct s_rk_pac_pin pins[RK_PAC_PIN_MAX] = {
	{"P5C3", RK_PAC_PIN_3G_PWR},
	{"P5C0", RK_PAC_PIN_3G_RST},
	{"P5C1", RK_PAC_PIN_SIM_SWITCH},
	{"P0B6", RK_PAC_PIN_USB_HOST_1_2},
	{"P0C1", RK_PAC_PIN_USB_HOST_3},
	{"P3B6", RK_PAC_PIN_OUTPUT_1},
	{"P3B7", RK_PAC_PIN_OUTPUT_2},
	{"P7C5", RK_PAC_PIN_HDMI_IN},
	{"P3C1", RK_PAC_PIN_CHARGER},
	{"P0C2", RK_PAC_PIN_DC},
	{"P3B3", RK_PAC_PIN_INPUT_1},
	{"P3B4", RK_PAC_PIN_INPUT_2},
	{"P4D3", RK_PAC_PIN_INPUT_3},
	{"P5C2", RK_PAC_PIN_LED_EN},
	{"P3B5", RK_PAC_PIN_ACC},
};

static int gpio_dev_fd = -1;

static char* rtn = NULL;

static char* jstringTostring(JNIEnv* env, jstring jstr)
{
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*)malloc(alen + 1);

		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);

	return rtn;
}

static jint
add(JNIEnv *env, jobject thiz, jint a, jint b) {
int result = a + b;
    ALOGI("%d + %d = %d", a, b, result);
    return result;
}

/* Return fd */
static jint open_gpio(JNIEnv *env, jobject thiz)
{
	int fd;

	fd = open(GPIO_DEV, O_RDWR);
	if (fd < 0) {
		ALOGE("%s: open %s failed %d", __func__, GPIO_DEV, errno);
	} else {
		gpio_dev_fd = fd;
		if (LOCAL_DEBUG)
			ALOGD("%s: open %s success %d", __func__, GPIO_DEV, gpio_dev_fd);
	}

	return fd;
}

static jint close_gpio(JNIEnv *env, jobject thiz)
{
	if (LOCAL_DEBUG)
		ALOGD("%s: close %d", __func__, gpio_dev_fd);

	if (gpio_dev_fd >= 0) {
		close(gpio_dev_fd);
		gpio_dev_fd = -1;
	}
	return 0;
}

static int get_gpio_data(JNIEnv *env, jobject thiz, jstring gpio_name)
{
	int i;
	char * io_name = jstringTostring(env, gpio_name);
	int value;
	int pin = -1;

	if (gpio_dev_fd < 0) {
		ALOGE("%s: Not open fd", __func__);
		goto err;
	}

	if (!io_name) {
		ALOGE("%s: String is null", __func__);
		goto err;
	}

	if (LOCAL_DEBUG)
		ALOGD("%s: GPIO name: %s", __func__, io_name);

	for (i = 0; i < RK_PAC_PIN_MAX; i++) {
		if (!strncmp(io_name, pins[i].name, strlen(pins[i].name))) {
			pin = pins[i].pin;
			break;
		}
	}

	if (pin < 0) {
		ALOGE("%s: Not found %s", __func__, io_name);
		goto err;
	}

	value = pin;
	if (ioctl(gpio_dev_fd, RK_PAC_IOCTL_CMD_READ, &value) < 0) {
		ALOGE("%s: Not support %s", __func__, io_name);
		goto err;
	}

	if (LOCAL_DEBUG)
		ALOGD("%s: GPIO name: %s, value %d", __func__, io_name, value);

	if (rtn) {
		free(rtn);
		rtn = NULL;
	}

	return value;

err:
	if (rtn) {
		free(rtn);
		rtn = NULL;
	}
	return -1;
}

static int set_gpio_data(JNIEnv *env, jobject thiz, jstring gpio_name, int value)
{
	int i;
	char * io_name = jstringTostring(env, gpio_name);
	int pin = -1;
	int cmd;

	if (gpio_dev_fd < 0) {
		ALOGE("%s: Not open fd", __func__);
		goto err;
	}

	if (!io_name) {
		ALOGE("%s: String is null", __func__);
		goto err;
	}

	if (LOCAL_DEBUG)
		ALOGD("%s: GPIO name: %s", __func__, io_name);

	for (i = 0; i < RK_PAC_PIN_MAX; i++) {
		if (!strncmp(io_name, pins[i].name, strlen(pins[i].name))) {
			pin = pins[i].pin;
			break;
		}
	}

	if (LOCAL_DEBUG)
		ALOGD("%s: Pin: %d", __func__, pin);

	if (pin < 0) {
		ALOGE("%s: Not found %s", __func__, io_name);
		goto err;
	}

	if (value) {
		cmd = RK_PAC_IOCTL_CMD_SET_ENABLE;
	} else {
		cmd = RK_PAC_IOCTL_CMD_SET_DISABLE;
	}

	if (pin == RK_PAC_PIN_SIM_SWITCH) {
		if (cmd == RK_PAC_IOCTL_CMD_SET_ENABLE) {
			property_set("persist.sys.rk_pac_sim_gpio", "true");
		} else {
			property_set("persist.sys.rk_pac_sim_gpio", "false");
		}
	} else {
		if (ioctl(gpio_dev_fd, cmd, &pin) < 0) {
			ALOGE("%s: Not support %s", __func__, io_name);
			goto err;
		}
	}

	if (rtn) {
		free(rtn);
		rtn = NULL;
	}

	return 0;

err:
	if (rtn) {
		free(rtn);
		rtn = NULL;
	}
	return -1;
}

static const char *classPathName = "com/wzh/yho_gpio_operate/gpio_info";

static JNINativeMethod methods[] = {
//  {"add", "(II)I", (void*)add },
  {"open_gpio", "()I", (void*)open_gpio },
  {"close_gpio", "()I", (void*)close_gpio },
  {"get_gpio_data", "(Ljava/lang/String;)I", (void*)get_gpio_data },
  {"set_gpio_data", "(Ljava/lang/String;I)I", (void*)set_gpio_data },
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env)
{
  if (!registerNativeMethods(env, classPathName,
                 methods, sizeof(methods) / sizeof(methods[0]))) {
    return JNI_FALSE;
  }

  return JNI_TRUE;
}


// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */
 
typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    
    ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("ERROR: registerNatives failed");
        goto bail;
    }
    
    result = JNI_VERSION_1_4;
    
bail:
    return result;
}
