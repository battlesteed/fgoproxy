package steed.fgoproxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import steed.proxy.server.NettyServerBootstrap;
import steed.util.logging.LoggerFactory;

public class SteedProxyStartUp {
	
	public static void main(String[] args){
		new NettyServerBootstrap(8888, new ChannelInitializer<Channel>() {
			
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("httpRequestDecoder", new HttpRequestDecoder());
//				pipeline.addLast("httpRequestDecoder", new http);
//				/pipeline.addLast("aggegator", new HttpObjectAggregator(Integer.MAX_VALUE));
				pipeline.addLast("test", new ChannelInboundHandlerAdapter(){

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						super.channelRead(ctx, msg);
						LoggerFactory.getLogger().debug("msgClass-->"+msg.getClass().getName());
					}
					
				});
				pipeline.addLast("test2", new SimpleChannelInboundHandler<HttpObject>(){

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
						LoggerFactory.getLogger().debug("HttpObjectClass-->"+msg.getClass().getName());
						DefaultFullHttpResponse ddd = (DefaultFullHttpResponse) msg;
						
//						ctx.writeAndFlush(msg);
//						ddd.
					}
					
				});
				pipeline.addLast("httpRequestEncoder",new HttpRequestEncoder());
			}
		});
	}
}
