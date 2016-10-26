package com.perfecttoilettime.perfecttoilettime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class AddBathroomActivity extends AppCompatActivity {

    private Spinner bathroomName;
    private EditText bathroomDescription;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);

        bathroomName = (Spinner) findViewById(R.id.bathroomName);
        bathroomDescription = (EditText) findViewById(R.id.bathroomDescription);
        addBtn = (Button) findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() { //action once the add bathroom button is clicked
            @Override
            public void onClick(View v) {
                String name = bathroomName.getSelectedItem().toString(); //name of bathroom as string from spinner selection
                String description = bathroomDescription.getText().toString(); //description of bathroom from text field
                String longitude = ""; //longitude of bathroom location(always empty for now until figure out how to implement this)
                String latitude = ""; //latitude of bathroom location(always empty for now until figure out how to implement this)
                String urlString ="http://socialgainz.com/Bumpr/PerfectToiletTime/insertBathroom.php?longitude=" +longitude+ "&latitude=" +latitude+ "&name=" +name+ "&description=" +description+ "";
                final String urlString1 = urlString.replaceAll(" ", "%20");
                System.out.println(urlString1);
                try
                {
                    URL url = new URL(urlString1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String re = readStream(in);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    private String readStream(InputStream is){
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        }
        catch (IOException e){
            return "";

        }
    }
}
