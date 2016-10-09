package com.magic.wdl.javasocketexample;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wangdongliang on 16/9/29.
 */

public class ServerThread extends Thread {
    private ServerSocket serverSocket = null;
    private Socket serverReceiveSocket = null;
    private Socket serverSendSocket = null;

    private DataInputStream dataInputStream;
    private PrintStream printStream = null;

    private BufferedReader bufferedReader;

    @Override
    public void run() {
        try {
            // 服务器线程通过2000端口监听客户端发来的消息
            serverSocket = new ServerSocket(2000);
            serverReceiveSocket = serverSocket.accept();

            // 和客户端2001端口进行通信,向客户端发送消息
            serverSendSocket = new Socket("127.0.0.1", 2001);

            dataInputStream = new DataInputStream(serverReceiveSocket.getInputStream());
            printStream = new PrintStream(serverSendSocket.getOutputStream());

            bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream, "UTF-8"));

            while (!Thread.currentThread().isInterrupted()) {
                try {
//                  String str = dataInputStream.readLine();
                    String line = bufferedReader.readLine();

                    printStream.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        cleanUpJob();
    }

    private void cleanUpJob() {
        try {
            bufferedReader.close();
            dataInputStream.close();
            printStream.close();
            serverSendSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
