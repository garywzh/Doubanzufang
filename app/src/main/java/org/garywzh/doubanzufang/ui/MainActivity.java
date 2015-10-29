package org.garywzh.doubanzufang.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.garywzh.doubanzufang.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button button;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("输入地点");
        searchView.clearFocus();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = searchView.getQuery().toString();

                final Intent intent = new Intent(getBaseContext(), ResultActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.bt_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = searchView.getQuery().toString();

                if (location.isEmpty()){
                    Toast.makeText(getBaseContext(), "请输入地点", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Intent intent = new Intent(getBaseContext(), ResultActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });
    }
}
