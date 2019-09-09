/**
 * Copyright 2016-2019 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.http_cache.internal.stream;

import static java.lang.System.currentTimeMillis;
import static org.reaktivity.nukleus.buffer.BufferPool.NO_SLOT;
import static org.reaktivity.nukleus.http_cache.internal.HttpCacheConfiguration.DEBUG;
import static org.reaktivity.nukleus.http_cache.internal.stream.util.HttpHeadersUtil.getHeader;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.nukleus.function.MessageConsumer;
import org.reaktivity.nukleus.http_cache.internal.proxy.cache.DefaultCacheEntry;
import org.reaktivity.nukleus.http_cache.internal.types.HttpHeaderFW;
import org.reaktivity.nukleus.http_cache.internal.types.ListFW;
import org.reaktivity.nukleus.http_cache.internal.types.OctetsFW;
import org.reaktivity.nukleus.http_cache.internal.types.stream.AbortFW;
import org.reaktivity.nukleus.http_cache.internal.types.stream.BeginFW;
import org.reaktivity.nukleus.http_cache.internal.types.stream.DataFW;
import org.reaktivity.nukleus.http_cache.internal.types.stream.EndFW;
import org.reaktivity.nukleus.http_cache.internal.types.stream.HttpBeginExFW;

final class HttpCacheProxyNotModifiedResponse
{
    private final HttpCacheProxyFactory factory;

    private final int initialWindow;
    private final int requestHash;
    private final MessageConsumer acceptReply;
    private final long acceptRouteId;
    private final long acceptReplyId;

    private final MessageConsumer connectReply;
    private final long connectRouteId;
    private final long connectReplyId;

    private int connectReplyBudget;
    private int requestSlot;

    HttpCacheProxyNotModifiedResponse(
        HttpCacheProxyFactory factory,
        int requestHash,
        int requestSlot,
        MessageConsumer acceptReply,
        long acceptRouteId,
        long acceptReplyId,
        MessageConsumer connectReply,
        long connectReplyId,
        long connectRouteId)
    {
        this.factory = factory;
        this.requestHash = requestHash;
        this.requestSlot = requestSlot;
        this.acceptReply = acceptReply;
        this.acceptRouteId = acceptRouteId;
        this.acceptReplyId = acceptReplyId;
        this.connectReply = connectReply;
        this.connectRouteId = connectRouteId;
        this.connectReplyId = connectReplyId;
        this.initialWindow = factory.responseBufferPool.slotCapacity();
    }

    @Override
    public String toString()
    {
        return String.format("%s[connectRouteId=%016x, connectReplyStreamId=%d]",
                             getClass().getSimpleName(), connectRouteId, connectReplyId);
    }

    void onResponseMessage(
        int msgTypeId,
        DirectBuffer buffer,
        int index,
        int length)
    {
        switch (msgTypeId)
        {
        case BeginFW.TYPE_ID:
            final BeginFW begin = factory.beginRO.wrap(buffer, index, index + length);
            onBegin(begin);
            break;
        case DataFW.TYPE_ID:
            final DataFW data = factory.dataRO.wrap(buffer, index, index + length);
            onData(data);
            break;
        case EndFW.TYPE_ID:
            final EndFW end = factory.endRO.wrap(buffer, index, index + length);
            onEnd(end);
            break;
        case AbortFW.TYPE_ID:
            final AbortFW abort = factory.abortRO.wrap(buffer, index, index + length);
            onAbort(abort);
            break;
        }
    }

    private void onBegin(
        BeginFW begin)
    {
        final long connectReplyId = begin.streamId();
        final OctetsFW extension = factory.beginRO.extension();
        final HttpBeginExFW httpBeginFW = extension.get(factory.httpBeginExRO::wrap);
        final ListFW<HttpHeaderFW> responseHeaders = httpBeginFW.headers();

        if (DEBUG)
        {
            System.out.printf("[%016x] CONNECT %016x %s [received response]\n", currentTimeMillis(), connectReplyId,
                              getHeader(responseHeaders, ":status"));
        }
        DefaultCacheEntry cacheEntry = factory.defaultCache.supply(requestHash);
        factory.defaultCache.send304(cacheEntry,
                                     getRequestHeaders(),
                                     acceptReply,
                                     acceptRouteId,
                                     acceptReplyId);
        sendWindow(initialWindow, begin.trace());
        purgeRequest();
    }

    private void onData(
        DataFW data)
    {
        sendWindow(data.length() + data.padding(), data.trace());
    }

    private void onEnd(EndFW end)
    {
        factory.writer.doHttpEnd(acceptReply,
                                 acceptRouteId,
                                 acceptReplyId,
                                 end.trace());

    }

    private void onAbort(AbortFW abort)
    {
        factory.writer.doAbort(acceptReply,
                               acceptRouteId,
                               acceptReplyId,
                               abort.trace());
    }

    private void sendWindow(
        int credit,
        long traceId)
    {
        connectReplyBudget += credit;
        if (connectReplyBudget > 0)
        {
            factory.writer.doWindow(connectReply,
                                    connectRouteId,
                                    connectReplyId,
                                    traceId,
                                    credit,
                                    0,
                                    0L);
        }
    }

    private ListFW<HttpHeaderFW> getRequestHeaders()
    {
        final MutableDirectBuffer buffer = factory.requestBufferPool.buffer(requestSlot);
        return factory.requestHeadersRO.wrap(buffer, 0, buffer.capacity());
    }

    private void purgeRequest()
    {
        if (requestSlot != NO_SLOT)
        {
            factory.requestBufferPool.release(requestSlot);
            this.requestSlot = NO_SLOT;
        }
    }
}