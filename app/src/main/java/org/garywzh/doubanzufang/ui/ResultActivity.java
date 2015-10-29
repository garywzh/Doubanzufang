package org.garywzh.doubanzufang.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Toast;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.ui.adapter.ItemAdapter;
import org.garywzh.doubanzufang.ui.loader.AsyncTaskLoader;
import org.garywzh.doubanzufang.ui.loader.ItemListLoader;
import org.garywzh.doubanzufang.utils.LogUtils;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<AsyncTaskLoader.LoaderResult<ResponseBean>>, ItemAdapter.OnItemActionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private SearchView searchView;
    private LinearLayoutManager linearLayoutManager;
    private ItemAdapter mAdapter;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLocation = getIntent().getStringExtra("location");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new ItemAdapter(this);
        recyclerView.setAdapter(mAdapter);

        searchView = (SearchView) findViewById(R.id.searchview);
        searchView.onActionViewExpanded();
        searchView.setQuery(mLocation, false);
        searchView.clearFocus();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = searchView.getQuery().toString();

            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> onCreateLoader(int id, Bundle args) {
        return new ItemListLoader(this, mLocation);
    }

    @Override
    public void onLoadFinished(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader, AsyncTaskLoader.LoaderResult<ResponseBean> result) {
        if (result.hasException()) {
            Toast.makeText(this, "视频列表加载失败 - 网络错误", Toast.LENGTH_SHORT).show();
            return;
        }

        mAdapter.setDataSource(result.mResult.items);
    }

    @Override
    public void onLoaderReset(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader) {
        LogUtils.d(TAG, "onLoaderReset called");
    }

    private ItemListLoader getLoader() {
        return (ItemListLoader) getSupportLoaderManager().<AsyncTaskLoader.LoaderResult<ResponseBean>>getLoader(0);
    }

    @Override
    public boolean onItemOpen(View view, Item topic) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Item.buildUrlFromId(topic.tid)));
        startActivity(i);
        return true;
    }
}
