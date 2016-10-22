package com.perfecttoilettime.perfecttoilettime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class MailSenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);

        Spinner buildingSpinner = (Spinner) findViewById(R.id.buildingSpinner);
        String building = buildingSpinner.getSelectedItem().toString();

        Spinner floorSpinner = (Spinner) findViewById(R.id.floorSpinner);
        String floor = floorSpinner.getSelectedItem().toString();

        Spinner problemSpinner = (Spinner) findViewById(R.id.problemSpinner);
        String problem = problemSpinner.getSelectedItem().toString();

        Button sendMailBtn = (Button) findViewById(R.id.sendButton);
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    GMailSender sender = new GMailSender("perfecttoilettimeapp@gmail.com","Toiletries*");
                    sender.sendMail("Bathroom Service Request", "Bathroom needs service", "perfecttoilettime@gmail.com", "mark.yankees@gmail.com");
                } catch (Exception e){
                    Log.e("SendMail", e.getMessage(), e);
                }

            }
        });
    }
}
