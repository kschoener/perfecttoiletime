package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.perfecttoilettime.perfecttoilettime.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AddBathroomActivity extends AppCompatActivity {

    private Spinner bathroomName;
    private EditText bathroomDescription;
    private Button addBtn;
    public static final String addBathroomExtra = "addBathroomExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);
        if(getIntent() != null && getIntent().getExtras() != null) {
            final double[] coords;
            coords = getIntent().getDoubleArrayExtra(addBathroomExtra);
            bathroomName = (Spinner) findViewById(R.id.bathroomName);
            bathroomDescription = (EditText) findViewById(R.id.bathroomDescription);
            addBtn = (Button) findViewById(R.id.addBtn);
            addBtn.setOnClickListener(new View.OnClickListener() { //action once the add bathroom button is clicked
                @Override
                public void onClick(View v) {
                    String name = bathroomName.getSelectedItem().toString(); //name of bathroom as string from spinner selection
                    String description = bathroomDescription.getText().toString(); //description of bathroom from text field
                    String longitude = "" + coords[1]; //longitude of bathroom location(always empty for now until figure out how to implement this)
                    String latitude = "" + coords[0]; //latitude of bathroom location(always empty for now until figure out how to implement this)
                    String urlString = "http://socialgainz.com/Bumpr/PerfectToiletTime/insertBathroom.php?longitude=" + longitude + "&latitude=" + latitude + "&name=" + name + "&description=" + description + "";
                    final String urlString1 = urlString.replaceAll(" ", "%20");
                    final WebView view = (WebView) findViewById(R.id.connection);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(urlString1);
                        }
                    });
                }
            });
        }
    }
/*
    public class JSONAsyncTask extends AsyncTask<Void, Void, String> {
        private String mUrl;
        private AddBathroomActivity mRef;

        public JSONAsyncTask(String url, AddBathroomActivity ref) {
            mUrl = url;
            mRef = ref;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString = null;
            resultString = getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            mRef.addBathroomMarkers(strings);

        }

        private String getJSON(String url) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
//                            sb.append(line+"\n");
                            sb.append(line);
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (Exception ex) {
                return ex.toString();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        //disconnect error
                    }
                }
            }
            return null;
        }
    }
    //end of async task class
    */
}
