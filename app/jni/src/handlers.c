#include <jni.h>
#include <serial.h>
#include "audio.h"
#include <SDL.h>
#include <android/api-level.h>

JNIEXPORT void JNICALL
Java_io_maido_m8client_M8SDLActivity_loop(JNIEnv *env, jobject thiz) {
    audio_loop();
}

JNIEXPORT void JNICALL
Java_io_maido_m8client_M8SDLActivity_connect(JNIEnv *env, jobject thiz,
                                             jint fd, jint audiodevice) {
    if (android_get_device_api_level() > 27) {
        SDL_SetHint(SDL_HINT_AUDIODRIVER, "aaudio");
    }
    set_audio_device(audiodevice);
    set_usb_init_callback(audio_setup);
    set_usb_destroy_callback(audio_destroy);
    init_serial_with_file_descriptor(fd);
}

JNIEXPORT void JNICALL
Java_io_maido_m8client_M8TouchListener_sendClickEvent(JNIEnv *env, jobject thiz, jchar event) {
    send_msg_controller(event);
}

int android_main(int argc, char *argv[]) {
    return SDL_main(argc, argv);
}