package com.project.fanyuzeng.vpndemo;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = ExampleUnitTest.class.getSimpleName();

    @Test
    public void addition_isCorrect() throws Exception {
//        InetAddress byName = InetAddress.getByName("www.baidu.com");
//        System.out.println("byName:" + byName);
//        System.out.println("======================");
//        InetAddress[] allByName = InetAddress.getAllByName("www.baidu.com");
//        for (InetAddress address : allByName) {
//            System.out.println("all by name:" + address);
//        }
//        System.out.println("======================");
//        //返回本机 IP
//        InetAddress localHost = InetAddress.getLocalHost();
//        System.out.println("localHost:" + localHost);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);

        ByteBuffer duplicate = buffer.duplicate();

        System.out.println("origin buffer:" + buffer.toString());
        System.out.println("duplicate buffer:" + duplicate.toString());

//        buffer.flip();

        System.out.println("=============put==============");

        buffer.put((byte) 4);

        System.out.println("origin buffer:" + buffer.toString());
        System.out.println("duplicate buffer:" + duplicate.toString());

        buffer.flip();
        System.out.println("=======origin buffer=====");
        while (buffer.hasRemaining()) {
            byte b = buffer.get();

            System.out.println(b);
        }
        duplicate.flip();
        System.out.println("=======duplicate buffer=====");
        while (duplicate.hasRemaining()) {
            byte b = duplicate.get();
            System.out.println(b);
        }
    }
}