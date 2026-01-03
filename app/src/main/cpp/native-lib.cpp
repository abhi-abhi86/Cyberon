#include <jni.h>
#include <string>
#include <fstream>
#include <vector>
#include <iomanip>
#include <sstream>
#include <zlib.h> // For crc32

// Helper to calculate CRC32 of a file
std::string calculateCRC32(const std::string& filePath) {
    std::ifstream file(filePath, std::ios::binary);
    if (!file.is_open()) {
        return "Error: Could not open file";
    }

    const size_t bufferSize = 4096;
    std::vector<char> buffer(bufferSize);
    uLong crc = crc32(0L, Z_NULL, 0);

    while (file.read(buffer.data(), bufferSize)) {
        crc = crc32(crc, (const Bytef*)buffer.data(), file.gcount());
    }
    // Process remaining bytes
    if (file.gcount() > 0) {
        crc = crc32(crc, (const Bytef*)buffer.data(), file.gcount());
    }

    std::stringstream ss;
    ss << std::hex << std::uppercase << std::setw(8) << std::setfill('0') << crc;
    return ss.str();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_cyberon_MainActivity_calculateChecksumFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jstring filePath) {
    
    const char *pathChars = env->GetStringUTFChars(filePath, 0);
    std::string path(pathChars);
    env->ReleaseStringUTFChars(filePath, pathChars);

    std::string checksum = calculateCRC32(path);

    return env->NewStringUTF(checksum.c_str());
}
