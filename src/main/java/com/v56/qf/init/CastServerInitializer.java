/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf.init;

import com.v56.qf.component.CacheComponent;
import com.v56.qf.handler.HttpApiHandler;
import com.v56.qf.handler.WsInTextFrameHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/5
 */
public class CastServerInitializer extends ChannelInitializer<Channel> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(CastServerInitializer.class);

    private final CacheComponent cacheComponent;

    public CastServerInitializer(CacheComponent cacheComponent) {
        this.cacheComponent = cacheComponent;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("logging", new LoggingHandler(LogLevel.INFO))
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(64 * 1024))
                .addLast(new HttpApiHandler(cacheComponent))
                .addLast(new WebSocketServerProtocolHandler("/websocket"))
                .addLast(new WsInTextFrameHandler(cacheComponent));
    }
}
