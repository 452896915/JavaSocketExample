package com.magic.wdl.javasocketexample;

import android.os.Handler;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by wangdongliang on 16/9/30.
 */

public class ClientReceiveThread extends Thread {
    private List<String> strList;
    private Handler handler;

    private ServerSocket server = null;
    private Socket socket = null;

    private DataInputStream inputStream;
    private BufferedReader bufferedReader;

    public ClientReceiveThread(List<String> strList, Handler handler) {
        this.strList = strList;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            // 客户端监听2001端口,等待服务器的连接,接收服务器发来的消息
            server = new ServerSocket(2001);
            socket = server.accept();

            inputStream = new DataInputStream(socket.getInputStream());

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            while (!Thread.currentThread().isInterrupted()) {
//              String line = inputStream.readLine();
                String line = bufferedReader.readLine();
                if (!TextUtils.isEmpty(line)) {
                    strList.add(line);
                    handler.sendEmptyMessage(-1);
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
            inputStream.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
