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
