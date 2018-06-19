/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf.util;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/19
 */
public class ReturnUtil {
    private static final Logger log = LoggerFactory.getLogger(ReturnUtil.class);

    private static final String SUCCESS_JSON;

    static {
        JSONObject json = new JSONObject();
        json.put("status", 200);
        json.put("message", "success");
        SUCCESS_JSON = json.toJSONString();
    }

    public static void returnJson(ChannelHandlerContext ctx, FullHttpRequest request, String json) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(json.getBytes()));
        response.headers()
                .set(CONTENT_TYPE, "text/json")
                .setInt(CONTENT_LENGTH, response.content().readableBytes());

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        } else {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void returnJsonSuccess(ChannelHandlerContext ctx, FullHttpRequest request) {
        returnJson(ctx, request, SUCCESS_JSON);
    }
}
