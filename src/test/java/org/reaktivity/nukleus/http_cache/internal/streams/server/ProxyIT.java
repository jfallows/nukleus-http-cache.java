/**
 * Copyright 2016-2017 The Reaktivity Project
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
package org.reaktivity.nukleus.http_cache.internal.streams.server;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;
import static org.reaktivity.reaktor.internal.ReaktorConfiguration.ABORT_STREAM_FRAME_TYPE_ID;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.reaktivity.nukleus.http_cache.internal.types.stream.AbortFW;
import org.reaktivity.reaktor.test.ReaktorRule;

public class ProxyIT
{
    private final K3poRule k3po = new K3poRule()
        .addScriptRoot("route", "org/reaktivity/specification/nukleus/http_cache/control/route")
        .addScriptRoot("streams", "org/reaktivity/specification/nukleus/http_cache/streams/proxy");

    private final TestRule timeout = new DisableOnDebug(new Timeout(15, SECONDS));

    private final ReaktorRule nukleus = new ReaktorRule()
            .directory("target/nukleus-itests")
            .commandBufferCapacity(1024)
            .responseBufferCapacity(1024)
            .counterValuesBufferCapacity(1024)
            .nukleus("http-cache"::equals)
            .configure(ABORT_STREAM_FRAME_TYPE_ID, AbortFW.TYPE_ID)
            .clean();

    @Rule
    public final TestRule chain = outerRule(nukleus).around(k3po).around(timeout);

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/proxy.request/accept/client",
        "${streams}/proxy.request/connect/server",
        })
    public void proxyRequest() throws Exception
    {
        k3po.finish();
    }
    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/proxy.request.and.304/accept/client",
        "${streams}/proxy.request.and.304/connect/server",
    })
    public void proxyRequestWith304() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/proxy.request.and.follow.304/accept/client",
        "${streams}/proxy.request.and.follow.304/connect/server",
    })
    public void proxyAndFollow304() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/debounce.cache.sync/accept/client",
        "${streams}/debounce.cache.sync/connect/server",
    })
    public void debounceCacheSync() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/debounce.cache.sync.and.inject.individualized.push.promise/accept/client",
        "${streams}/debounce.cache.sync.and.inject.individualized.push.promise/connect/server",
    })
    public void debounceCacheSyncAndInjectIndividualizedPushPromise() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Ignore("TODO remove this as it forwards push promise")
    @Specification({
        "${route}/proxy/controller",
        "${streams}/debounce.cache.sync.but.not.forward.304/accept/client",
        "${streams}/debounce.cache.sync.but.not.forward.304/connect/server",
    })
    public void debounceCacheSyncButNotForward304() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/debounce.cache.sync.but.not.forward.304.without.pp/accept/client",
        "${streams}/debounce.cache.sync.but.not.forward.304.without.pp/connect/server",
    })
    public void debounceCacheSyncButNotForward304WithoutPP() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/not.debounce.multiple.requests/accept/client",
        "${streams}/not.debounce.multiple.requests/connect/server",
    })
    public void notDebounceMultipleRequests() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/not.debounce.private.cache/accept/client",
        "${streams}/not.debounce.private.cache/connect/server",
    })
    public void notDebounceWhenCacheSyncPrivateCacheControl() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/not.debounce.implied.private/accept/client",
        "${streams}/not.debounce.implied.private/connect/server",
    })
    public void notDebounceWhenImpliedCacheSyncPrivate() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/debounce.when.explicitly.public/accept/client",
        "${streams}/debounce.when.explicitly.public/connect/server",
    })
    public void debounceExplicitlyPublic() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/not.debounce.varys/accept/client",
        "${streams}/not.debounce.varys/connect/server",
    })
    public void notDebounceWhenVarys() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Ignore("not implemented")
    @Specification({
        "${route}/proxy/controller",
        "${streams}/cache.response/accept/client",
        "${streams}/cache.response/connect/server",
    })
    public void cacheResponse() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Ignore("not implemented")
    @Specification({
        "${route}/proxy/controller",
        "${streams}/cache.response.and.push.promise/accept/client",
        "${streams}/cache.response.and.push.promise/connect/server",
    })
    public void cacheResponseAndPushPromise() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/proxy.post.request/accept/client",
        "${streams}/proxy.post.request/connect/server",
    })
    public void proxyPostRequest() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/accept.sent.abort/accept/client",
        "${streams}/accept.sent.abort/connect/server",
    })
    public void acceptSentAbort() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/connect.reply.sent.abort/accept/client",
        "${streams}/connect.reply.sent.abort/connect/server",
    })
    public void connectReplySentAbort() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/connect.sent.reset/accept/client",
        "${streams}/connect.sent.reset/connect/server",
    })
    public void connectSentReset() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/accept.reply.sent.reset/accept/client",
        "${streams}/accept.reply.sent.reset/connect/server",
    })
    public void acceptReplySentReset() throws Exception
    {
        k3po.finish();
    }


    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/client.sent.abort.on.scheduled.poll/accept/client"
    })
    public void clientSentAbortOnScheduledPoll() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/inject.header.values/accept/client",
        "${streams}/inject.header.values/connect/server",
    })
    public void injectHeaderValues() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/inject.missing.header.values/accept/client",
        "${streams}/inject.missing.header.values/connect/server",
    })
    public void injectMissingHeaderValues() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/inject.push.promise/accept/client",
        "${streams}/inject.push.promise/connect/server",
    })
    public void injectPushPromise() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/strip.injected.headers/accept/client",
        "${streams}/strip.injected.headers/connect/server",
    })
    public void stripInjectedHeaders() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/strip.injected.header.values/accept/client",
        "${streams}/strip.injected.header.values/connect/server",
    })
    public void stripInjectedHeaderValues() throws Exception
    {
        k3po.finish();
    }

    @Test
    @Specification({
        "${route}/proxy/controller",
        "${streams}/strip.missing.injected.header.values/accept/client",
        "${streams}/strip.missing.injected.header.values/connect/server",
    })
    public void stripMissingInjectedHeaderValues() throws Exception
    {
        k3po.finish();
    }
}
