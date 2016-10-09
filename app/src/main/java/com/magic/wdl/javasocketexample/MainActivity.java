package com.magic.wdl.javasocketexample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.list_view)
    ListView listView;

    @Bind(R.id.input_et)
    EditText editText;

    // 待发送消息列表
    private List<String> toSendList;
    // 聊天消息列表
    private List<String> msgList;

    private Handler handler;

    private ArrayAdapter<String> arrayAdapter;

    // 服务器线程,负责接收消息并返回消息
    private ServerThread serverThread;

    // 客户端发消息线程
    private ClientSendThread clientSendThread;
    // 客户端收消息线程
    private ClientReceiveThread clientReceiveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toSendList = new ArrayList<>();
        msgList = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, msgList);
        listView.setAdapter(arrayAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 收到服务器发过来的数据,更新列表
                if (msg.what < 0) {
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        };

        serverThread = new ServerThread();
        clientSendThread = new ClientSendThread(toSendList);
        clientReceiveThread = new ClientReceiveThread(msgList, handler);

        // 注意,这里不能Thread.run()启动线程,因为run()方法不会启动新的线程,而是在当前线程执行;start方法才启动新的线程
        // http://stackoverflow.com/questions/8579657/whats-the-difference-between-thread-start-and-runnable-run
//      clientReceiveThread.run();

        clientReceiveThread.start();
        serverThread.start();
        clientSendThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 发出中断信号,三个线程各自执行清理工作
        clientSendThread.interrupt();
        clientReceiveThread.interrupt();
        serverThread.interrupt();
    }

    @OnClick(R.id.send_button)
    public void onSendMessage(View v) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, "Empty text!", Toast.LENGTH_SHORT).show();
            return;
        }

        msgList.add(editText.getText().toString());
        arrayAdapter.notifyDataSetChanged();

        toSendList.add(editText.getText().toString());
        editText.setText("");
    }
}
