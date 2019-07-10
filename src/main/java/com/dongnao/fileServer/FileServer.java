package com.dongnao.fileServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public class FileServer {

	public static void main(String[] args) throws Exception {
		int port=8080; //服务端默认端口
		new FileServer().bind(port);
	}
	
	public void bind(int port) throws Exception{
		//1用于服务端接受客户端的连接
		EventLoopGroup acceptorGroup = new NioEventLoopGroup();
		//2用于进行SocketChannel的网络读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//Netty用于启动NIO服务器的辅助启动类
			ServerBootstrap sb = new ServerBootstrap();
			//将两个NIO线程组传入辅助启动类中
			sb.group(acceptorGroup, workerGroup)
				//设置创建的Channel为NioServerSocketChannel类型
				.channel(NioServerSocketChannel.class)
				//配置NioServerSocketChannel的TCP参数  //指定 队列的大小  多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
				.option(ChannelOption.SO_BACKLOG, 1024)
				//设置绑定IO事件的处理类
				.childHandler(new ChannelInitializer<SocketChannel>() {
					//创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {


						ch.pipeline().addLast(new ObjectEncoder());
						ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
						ch.pipeline().addLast(new FileUploadServerHandler());


					}
				});
			//绑定端口，同步等待成功（sync()：同步阻塞方法，等待bind操作完成才继续）
			//ChannelFuture主要用于异步操作的通知回调
			ChannelFuture cf = sb.bind(port).sync();
			System.out.println("服务端启动在8080端口。");
			//等待服务端监听端口关闭
			cf.channel().closeFuture().sync();
		} finally {
			//优雅退出，释放线程池资源
			acceptorGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}