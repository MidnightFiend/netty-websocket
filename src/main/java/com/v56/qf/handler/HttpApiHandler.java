/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf.handler;

import com.v56.qf.component.CacheComponent;
import com.v56.qf.util.ReturnUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * <p>
 * Description: 其他后端调用API
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/15
 */
public class HttpApiHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(HttpApiHandler.class);

    private final CacheComponent cacheComponent;

    private static File TEST_PAGE;

    public HttpApiHandler(CacheComponent cacheComponent) {
        this.cacheComponent = cacheComponent;

        URL location = HttpApiHandler.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            TEST_PAGE = new File(path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        System.out.println(fullHttpRequest.uri());
        try {
            if ("/broadcast".equals(fullHttpRequest.uri())) {
                // 1.广播
                castAll(channelHandlerContext, fullHttpRequest);
            } else if ("/broadcastRoom".equals(fullHttpRequest.uri())) {
                // 2.组播
                castRoom(fullHttpRequest);
            } else if ("/test".equals(fullHttpRequest.uri())) {
                // 获取测试页
                returnTestPage(channelHandlerContext, fullHttpRequest);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * 广播
     * @param request
     */
    private void castAll(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("castAll: {}", request.content().toString(CharsetUtil.UTF_8));
        // socket群发
        cacheComponent.getChannelGroup()
                .writeAndFlush(new TextWebSocketFrame(request.content().retain()))
                .addListener(ChannelFutureListener.CLOSE);

        ReturnUtil.returnJsonSuccess(ctx, request);
    }

    /**
     * 组播
     * @param request
     */
    private void castRoom(FullHttpRequest request) {

    }

    /**
     * 测试页面
     * @param ctx
     * @param request
     * @throws Exception
     */
    private void returnTestPage(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (HttpUtil.is100ContinueExpected(request)) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
            ctx.writeAndFlush(response);
            return;
        }
        System.out.println("page-size: " + TEST_PAGE.length());
        RandomAccessFile file = new RandomAccessFile(TEST_PAGE, "r");
        DefaultHttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            response.headers()
                    .set(HttpHeaderNames.CONTENT_LENGTH, file.length())
                    .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
