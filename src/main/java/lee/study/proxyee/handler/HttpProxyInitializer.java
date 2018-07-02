package lee.study.proxyee.handler;

import javax.net.ssl.SSLEngine;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.ssl.SslHandler;
import lee.study.proxyee.util.ProtoUtil.RequestProto;

/**
 * HTTP代理，转发解码后的HTTP报文
 */
public class HttpProxyInitializer extends ChannelInitializer {

	private Channel clientChannel;
	private RequestProto requestProto;
	private ProxyHandler proxyHandler;
	private HttpRequest httpRequest;

	public HttpProxyInitializer(Channel clientChannel, RequestProto requestProto, ProxyHandler proxyHandler,HttpRequest httpRequest) {
		this.clientChannel = clientChannel;
		this.requestProto = requestProto;
		this.proxyHandler = proxyHandler;
		this.httpRequest = httpRequest;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		if (proxyHandler != null) {
			ch.pipeline().addLast(proxyHandler);
		}
		if (requestProto.getSsl()) {
			ch.pipeline()
			.addLast(((HttpProxyServerHandle) clientChannel.pipeline().get("serverHandle")).getServerConfig()
					.getClientSslCtx().newHandler(ch.alloc(), requestProto.getHost(), requestProto.getPort()));
		}
		ch.pipeline().addLast("httpCodec", new HttpClientCodec());
		ch.pipeline().addLast("aggegator", new HttpObjectAggregator(Integer.MAX_VALUE));
		ch.pipeline().addLast("httpContentDecompressor", new HttpContentDecompressor());
		ch.pipeline().addLast("proxyClientHandle", new HttpProxyClientHandle(clientChannel,httpRequest));
//		ch.pipeline().addLast("httpContentCompressor", new HttpContentCompressor());
	}
}
