package com.perfecttoilettime.perfecttoilettime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


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
            }
        });
    }
}
