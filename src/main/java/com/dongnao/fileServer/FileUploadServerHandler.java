package com.dongnao.fileServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;
/**
 * 泛型不匹配 不会调用 messageReceived  有channelread 不调用 messageReceived
 * @author yangsong
 * @params
 * @date 2019/7/10
 * return
 **/

public class FileUploadServerHandler extends SimpleChannelInboundHandler<FileUploadFile> {
    private int byteRead;
    private volatile int start = 0;
    private String file_dir = "D:";

   /* @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileUploadFile) {
            FileUploadFile ef = (FileUploadFile) msg;
            byte[] bytes = ef.getBytes();
            byteRead = ef.getEndPos();
            String md5 = ef.getFile_md5();//文件名
            String path = file_dir + File.separator + md5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;
            if (byteRead > 0) {
                ctx.writeAndFlush(start);
                randomAccessFile.close();
                System.out.println("threadName="+Thread.currentThread().getName()+"-------"+start);
            }
        }
    }*/

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FileUploadFile ef) throws Exception {
        byte[] bytes = ef.getBytes();
        byteRead = ef.getEndPos();
        String md5 = ef.getFile_md5();//文件名
        String path = file_dir + File.separator + md5;
        File file = new File(path);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
        randomAccessFile.seek(start);
        randomAccessFile.write(bytes);
        start = start + byteRead;
        if (byteRead > 0) {
            ctx.writeAndFlush(start);
            randomAccessFile.close();
            //System.out.println("threadName="+Thread.currentThread().getName()+"-------"+start);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
