package com.perfecttoilettime.perfecttoilettime.frontEnd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.perfecttoilettime.perfecttoilettime.PerfectToiletTime;
import com.perfecttoilettime.perfecttoilettime.R;

public class addinfoActivity extends AppCompatActivity {
    EditText eText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addinfo);
        eText = (EditText) findViewById(R.id.editText);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = eText.getText().toString();
                Toast msg = Toast.makeText(getBaseContext(),str,Toast.LENGTH_LONG);
                msg.show();
            }
        });

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }
        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        if (checkBox2.isChecked()) {
            checkBox2.setChecked(false);
        }
        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        if (checkBox3.isChecked()) {
            checkBox3.setChecked(false);
        }
        final CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        if (checkBox4.isChecked()) {
            checkBox4.setChecked(false);
        }
        final CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        if (checkBox5.isChecked()) {
            checkBox5.setChecked(false);
        }

    }
}
