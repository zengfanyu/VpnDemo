package com.project.fanyuzeng.vpndemo.utils;

/**
 * 将有符号类型转换为无符号类型的方法
 * <p>
 * 参考自<a href="http://eric-gcm.iteye.com/blog/1166399">java 中unsigned类型的转换</a>
 *
 * @author：ZengFanyu .
 * @date：2017/11/25
 */
public class UnsignedConvertUtil {
    /**
     * 将data字节型数据转换为0~255 (0xFF 即Byte)
     *
     * @param data 有符号型 byte 数据
     * @return 无符号型 data
     */
    public static short getUnsignedByte(byte data) {
        return (short) (data & 0x0FF);
    }

    /**
     * 将data字节型数据转换为0~65535 (0xFFFF 即 short)
     *
     * @param data 有符号型 short 数据
     * @return 无符号型 data
     */
    public static int getUnsignedShort(short data) {
        return data & 0x0FFFF;
    }

    /**
     * 将int数据转换为0~4294967295 (0xFFFFFFFF即 long)。
     *
     * @param data 有符号型 int 数据
     * @return 无符号型 data
     */
    public static long getUnsignedInt(int data) {
        return data & 0x0FFFFFFFFL;
    }
}
