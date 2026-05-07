#include <jni.h>
#include <cmath>
#include <vector>
#include <string>

struct Titik {
    float x, y;
};

std::vector<Titik> tubuhUlar;

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_contoh_ularmanja_Utama_updateLogikaUlar(
        JNIEnv* env, jobject /* this */, 
        jfloat inputX, jfloat inputY, jfloat speed) {
    
    // Nanti logika pertambahan tubuh ular bisa kamu tulis di sini
    // Contoh penggunaan vector:
    // Jika makan buah -> tubuhUlar.push_back({koordinatX, koordinatY});

    float barux = inputX * speed;
    float baruy = inputY * speed;

    float hasil[2] = {barux, baruy};
    jfloatArray out = env->NewFloatArray(2);
    env->SetFloatArrayRegion(out, 0, 2, hasil);
    
    return out;
}