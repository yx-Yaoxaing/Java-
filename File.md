# Java-
打怪升级
package com.yx.code;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * @author yaoxiang
 * @date 2021-06-27 15:15
 */
public class ClassTest {
    public static void main(String[] args) throws Exception {
        File file = new File("F:\\static\\studentregister\\md.txt");
        // 字节
        long length = file.length();
        System.out.println(length);
        FileInputStream inputStream = new FileInputStream(file);
        // 将字节流的每一个字节 read（）方法会读取每一个字节  将字节写入byte数组
        byte[] bytes = new byte[(int) file.length()];
        System.out.println("[inputStream] : " + inputStream.toString());
        // 每一次的read都会读取一个字节 a 97，当read返回的值为-1的时候 代表字节流中的数据已经读取完
        //
        // System.out.println(inputStream.read());
        //
        int num ;
        // 当read读取的时候
        // abcde姚祥aaa
        // abcde 一个字节一个字母 中文是三个字节一个中文
        // 97
        //98
        //99
        //100
        //101
        //姚祥
        //229
        //167
        //154
        //231
        //165
        //165
        // aaa
        //97
        //97
        //97
        int a;
        String s = null;
        while ((a=inputStream.read(bytes)) != -1){
            char[] chars = new char[1024];
            System.out.println(new String(bytes, 0, a));
        }
        System.out.println(Arrays.toString(bytes));
    }
}



/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "jni.h"
#include "jni_util.h"

extern jfieldID IO_fd_fdID;
extern jfieldID IO_handle_fdID;

#ifdef _ALLBSD_SOURCE
#include <fcntl.h>
#ifndef O_SYNC
#define O_SYNC  O_FSYNC
#endif
#ifndef O_DSYNC
#define O_DSYNC O_FSYNC
#endif
#elif !defined(O_DSYNC) || !defined(O_SYNC)
#define O_SYNC  (0x0800)
#define O_DSYNC (0x2000)
#endif

/*
 * IO helper functions
 */

jint readSingle(JNIEnv *env, jobject this, jfieldID fid);
jint readBytes(JNIEnv *env, jobject this, jbyteArray bytes, jint off,
               jint len, jfieldID fid);
void writeSingle(JNIEnv *env, jobject this, jint byte, jboolean append, jfieldID fid);
void writeBytes(JNIEnv *env, jobject this, jbyteArray bytes, jint off,
                jint len, jboolean append, jfieldID fid);
void fileOpen(JNIEnv *env, jobject this, jstring path, jfieldID fid, int flags);
void throwFileNotFoundException(JNIEnv *env, jstring path);
size_t getLastErrorString(char *buf, size_t len);

/*
 * Macros for managing platform strings.  The typical usage pattern is:
 *
 *     WITH_PLATFORM_STRING(env, string, var) {
 *         doSomethingWith(var);
 *     } END_PLATFORM_STRING(env, var);
 *
 *  where  env      is the prevailing JNIEnv,
 *         string   is a JNI reference to a java.lang.String object, and
 *         var      is the char * variable that will point to the string,
 *                  after being converted into the platform encoding.
 *
 * The related macro WITH_FIELD_PLATFORM_STRING first extracts the string from
 * a given field of a given object:
 *
 *     WITH_FIELD_PLATFORM_STRING(env, object, id, var) {
 *         doSomethingWith(var);
 *     } END_PLATFORM_STRING(env, var);
 *
 *  where  env      is the prevailing JNIEnv,
 *         object   is a jobject,
 *         id       is the field ID of the String field to be extracted, and
 *         var      is the char * variable that will point to the string.
 *
 * Uses of these macros may be nested as long as each WITH_.._STRING macro
 * declares a unique variable.
 */

#define WITH_PLATFORM_STRING(env, strexp, var)                                \
    if (1) {                                                                  \
        const char *var;                                                      \
        jstring _##var##str = (strexp);                                       \
        if (_##var##str == NULL) {                                            \
            JNU_ThrowNullPointerException((env), NULL);                       \
            goto _##var##end;                                                 \
        }                                                                     \
        var = JNU_GetStringPlatformChars((env), _##var##str, NULL);           \
        if (var == NULL) goto _##var##end;

#define WITH_FIELD_PLATFORM_STRING(env, object, id, var)                      \
    WITH_PLATFORM_STRING(env,                                                 \
                         ((object == NULL)                                    \
                          ? NULL                                              \
                          : (*(env))->GetObjectField((env), (object), (id))), \
                         var)

#define END_PLATFORM_STRING(env, var)                                         \
        JNU_ReleaseStringPlatformChars(env, _##var##str, var);                \
    _##var##end: ;                                                            \
    } else ((void)NULL)


/* Macros for transforming Java Strings into native Unicode strings.
 * Works analogously to WITH_PLATFORM_STRING.
 */

#define WITH_UNICODE_STRING(env, strexp, var)                                 \
    if (1) {                                                                  \
        const jchar *var;                                                     \
        jstring _##var##str = (strexp);                                       \
        if (_##var##str == NULL) {                                            \
            JNU_ThrowNullPointerException((env), NULL);                       \
            goto _##var##end;                                                 \
        }                                                                     \
        var = (*(env))->GetStringChars((env), _##var##str, NULL);             \
        if (var == NULL) goto _##var##end;

#define END_UNICODE_STRING(env, var)                                          \
        (*(env))->ReleaseStringChars(env, _##var##str, var);                  \
    _##var##end: ;                                                            \
    } else ((void)NULL)
