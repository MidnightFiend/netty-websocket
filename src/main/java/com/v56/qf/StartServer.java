/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf;

import com.v56.qf.component.CacheComponent;
import com.v56.qf.init.CastServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.net.InetSocketAddress;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/15
 */
public class StartServer {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(StartServer.class);

    private final CacheComponent cacheComponent = new CacheComponent();

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer());
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    protected ChannelInitializer<Channel> createInitializer() {
        return new CastServerInitializer(cacheComponent);
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        cacheComponent.getChannelGroup().close();
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 9990;
        // netty日志支持
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        final StartServer endPoint = new StartServer();
        ChannelFuture future = endPoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endPoint.destroy();
            }
        });
        future.channel()
                .closeFuture()
                .syncUninterruptibly();
    }
}
