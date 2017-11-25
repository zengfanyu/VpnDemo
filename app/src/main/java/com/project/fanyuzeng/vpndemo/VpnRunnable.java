package com.project.fanyuzeng.vpndemo;

import android.util.Log;

import com.project.fanyuzeng.vpndemo.utils.ByteBufferUtil;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author: fanyuzeng on 2017/11/24 20:01
 * @desc:
 */
public class VpnRunnable implements Runnable {
    private static final String TAG = "VpnRunnable";
    private FileDescriptor mFileDescriptor;

    public VpnRunnable(FileDescriptor fileDescriptor) {
        mFileDescriptor = fileDescriptor;
    }

    @Override
    public void run() {
        Log.d(TAG, ">> run >> " + "VpnRunnable start!");
        FileChannel vpnInChannel = new FileInputStream(mFileDescriptor).getChannel();
        FileChannel vpnOutChannle = new FileOutputStream(mFileDescriptor).getChannel();

        ByteBuffer sentToNetBuffer = null;
        boolean hasDataSent = true;
        while (!Thread.interrupted()) {
            if (hasDataSent) {
                sentToNetBuffer = ByteBufferUtil.getByteBuffer();
            } else {
                sentToNetBuffer.clear();
            }

            try {
                int readBytes = vpnInChannel.read(sentToNetBuffer);
                Log.d(TAG, ">> run >> " + "readByte:" + readBytes + "writeByte:" + readBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
