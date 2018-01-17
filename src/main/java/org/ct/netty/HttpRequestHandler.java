package org.ct.netty;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

@Component
@Qualifier("serverHandler")
@Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	AtomicInteger ai = new AtomicInteger();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		String res = "Hello world " + ai.addAndGet(1);
		FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
		resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                resp.content().readableBytes());
        resp.headers().set(HttpHeaderNames.CONNECTION, true);
        
        ctx.writeAndFlush(resp);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		System.out.println(cause.getMessage());
	}
}
