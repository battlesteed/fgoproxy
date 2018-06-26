package lee.study;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import lee.study.proxyee.exception.HttpProxyExceptionHandle;
import lee.study.proxyee.intercept.CertDownIntercept;
import lee.study.proxyee.intercept.HttpProxyIntercept;
import lee.study.proxyee.intercept.HttpProxyInterceptInitializer;
import lee.study.proxyee.intercept.HttpProxyInterceptPipeline;
import lee.study.proxyee.server.HttpProxyServer;

public class TestHttpProx {

	public static void main(String[] args) {
		new HttpProxyServer()
				// .proxyConfig(new ProxyConfig(ProxyType.SOCKS5, "127.0.0.1",
				// 1085)) //使用socks5二级代理
				.proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
					@Override
					public void init(HttpProxyInterceptPipeline pipeline) {
						pipeline.addLast(new CertDownIntercept()); // 处理证书下载
						pipeline.addLast(new HttpProxyIntercept() {
							@Override
							public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
									HttpProxyInterceptPipeline pipeline) throws Exception {
								// 替换UA，伪装成手机浏览器
								/*
								 * httpRequest.headers().set(HttpHeaderNames.
								 * USER_AGENT,
								 * "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
								 * );
								 */
								// 转到下一个拦截器处理
								pipeline.beforeRequest(clientChannel, httpRequest);
							}

							
							
							
							@Override
							public void afterResponse(Channel clientChannel, Channel proxyChannel,
									HttpRequest httpRequest, HttpContent httpContent,
									HttpProxyInterceptPipeline pipeline) throws Exception {
								super.afterResponse(clientChannel, proxyChannel, httpRequest, httpContent, pipeline);
								httpContent.content();
							}




							/*@Override
							public void afterResponse(Channel clientChannel, Channel proxyChannel,HttpRequest httpRequest, 
									HttpResponse httpResponse,HttpProxyInterceptPipeline pipeline) throws Exception {

								// 拦截响应，添加一个响应头
								httpResponse.headers().add("intercept", "test");
								
								
								// 转到下一个拦截器处理
								pipeline.afterResponse(clientChannel, proxyChannel, httpRequest, httpResponse);
							}*/
						});
					}
				}).httpProxyExceptionHandle(new HttpProxyExceptionHandle() {
					@Override
					public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
						System.out.println("111111111111111");
						cause.printStackTrace();
					}

					@Override
					public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause)
							throws Exception {
						System.out.println("22222222222222");
						cause.printStackTrace();
					}
				}).start(9999);
	}
}
