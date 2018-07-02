package steed.proxy.server;

import java.util.Map;
import java.util.Map.Entry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import steed.util.logging.LoggerFactory;

public class NettyServerBootstrap {
	private int port;

	public NettyServerBootstrap(int port, ChannelInitializer<Channel> channelInitializer){
		this.port = port;
		bind(channelInitializer);
	}

	private void bind(ChannelInitializer<Channel> channelInitializer){
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childHandler(channelInitializer);
		ChannelFuture f;
		try {
			f = bootstrap.bind(port).sync();
			if (f.isSuccess()) {
				LoggerFactory.getLogger().debug("steed proxy server started.");
			} else {
				LoggerFactory.getLogger().debug("steed proxy server faile to start.");
			}
		} catch (InterruptedException e) {
			LoggerFactory.getLogger().debug("steed proxy server stop",e);
		}
	}

}
