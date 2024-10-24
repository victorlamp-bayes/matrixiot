package com.victorlamp.matrixiot.service.agent.thirdPartyService.GD.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
public class GDResponseHandler extends ChannelInboundHandlerAdapter {
    private final String sendData;
    private String response = "";
    private String receivedBuffer; // tcp服务器返回的数据会临时存储在该变量
    private final String StartChar = "^^";
    private final String EndChar = "$$";

    public String getResponse() {
        return response;
    }

    public GDResponseHandler(String sendData) {
        this.sendData = sendData;
    }

    // 当客户端和服务端TCP链路建立成功之后，Netty的NIO线程会调用channelActive方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        byte[] message = new byte[0];
        log.info(sendData);
        try {
            message = sendData.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            log.info("客户端GB2312编码失败");
        }
        ByteBuf buffer = Unpooled.buffer(message.length);
        buffer.writeBytes(message);

        ctx.writeAndFlush(buffer);
    }

    // 当服务端返回应答消息时，channelRead方法被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 从Netty的ByteBuf中读取并打印应答消息。
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "GB2312");
        receivedBuffer += body;
        int iPosStar, iPosEnd;
        iPosStar = receivedBuffer.indexOf(StartChar);
        iPosEnd = receivedBuffer.indexOf(EndChar);
        if (iPosEnd > -1) { // 发现了结束符
            if (iPosStar < 0) { // 没发现开始符，需要丢弃该段数据
                receivedBuffer = "";
            } else {
                response = receivedBuffer.substring(iPosStar + StartChar.length(), iPosEnd);
                if (receivedBuffer.endsWith(EndChar))
                    receivedBuffer = "";
                else
                    receivedBuffer = receivedBuffer.substring(iPosEnd + EndChar.length());
            }

            // 关闭连接释放客户端资源
            ctx.close();
        }
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        // 当发生异常时，打印异常日志，释放客户端资源。
        ctx.close();
    }
}
