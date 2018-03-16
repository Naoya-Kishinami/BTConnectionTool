package com.android.btconnectiontool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.btconnection.BTConnect;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.txtHello);
        textView.setText(BTConnect.GetReceived());

        if (BTConnect.InitConnection()) {
            BTConnect.DeviceConnect(this);
        }

        findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.txtHello);
                textView.setText(BTConnect.GetReceived());
            }
        });
    }
}
