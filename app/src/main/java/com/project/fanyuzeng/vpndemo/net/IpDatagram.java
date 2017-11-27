package com.project.fanyuzeng.vpndemo.net;

import android.util.Log;

import com.project.fanyuzeng.vpndemo.utils.UnsignedConvertUtil;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import static com.project.fanyuzeng.vpndemo.net.Ipv4Header.IPV4_HEADER_SISE;
import static com.project.fanyuzeng.vpndemo.net.Ipv4Header.TCP_PROTOCOL_NUM;
import static com.project.fanyuzeng.vpndemo.net.Ipv4Header.UDP_PROTOCOL_NUM;
import static com.project.fanyuzeng.vpndemo.net.TcpHeader.TCP_HEADER_SISE;
import static com.project.fanyuzeng.vpndemo.net.UdpHeader.UDP_HEADER_SISE;

/**
 * 封装IP数据报,其中包含 IP数据报首部,TCP或者UDP报文段首部
 *
 * @author: fanyuzeng on 2017/11/27 11:41
 */
public class IpDatagram {
    private static final String TAG = IpDatagram.class.getSimpleName();

    private Ipv4Header mIpv4Header;
    private TcpHeader mTcpHeader;
    private UdpHeader mUdpHeader;
    private ByteBuffer mBuffer;

    private boolean isTCP;
    private boolean isUDP;

    public IpDatagram(ByteBuffer byteBuffer) {
        mBuffer = byteBuffer;
        mIpv4Header = new Ipv4Header(byteBuffer);
        if (mIpv4Header.getProtocolNum() == TCP_PROTOCOL_NUM) {
            isTCP = true;
            isUDP = false;
            mTcpHeader = new TcpHeader(mBuffer);
            Log.d(TAG, ">> IpDatagram >> " + "TCP and TcpHeader construct");
        } else if (mIpv4Header.getProtocolNum() == UDP_PROTOCOL_NUM) {
            isTCP = false;
            isUDP = true;
            mUdpHeader = new UdpHeader(mBuffer);
            Log.d(TAG, ">> IpDatagram >> " + "UDP and UdpHeader consruct");
        }
    }

    public void constructHeader(ByteBuffer buffer) {
        mIpv4Header.consructIPv4Buffer(buffer);
        if (isTCP) {
            mTcpHeader.constructTcpHeader(buffer);
        } else if (isUDP) {
            mUdpHeader.constructUdpHeader(buffer);
        }
    }

    public boolean isTCP() {
        return isTCP;
    }

    public boolean isUDP() {
        return isUDP;
    }

    /**
     * 交换IP数据报中源地址和目的地址,以及传输层协议中的源端口和目的端口
     */
    public void swapSourceAndDestinationPort() {
        InetAddress tempAddress;
        tempAddress = mIpv4Header.getSourceAddress();
        mIpv4Header.setSourceAddress(mIpv4Header.getDestinationAddress());
        mIpv4Header.setDestinationAddress(tempAddress);

        if (isTCP) {
            int tempPort = mTcpHeader.getSourcePort();
            mTcpHeader.setSourcePort(mTcpHeader.getDestinationPort());
            mTcpHeader.setDestinationPort(tempPort);
        } else if (isUDP) {
            int tempPort = mUdpHeader.getSourcePort();
            mUdpHeader.setSourcePort(mUdpHeader.getDestinationPort());
            mUdpHeader.setDestinationPort(tempPort);
        }

    }

    /**
     * 用服务器端发送来的数据(ByteBuffer)更新本地数据(ByteBuffer)
     *
     * @param buffer
     * @param flags
     * @param seqNum
     * @param ackNum
     * @param payloadSize
     * @see <a href="http://upload-images.jianshu.io/upload_images/1055199-9819cc4ab70a6570.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">三次握手,四次挥手示意图</a>
     */
    public void updateTcpBuffer(ByteBuffer buffer, byte flags, long seqNum, long ackNum, int payloadSize) {
        // TODO: 2017/11/27  
    }

    /**
     * 用服务器端发送来的数据(ByteBuffer)更新本地数据(ByteBuffer)
     *
     * @param buffer
     * @param payloadSize
     */
    public void updateUdpBuffer(ByteBuffer buffer, int payloadSize) {
        // TODO: 2017/11/27  
    }


    /**
     * 更新IP数据报首部校验和字段
     *
     * @see <a href="http://blog.csdn.net/star_xiong/article/details/17303003">校验和相关</a>
     */
    public void updateIpv4CheckSum() {
        ByteBuffer duplicateBuffer = mBuffer.duplicate();
        //此处一定要将position标记位置零
        duplicateBuffer.position(0);
        //将之前的校验和字段置零 从0开始,10 字节 即 80 bit 处 ,就是checkSum 字段
        duplicateBuffer.putShort(10, (short) 0);

        int ipHeaderCheckSum = 0;
        int headerLength = mIpv4Header.getHeaderLength();
        while (headerLength > 1) {
            ipHeaderCheckSum += UnsignedConvertUtil.getUnsignedShort(duplicateBuffer.getShort());
            headerLength -= 2;
        }
        if (headerLength > 0) {
            ipHeaderCheckSum += UnsignedConvertUtil.getUnsignedByte(duplicateBuffer.get()) << 8;
        }
        while (ipHeaderCheckSum >> 16 > 0) {
            ipHeaderCheckSum = (ipHeaderCheckSum & 0xffff) + (ipHeaderCheckSum >> 16);
        }
        ipHeaderCheckSum = ~ipHeaderCheckSum;
        mIpv4Header.setHeaderCheckSum(ipHeaderCheckSum);
        mBuffer.putShort(10, (short) ipHeaderCheckSum);
    }

    /**
     * 更新 TCP 报文段首部校验和字段
     *
     * @param payloadSize TCP 报文段的载荷长度
     */
    public void updateTcpCheckSum(int payloadSize) {
        int tcpHeaderCheckSum = 0;
        int tcpTotalLength = TCP_HEADER_SISE + payloadSize;
        //计算伪首部校验和
        tcpHeaderCheckSum += calculatePseudoHeaderCheckSum(tcpTotalLength);

        ByteBuffer duplicateBuffer = mBuffer.duplicate();
        //IPV4_HEADER_SISE + 16 是 TCP头部校验和字段相对于IP数据报第一个字节数据的偏移量
        duplicateBuffer.putShort(IPV4_HEADER_SISE + 16, (short) 0);

        //将position标记位指向TCP报文段的开始位置处
        duplicateBuffer.position(IPV4_HEADER_SISE);

        while (tcpTotalLength > 1) {
            tcpHeaderCheckSum += UnsignedConvertUtil.getUnsignedShort(duplicateBuffer.getShort());
            tcpHeaderCheckSum -= 2;
        }
        if (tcpTotalLength > 0) {
            tcpHeaderCheckSum += UnsignedConvertUtil.getUnsignedByte(duplicateBuffer.get()) << 8;
        }
        while (tcpHeaderCheckSum >> 16 > 0) {
            tcpHeaderCheckSum = (tcpHeaderCheckSum & 0xFFFF) + (tcpHeaderCheckSum >> 16);
        }
        tcpHeaderCheckSum = ~tcpHeaderCheckSum;
        mTcpHeader.setCheckSum(tcpHeaderCheckSum);
        mBuffer.putShort(IPV4_HEADER_SISE + 16, (short) tcpHeaderCheckSum);


    }

    /**
     * 更新 UDP报文段首部校验和字段
     *
     * @param payloadSize UDP 报文段的载荷长度
     */
    public void updateUdpCheckSum(int payloadSize) {
        int udpHeaderCheckSum = 0;
        int udpTotalLength = UDP_HEADER_SISE + payloadSize;
        //计算伪首部校验和
        udpHeaderCheckSum += calculatePseudoHeaderCheckSum(udpTotalLength);

        ByteBuffer duplicateBuffer = mBuffer.duplicate();
        //IPV4_HEADER_SISE + 7 是 UDP头部校验和字段相对于IP数据报第一个字节数据的偏移量
        duplicateBuffer.putShort(IPV4_HEADER_SISE + 7, (short) 0);

        //将position标记位指向UDP报文段的开始位置处
        duplicateBuffer.position(IPV4_HEADER_SISE);

        while (udpTotalLength > 1) {
            udpHeaderCheckSum += UnsignedConvertUtil.getUnsignedShort(duplicateBuffer.getShort());
            udpHeaderCheckSum -= 2;
        }
        if (udpTotalLength > 0) {
            udpHeaderCheckSum += UnsignedConvertUtil.getUnsignedByte(duplicateBuffer.get()) << 8;
        }
        while (udpHeaderCheckSum >> 16 > 0) {
            udpHeaderCheckSum = (udpHeaderCheckSum & 0xFFFF) + (udpHeaderCheckSum >> 16);
        }
        udpHeaderCheckSum = ~udpHeaderCheckSum;
        mUdpHeader.setCheckSum(udpHeaderCheckSum);
        mBuffer.putShort(IPV4_HEADER_SISE + 7, (short) udpHeaderCheckSum);

    }

    /**
     * 计算伪首部校验和的方法.
     * <p>
     * UDP 和 TCP 在计算校验和时,需要先构造一个伪首部,并且参与计算校验和
     *
     * @return 伪首部校验和
     * @see <a href="http://blog.csdn.net/jiangqin115/article/details/39315085">伪首部相关</a>
     */
    public int calculatePseudoHeaderCheckSum(int tcpTotalLength) {
        int pseudoHeaderCheckSum = 0;
        //源点地址
        ByteBuffer sourceAndDestinationIPAddressBuffer = ByteBuffer.wrap(mIpv4Header.getSourceAddress().getAddress());
        pseudoHeaderCheckSum += UnsignedConvertUtil.getUnsignedShort(sourceAndDestinationIPAddressBuffer.getShort())
                + UnsignedConvertUtil.getUnsignedShort(sourceAndDestinationIPAddressBuffer.getShort());
        //目的地址
        sourceAndDestinationIPAddressBuffer = ByteBuffer.wrap(mIpv4Header.getDestinationAddress().getAddress());
        pseudoHeaderCheckSum += UnsignedConvertUtil.getUnsignedShort(sourceAndDestinationIPAddressBuffer.getShort())
                + UnsignedConvertUtil.getUnsignedShort(sourceAndDestinationIPAddressBuffer.getShort());
        //协议号
        pseudoHeaderCheckSum += TCP_PROTOCOL_NUM;
        //TCP报文段长,即 头部长加上负载数据长
        pseudoHeaderCheckSum += tcpTotalLength;

        return pseudoHeaderCheckSum;
    }


}
