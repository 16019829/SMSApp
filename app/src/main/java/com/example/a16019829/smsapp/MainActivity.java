package com.example.a16019829.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    Button btnSendMsg;
    EditText etTo;
    EditText etContent;

    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = findViewById(R.id.buttonSend);
        btnSendMsg = findViewById(R.id.buttonSendMsg);
        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);

        checkPermission();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        filter.addAction("com.example.broadcast.SMS_RECEIVED");
        this.registerReceiver(br, filter);


        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String phone = etTo.getText().toString();
                String msg = etContent.getText().toString();
                StringTokenizer token = new StringTokenizer(phone, ",");

                while (token.hasMoreElements()){
                    String mobileNo = (String)token.nextElement();
                    if (mobileNo.length()> 0 && msg.trim().length() > 0) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(mobileNo, null, msg, null, null);
                    }
                }
            }
        });
        btnSendMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
    private void sendSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            Uri sms_uri = Uri.parse("smsto:" + etTo.getText().toString());
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sendIntent.putExtra("sms_body", etContent.getText().toString());

            startActivity(sendIntent);

        }
        else {
            String phoneNo = etTo.getText().toString();
            String message = etContent.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("address", phoneNo);
            intent.putExtra("sms_body", message);
            startActivity(intent);
        }
    }
}
