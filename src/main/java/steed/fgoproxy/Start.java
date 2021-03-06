package steed.fgoproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import lee.study.proxyee.exception.HttpProxyExceptionHandle;
import lee.study.proxyee.intercept.CertDownIntercept;
import lee.study.proxyee.intercept.HttpProxyIntercept;
import lee.study.proxyee.intercept.HttpProxyInterceptInitializer;
import lee.study.proxyee.intercept.HttpProxyInterceptPipeline;
import lee.study.proxyee.server.HttpProxyServer;

public class Start {

	public static void main(String[] args) {
		new HttpProxyServer().proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
			@Override
			public void init(HttpProxyInterceptPipeline pipeline) {
				pipeline.addLast(new CertDownIntercept()); // 处理证书下载
				pipeline.addLast(new HttpProxyIntercept() {
					private HttpRequest httpRequest;
					@Override
					public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
							HttpProxyInterceptPipeline pipeline) throws Exception {
						pipeline.beforeRequest(clientChannel, httpRequest);
//						httpRequest.
						this.httpRequest = httpRequest;
					}

					@Override
					public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpRequest httpRequest,
							HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
						httpResponse.headers().add("intercept", "test");
						
//						HttpContent httpContent = ((FullHttpResponse)httpResponse).content()
								
						boolean open = clientChannel.isOpen();
//						ByteBuf byteBuf = ((DefaultHttpResponse)httpResponse).content();
//						byte[] msgByte = new byte[byteBuf.readableBytes()];
//				    	byteBuf.readBytes(msgByte);
//				    	String json = new String(msgByte);
//						FGOUtil.out("返回数据", json);
						
						FGOUtil.out("isActive", proxyChannel.isActive());
						FGOUtil.out("isOpen", proxyChannel.isOpen());
						FGOUtil.out("LastHttpContent", httpResponse.getClass().getName());
						
						pipeline.afterResponse(clientChannel, proxyChannel, httpRequest, httpResponse);
					}

					@Override
					public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpRequest httpRequest,
							HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
						FGOUtil.out("isActive", proxyChannel.isActive());
						FGOUtil.out("private HttpRequest httpRequest;", this.httpRequest);
						FGOUtil.out("isOpen", proxyChannel.isOpen());
						FGOUtil.out("LastHttpContent", httpContent instanceof LastHttpContent);
//						boolean open = clientChannel.isOpen();
//						ByteBuf byteBuf = httpContent.content();
//						byte[] msgByte = new byte[byteBuf.readableBytes()];
//				    	byteBuf.readBytes(msgByte);
//				    	String json = new String(msgByte);
//						FGOUtil.out("返回数据", json);
						
//						httpContent.content();
//						httpcon
						super.afterResponse(clientChannel, proxyChannel, httpRequest, httpContent, pipeline);
						
						
					}
					
					
					
					
				});
			}
		}).httpProxyExceptionHandle(new HttpProxyExceptionHandle() {
			@Override
			public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
				cause.printStackTrace();
			}

			@Override
			public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause) throws Exception {
				cause.printStackTrace();
			}
		}).start(8888);
	}
}
