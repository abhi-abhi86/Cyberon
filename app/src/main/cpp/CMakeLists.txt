cmake_minimum_required(VERSION 3.18.1)

project("cyberon")

add_library(
        native-lib
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)

# zlib is usually available on Android
find_library(
        z-lib
        z)

target_link_libraries(
        native-lib
        ${log-lib}
        ${z-lib})
