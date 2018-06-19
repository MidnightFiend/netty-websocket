/*
 * Copyright (c) 2017 Sohu TV. All rights reserved.
 */
package com.v56.qf.component;

import com.google.common.collect.Maps;
import com.v56.qf.model.WsUser;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author ouyangyiding
 * @version 1.0
 * @Date 2018/6/19
 */
public class CacheComponent {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(CacheComponent.class);

    private Map<String, List<WsUser>> roomUsers = Maps.newConcurrentMap();

    /**
     * 通道组
     */
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
