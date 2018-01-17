package org.ct.netty;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

@Component
public class HttpServer {
	private Channel channel;
	@Autowired
	@Qualifier("serverBootstrap")
	private ServerBootstrap b;

	@Autowired
	@Qualifier("tcpSocketAddress")
	private InetSocketAddress tcpPort;

	@PostConstruct
	public void start() throws Exception {
		channel = b.bind(tcpPort).sync().channel();
	}

	@PreDestroy
	public void stop() throws Exception {
		channel.close().sync();
	}
}
