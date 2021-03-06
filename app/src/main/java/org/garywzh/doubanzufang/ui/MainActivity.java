package org.garywzh.doubanzufang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

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

        searchView.setQueryHint(getString(R.string.hint_searchview));
//        test
//        searchView.setQuery(TEST_LOCATION, false);

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
            Toast.makeText(getBaseContext(), getString(R.string.toast_searchview_tip), Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = new Intent(getBaseContext(), ResultActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_fav:
                final Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
