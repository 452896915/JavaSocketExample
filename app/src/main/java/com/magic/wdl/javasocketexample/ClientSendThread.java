package com.magic.wdl.javasocketexample;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
/**
 * Created by wangdongliang on 16/9/29.
 */

public class ClientSendThread extends Thread {
    List<String> toSendList;

    Socket clientSocket = null;
    PrintStream outputStream = null;

    public ClientSendThread(List<String> toSendList) {
        this.toSendList = toSendList;
    }

    @Override
    public void run() {
        try {
            // 客户端连接服务器端2000端口,通过该端口向服务器发消息
            clientSocket = new Socket("127.0.0.1", 2000);

            // 使用OutputStream发送必须添加\r\n作为结尾,否则即使flush也不会发送,只有到close的时候才发送
            // 使用PrintStream的println会自动添加\r\n
//          OutputStream outputStream=clientSocket.getOutputStream();
//          outputStream.flush();
//          outputStream.write(new String("hello\r\n").getBytes());

            outputStream=new PrintStream(clientSocket.getOutputStream());

            while (!Thread.currentThread().isInterrupted()) {
                if (toSendList != null && toSendList.size() > 0) {
                    outputStream.println(toSendList.get(0));
                    toSendList.clear();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cleanUpJob();
    }

    private void cleanUpJob() {
        outputStream.close();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
