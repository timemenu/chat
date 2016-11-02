package com.whatpull.www.chat;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket socket;
    private static final String HOST = "http://192.168.0.22:3000";
    private static final String TAG = "ChattingActivity";
    private EditText message;
    private TextView content;
    private Button send;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        initView();
        initSocket();
    }

    private void initView() {
        message = (EditText) findViewById(R.id.message);
        content = (TextView) findViewById(R.id.content);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    private void  initSocket() {
        try {
            socket = IO.socket(HOST);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on("chat message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String res = args[0].toString();
                    if(!res.isEmpty() && res != "") {
                        String get = content.getText().toString();
                        sb = new StringBuilder(get);
                        if(!get.isEmpty() && get != "") {
                            sb.append("\n");
                        }
                        sb.append(res);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                content.setText(sb);
                            }
                        });
                    }
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void commitMessage(String message) {
        socket.emit("chat message", message);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.send == id) {
            commitMessage(message.getText().toString());
        }
    }
}
