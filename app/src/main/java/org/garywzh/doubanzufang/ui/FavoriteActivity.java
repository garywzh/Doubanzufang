package org.garywzh.doubanzufang.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.umeng.analytics.MobclickAgent;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.dao.ItemDao;
import org.garywzh.doubanzufang.helper.CustomTabsHelper;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.ui.adapter.FavItemAdapter;
import org.garywzh.doubanzufang.ui.widget.DividerItemDecoration;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavoriteActivity extends AppCompatActivity implements FavItemAdapter.OnItemActionListener {
    private static final String TAG = FavoriteActivity.class.getSimpleName();
    private FavItemAdapter mAdapter;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new FavItemAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSubscription = Observable
                .create(new Observable.OnSubscribe<List<Item>>() {
                    @Override
                    public void call(Subscriber<? super List<Item>> subscriber) {
                        subscriber.onNext(ItemDao.getItems());
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Item>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Item> items) {
                        mAdapter.setDataSource(items);
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
    protected void onStop() {
        super.onStop();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
