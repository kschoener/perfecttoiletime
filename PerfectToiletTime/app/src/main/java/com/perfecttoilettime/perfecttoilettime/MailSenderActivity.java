package com.perfecttoilettime.perfecttoilettime;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.os.AsyncTask;
import android.widget.Toast;

public class MailSenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);

        Button sendMailBtn = (Button) findViewById(R.id.sendButton);
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GMailSender sender = new GMailSender("perfecttoilettimeapp@gmail.com","Toiletries*", MailSenderActivity.this);
                new AsyncTask<Void, Void, Void>(){
                    Spinner buildingSpinner = (Spinner) findViewById(R.id.buildingSpinner);
                    String building = buildingSpinner.getSelectedItem().toString();

                    Spinner floorSpinner = (Spinner) findViewById(R.id.floorSpinner);
                    String floor = floorSpinner.getSelectedItem().toString();

                    Spinner problemSpinner = (Spinner) findViewById(R.id.problemSpinner);
                    String problem = problemSpinner.getSelectedItem().toString();
                    private ProgressDialog progressDialog;
                    private Context context = sender.getContext();
                    @Override protected void onPreExecute(){
                        super.onPreExecute();
                        progressDialog = ProgressDialog.show(context,"Sending email","Please Wait...",false,false);
                    }
                    @Override protected void onPostExecute(Void aVoid){
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        Toast.makeText(context,"Message Sent!",Toast.LENGTH_LONG).show();
                    }
                    @Override public Void doInBackground(Void... arg) {
                        try {
                            sender.sendMail("Bathroom Service Request", "Hi, the bathroom at " + building + " floor " + floor + " needs service. Problem: " + problem + ". Thanks!", "perfecttoilettimeapp@gmail.com", "mark.yankees@gmail.com");
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    return null;
                    }
                }.execute();
            }
        });
    }
}
