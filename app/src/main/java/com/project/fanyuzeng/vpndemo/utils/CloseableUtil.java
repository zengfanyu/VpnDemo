package com.project.fanyuzeng.vpndemo.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author：ZengFanyu .
 * @date：2017/11/25
 */

public class CloseableUtil {
    /**
     * 关闭可关闭的资源
     * @param somethingCanClosed
     */
    public static void closeResource(Closeable... somethingCanClosed) {
        for (Closeable closeable : somethingCanClosed) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
