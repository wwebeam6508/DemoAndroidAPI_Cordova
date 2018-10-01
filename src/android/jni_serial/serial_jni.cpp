#define LOG_TAG "SerialJni"

#include <android/log.h>
#include <unistd.h>

//#include <utils/Log.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <cerrno>
#include <string.h>
#include <termios.h>
#include <com_jhxd_serial_serialService.h>

#define  ALOGD(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

char* rtn = NULL;


static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate)
    {
		case 1200: return B1200;
		case 2400: return B2400;
		case 4800: return B4800;
		case 9600: return B9600;
		case 19200: return B19200;
		case 38400: return B38400;
		case 57600: return B57600;
		case 115200: return B115200;
		default: return -1;
    }
}

static int set_opt(int fd,int nspeed,int nbits,int nevent,int nstop)
{
	int ret = 0;
	struct termios newtio;
	ALOGE("set_opt----------------------->:%d, %d, %d, %d", nspeed, nbits, nevent, nstop);
	memset(&newtio,0,sizeof(newtio));
	newtio.c_cflag |= CLOCAL|CREAD;
	newtio.c_cflag &= ~CSIZE;
	switch(nbits)
	{
		case 7:
			newtio.c_cflag |= CS7;
			break;
		case 8:
			newtio.c_cflag |= CS8;
			break;
	}
	switch(nevent)
	{
		case 0:
			newtio.c_cflag &= ~PARENB;
			break;
		case 1:
			newtio.c_cflag |= PARENB;
			newtio.c_cflag |= PARODD;
			newtio.c_iflag |= (INPCK|ISTRIP);
			break;
		case 2:
			newtio.c_iflag |= (INPCK|ISTRIP);
			newtio.c_cflag |= PARENB;
			newtio.c_cflag &= ~PARODD;
			break;
	}

	speed_t speed = getBaudrate(nspeed);

	 if (speed == -1)
	 {
		ALOGE("Invalid baudrate");
		return -1;
	 }

	cfsetispeed(&newtio, speed);
	cfsetospeed(&newtio, speed);


	if(nstop == 1)
	{
		newtio.c_cflag&= ~CSTOPB;
	}
	else if(nstop == 2)
	{
		newtio.c_cflag |= CSTOPB;
	}



//	newtio.c_cc[VTIME] = 0;
//	newtio.c_cc[VMIN] = 1;

	tcflush(fd,TCIFLUSH);
	if((tcsetattr(fd,TCSANOW,&newtio))!= 0)
	{
		//perror("corm set error");
		ALOGE("function:%s com set error:%s",__FUNCTION__,strerror(errno));
		return -1;
	}

	ALOGD("set done!\r\n");
	return 0;
}


static jint set_baud(JNIEnv *env, jobject thiz, jint fd, jint nspeed, int nbits,int nevent,int nstop)
{
	if(set_opt(fd,nspeed, nbits, nevent, nstop) <0 ){
		ALOGE("function:%s:set_baud error",__FUNCTION__);
		return -1;
	}
	return 0;
}


static char* jstringTostring(JNIEnv* env, jstring jstr)
{
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = (char*)malloc(alen + 1);

		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);

	return rtn;
}

static jint open_serial(JNIEnv *env, jobject thiz,jstring ttyport)
{
	char *ttyportstring = jstringTostring(env,ttyport);
	ALOGD("open tty is: %s ", ttyportstring);
	int fd = open(ttyportstring, O_RDWR);
	if( fd < 0){
			ALOGE("open %s\n", strerror(errno));
			free(rtn);
			return -1;
	}
	free(rtn);
	return fd;
}


static jint serial_write(JNIEnv *env, jobject thiz,jint fd,jbyteArray bytedata,jint writesize)
{
	int writesuccesssize=0,writecomplete=0;

	bool ifsuccess = false;

	jbyte *writedata = env->GetByteArrayElements(bytedata,0);
	if((writedata==NULL)||(fd<0)||(writesize<0)){
		ALOGE("param errore");
		return -1;
	}

	do{

		writesuccesssize= write(fd,writedata+writecomplete,writesize-writecomplete);
		if(writesuccesssize<0){
			ALOGE("write errore");
			ifsuccess = false;
			break;
		}else{
			ifsuccess = true;
		}

		writecomplete += writesuccesssize;

	}while(writecomplete<writesize);

	env->ReleaseByteArrayElements(bytedata, writedata, 0);
	if(ifsuccess)
		return writesuccesssize;
	else
		return -1;
}

static jint serial_read(JNIEnv *env, jobject thiz,jint fd,jbyteArray readdata,jint readsize)
{

	int rec_size=0,hav_recsize=0;
	jbyte *recvbyte = (jbyte *)env->GetByteArrayElements(readdata, NULL);

	if((recvbyte==NULL)||(fd<0)||(readsize<0)){
			ALOGE("param errore");
			return -1;
	}

	rec_size = read(fd,recvbyte+hav_recsize,readsize-hav_recsize);
	if(rec_size<=0){
		ALOGE("serial read error");
	}

	/*do{
		rec_size= read(fd,recvbyte+hav_recsize,readsize-hav_recsize);
		if(rec_size<=0){
			ALOGE("serial read error");
			break;
		}
		hav_recsize= rec_size+hav_recsize;

	}while(hav_recsize<readsize);
*/
	env->ReleaseByteArrayElements(readdata, recvbyte, 0);

	return rec_size;

}

static jint close_serial(JNIEnv *env, jobject thiz,jint fd)
{
	if(fd < 0){
		ALOGE("fd is error");
		return -1;
	}

	close(fd);
	return 0;
}

static  JNINativeMethod myMethods[] ={
	{"serialOpen", "(Ljava/lang/String;)I", (void*)open_serial},
	{"serialPortSetting", "(IIIII)I", (void*)set_baud},
	{"serialRead", "(I[BI)I", (void*)serial_read },
	{"serialWrite", "(I[BI)I", (void*)serial_write },
	{"serialClose", "(I)I", (void*)close_serial },
	
}; 

jint myRegisterNatives(JNIEnv *env)
{
	ALOGD("%s\n", __FUNCTION__);
	jclass cls = env->FindClass("com/jhxd/serial/serialService");
	jint ret = -1;
	if(cls == NULL)
	{
		ALOGE("FindClass com/jhxd/serial error\n");
		return -1;
	}
	ret = env->RegisterNatives(cls, myMethods, sizeof(myMethods)/sizeof(myMethods[0]));
	if(ret< 0)
	{
		ALOGE("RegisterNatives error\n");
		return -1;
	}
	ALOGD("%s  OK\n", __FUNCTION__);

	return 0;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	ALOGD("%s\n", __FUNCTION__);
	JNIEnv *env = NULL;
	jint ret = -1;
	ret = vm->GetEnv((void **)&env, JNI_VERSION_1_4);

	if(ret != JNI_OK)
	{
		ALOGE("GetEnv error\n");
		return -1;
	}

	ret = myRegisterNatives(env);
	if(ret< 0)
		return -1;

	return JNI_VERSION_1_4;
}
