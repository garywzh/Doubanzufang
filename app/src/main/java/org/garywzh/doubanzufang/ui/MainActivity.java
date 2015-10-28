package org.garywzh.doubanzufang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.garywzh.doubanzufang.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.et_search);
        button = (Button) findViewById(R.id.bt_search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = editText.getText().toString();

                final Intent intent = new Intent(getBaseContext(), ResultActivity.class);

                intent.putExtra("location", location);

                startActivity(intent);

            }
        });
    }


}
