/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf.handler;

import com.v56.qf.component.CacheComponent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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
public class WsInTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(WsInTextFrameHandler.class);

    private final CacheComponent cacheComponent;

    public WsInTextFrameHandler(CacheComponent cacheComponent) {
        this.cacheComponent = cacheComponent;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("id: {}, msg: {}  完成握手", ctx.channel().id(), ctx.channel().metadata());
            System.out.println(ctx.channel().id() + " 完成握手");
            cacheComponent.getChannelGroup().add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println(ctx.channel().id() + " send: " + msg.retain().text());
        log.info(msg.retain().text());
    }
}
