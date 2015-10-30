package org.garywzh.doubanzufang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.garywzh.doubanzufang.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String TEST_LOCATION = "海淀";
    private Button button;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSearchView();

        button = (Button) findViewById(R.id.bt_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSearch();
            }
        });
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.onActionViewExpanded();

//        change text color
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.search_text));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.hint_text));

        searchView.setQueryHint("输入地点");
//        test
        searchView.setQuery(TEST_LOCATION, false);

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                beginSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void beginSearch() {
        String location = searchView.getQuery().toString();

        if (location.isEmpty()) {
            Toast.makeText(getBaseContext(), "请输入地点", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = new Intent(getBaseContext(), ResultActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }
}
