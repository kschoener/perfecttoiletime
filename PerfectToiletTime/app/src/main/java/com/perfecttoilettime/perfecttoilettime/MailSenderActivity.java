package com.perfecttoilettime.perfecttoilettime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MailSenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);

        Button sendMailBtn = (Button) findViewById(R.id.sendButton);
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    GMailSender sender = new GMailSender("perfecttoilettimeapp@gmail.com","Toiletries*");
                    sender.sendMail("This is subject", "This is body", "perfecttoilettime@gmail.com", "mark.yankees@gmail.com");
                } catch (Exception e){
                    Log.e("SendMail", e.getMessage(), e);
                }

            }
        });
    }
}
