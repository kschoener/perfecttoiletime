package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.perfecttoilettime.perfecttoilettime.R;


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
}
