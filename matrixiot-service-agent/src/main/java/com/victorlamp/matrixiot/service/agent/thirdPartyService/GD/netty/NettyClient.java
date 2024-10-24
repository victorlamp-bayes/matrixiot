package com.victorlamp.matrixiot.service.agent.thirdPartyService.GD.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Date;

@Slf4j
public class NettyClient {
    private static final String StartChar = "^^";
    private static final String EndChar = "$$";
    private final String serverIP;
    private final int port;

    public NettyClient(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    /**
     * 读集中器冻结数据
     *
     * @param concentratorMAC 集中器MAC地址
     * @param region          集中器行政区码
     * @param ports           端口号集合
     * @return json格式数据
     */
    public String getFreezeReadingInfo(String concentratorMAC, String region, String ports) {
        String sendData = MessageFormat.format("\"command\":\"8C00000107\",\"concentrator\":\"{0}\",\"region\":\"{1}\",\"type\":\"02\",\"ports\":[{2}]", concentratorMAC, region, ports);
        sendData = StartChar + "{" + sendData + "}" + EndChar;
        return executeCommand(sendData);
    }

    private String executeCommand(String sendData) {
        long ts;
        long startTime = new Date().getTime();
        int idcCommandTimeout = 120;
        EventLoopGroup group = new NioEventLoopGroup();
        GDResponseHandler handler = new GDResponseHandler(sendData);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(handler);
                        }
                    });

            log.info("连接站点:{}, 指令:{}", serverIP + ":" + port, sendData);
            ChannelFuture future = bootstrap.connect(serverIP, port).sync();

            if (future.isSuccess()) {
                future.channel().closeFuture().sync();
            }

            String returnedData = "";

            while (returnedData.length() == 0) {
                returnedData = handler.getResponse();
                if (returnedData.length() == 0) {
                    ts = new Date().getTime() - startTime;
                    if (ts > idcCommandTimeout * 1000) {
                        returnedData = "{\"status\":-1,\"msg\":\"指令超时！\"}";
                        break;
                    }
                }
            }

            // 字符串返回乱码的时候处理
            if (returnedData.indexOf("��") > 0) {
                returnedData = returnedData.replace("��", "\"");
            }

            log.info("响应数据:{}", returnedData);
            return returnedData;

        } catch (Exception e) {
            return "{\"status\":-1,\"msg\":\"" + e.getMessage() + "\"}";
        } finally {
            group.shutdownGracefully();
        }
    }
}
