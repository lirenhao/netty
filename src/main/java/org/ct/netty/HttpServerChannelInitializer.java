package org.ct.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

@Component
@Qualifier("httpServerChannelInitializer")
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	HttpRequestHandler serverHandler;
	
	@Override
	protected void initChannel(SocketChannel sch) throws Exception {
		ChannelPipeline pipeline = sch.pipeline();
		pipeline.addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(Short.MAX_VALUE)).addLast(serverHandler);
	}

}
