package org.garywzh.doubanzufang.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.umeng.analytics.MobclickAgent;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.dao.ItemDao;
import org.garywzh.doubanzufang.helper.CustomTabsHelper;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.ui.adapter.FavItemAdapter;
import org.garywzh.doubanzufang.ui.loader.AsyncTaskLoader;
import org.garywzh.doubanzufang.ui.loader.FavItemListLoader;
import org.garywzh.doubanzufang.ui.widget.DividerItemDecoration;
import org.garywzh.doubanzufang.util.LogUtils;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<AsyncTaskLoader.LoaderResult<List<Item>>>, FavItemAdapter.OnItemActionListener {
    private static final String TAG = FavoriteActivity.class.getSimpleName();

    private FavItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new FavItemAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<AsyncTaskLoader.LoaderResult<List<Item>>> onCreateLoader(int id, Bundle args) {
        return new FavItemListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<AsyncTaskLoader.LoaderResult<List<Item>>> loader, AsyncTaskLoader.LoaderResult<List<Item>> result) {
        if (result.hasException()) {
            Toast.makeText(this, "结果列表加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        mAdapter.setDataSource(result.mResult);

        LogUtils.d(TAG, "onLoadFinished called");
    }

    @Override
    public void onLoaderReset(Loader<AsyncTaskLoader.LoaderResult<List<Item>>> loader) {
        mAdapter.setDataSource(null);
        LogUtils.d(TAG, "onLoaderReset called");
    }

    @Override
    public boolean onItemOpen(View view, Item item) {

//        chrome custom tabs
        final Uri uri = Uri.parse(Item.buildUrlFromId(item.tid));
        final CustomTabsIntent.Builder builder = CustomTabsHelper.getBuilder(FavoriteActivity.this);
        builder.build().launchUrl(FavoriteActivity.this, uri);

        return true;
    }

    @Override
    public void onStartActionMode(final MultiSelector multiSelector) {
        startSupportActionMode(new ModalMultiSelectorCallback(multiSelector) {

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                super.onCreateActionMode(actionMode, menu);
                getMenuInflater().inflate(R.menu.menu_action_mode, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    actionMode.finish();

                    List<Item> items = mAdapter.mItems;
                    for (int i = items.size() - 1; i >= 0; i--) {
                        if (multiSelector.isSelected(i, 0)) {
                            ItemDao.remove(items.get(i));
                            items.remove(i);
                        }
                    }
                    mAdapter.notifyDataSetChanged();

                    multiSelector.clearSelections();
                    return true;
                }
                return false;
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
