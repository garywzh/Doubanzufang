package org.garywzh.doubanzufang.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.garywzh.doubanzufang.MyApplication;
import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.dao.ItemDao;
import org.garywzh.doubanzufang.helper.CustomTabsHelper;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.network.NetworkHelper;
import org.garywzh.doubanzufang.ui.adapter.ItemAdapter;
import org.garywzh.doubanzufang.ui.widget.DividerItemDecoration;
import org.garywzh.doubanzufang.util.ExecutorUtils;
import org.garywzh.doubanzufang.util.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ResultActivity extends AppCompatActivity implements ItemAdapter.OnItemActionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayoutManager linearLayoutManager;
    private View rootView;
    private SearchView searchView;
    private ItemAdapter mAdapter;
    private String mLocation;
    public String mSp;
    public boolean requireScrollToTop = false;
    private boolean onLoading;
    public boolean noMore = false;
    private CardView searchCard;

    private List<Item> mItems;
    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLocation = getIntent().getStringExtra("location");
        mSp = "0";
        mItems = new ArrayList<>();

        rootView = findViewById(R.id.rootview);
        initSearchCard();
        initRecyclerView();
    }

    private void initSearchCard() {
        searchCard = (CardView) findViewById(R.id.cv_search);
        searchView = (SearchView) searchCard.findViewById(R.id.searchview);
        searchView.onActionViewExpanded();

//        change text color
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.search_text));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.hint_text));

        searchView.setQueryHint(getString(R.string.hint_searchview));
        searchView.setQuery(mLocation, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    Toast.makeText(getBaseContext(), R.string.toast_searchview_tip, Toast.LENGTH_SHORT).show();
                    return false;
                }

                searchView.clearFocus();
                mLocation = query;
                mSp = "0";
                loadData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new ItemAdapter(this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new HidingScrollListener() {

            @Override
            public void onMoved(int distance) {
                searchCard.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                searchCard.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void onHide() {
                searchCard.animate().translationY(-mSearchViewHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        mSubscription = NetworkHelper.getItemsService()
                .getItems(mLocation, mSp)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        onLoading = true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<ResponseBean>() {
                    @Override
                    public void call(ResponseBean responseBean) {
                        if (mSp.equals("0")) {
                            mItems.clear();
                            requireScrollToTop = true;
                        }

                        final List<Item> items = responseBean.items;

                        if (items.size() == 0) {
                            noMore = true;
                        } else {
                            if (items.size() < 100) {
                                noMore = true;
                            }
                            mSp = items.get(items.size() - 1).tid;
                            mItems.addAll(items);

                            /*remove duplicates*/
                            final Set<Item> setItems = new LinkedHashSet<>(mItems);
                            mItems.clear();
                            mItems.addAll(setItems);

                            responseBean.items = mItems;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mAdapter.setShowProgressBar(false);
                        Toast.makeText(MyApplication.getInstance(), R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ResponseBean responseBean) {
                        if (requireScrollToTop) {
                            linearLayoutManager.scrollToPositionWithOffset(0, 0);
                            requireScrollToTop = false;
                        }

                        if (noMore) {
                            mAdapter.setShowProgressBar(false);
                        }
                        mAdapter.setDataSource(responseBean);
                        onLoading = false;
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
        final CustomTabsIntent.Builder builder = CustomTabsHelper.getBuilder(ResultActivity.this);
        builder.build().launchUrl(ResultActivity.this, uri);

        return true;
    }

    @Override
    public void onItemLongClick(View view, final Item item) {

        ExecutorUtils.execute(new Runnable() {
            @Override
            public void run() {
                ItemDao.put(item);
            }
        });

        Snackbar.make(rootView, R.string.SNACK_FAV, Snackbar.LENGTH_LONG).setAction(R.string.ACTION_UNDO, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        ItemDao.remove(item);
                    }
                });
            }
        }).show();
    }

    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

        private static final float THRESHOLD = 90;

        private int mSearchVIewOffset = 0;
        public int mSearchViewHeight = 160;
        private int mTotalScrolledDistance;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mTotalScrolledDistance < mSearchViewHeight) {
                    setVisible();
                } else {
                    if (mSearchVIewOffset > THRESHOLD) {
                        setInvisible();
                    } else {
                        setVisible();
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if ((mSearchVIewOffset < mSearchViewHeight && dy > 0) || (mSearchVIewOffset > 0 && dy < 0)) {
                mSearchVIewOffset += dy;
                clipSearchViewOffset();
                onMoved(mSearchVIewOffset);
            }

            mTotalScrolledDistance += dy;

            if (!onLoading && !noMore) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastItems = linearLayoutManager.findFirstVisibleItemPosition();
                if ((pastItems + visibleItemCount) >= (totalItemCount - 5)) {

                    LogUtils.d(TAG, "scrolled to bottom, loading more");
                    onLoading = true;
                    loadData();
                }
            }
        }

        private void clipSearchViewOffset() {
            if (mSearchVIewOffset > mSearchViewHeight) {
                mSearchVIewOffset = mSearchViewHeight;
            } else if (mSearchVIewOffset < 0) {
                mSearchVIewOffset = 0;
            }
        }

        private void setVisible() {
            if (mSearchVIewOffset > 0) {
                onShow();
                mSearchVIewOffset = 0;
            }
        }

        private void setInvisible() {
            if (mSearchVIewOffset < mSearchViewHeight) {
                onHide();
                mSearchVIewOffset = mSearchViewHeight;
            }
        }

        public abstract void onMoved(int distance);

        public abstract void onShow();

        public abstract void onHide();
    }
}
