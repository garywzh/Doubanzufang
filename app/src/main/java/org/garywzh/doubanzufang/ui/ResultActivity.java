package org.garywzh.doubanzufang.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
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

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.helper.CustomTabsHelper;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.ui.adapter.ItemAdapter;
import org.garywzh.doubanzufang.ui.loader.AsyncTaskLoader;
import org.garywzh.doubanzufang.ui.loader.ItemListLoader;
import org.garywzh.doubanzufang.util.LogUtils;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<AsyncTaskLoader.LoaderResult<ResponseBean>>, ItemAdapter.OnItemActionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private ItemAdapter mAdapter;
    private String mLocation;
    public String mSp;
    public boolean requireScrollToTop = false;
    private boolean onLoading;
    public boolean noMore = false;
    private CardView searchCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLocation = getIntent().getStringExtra("location");

        initSearchCard();
        initRecyclerView();

        mSp = "0";

        onLoading = true;
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void initSearchCard() {
        searchCard = (CardView) findViewById(R.id.cv_search);
        searchView = (SearchView) searchCard.findViewById(R.id.searchview);
        searchView.onActionViewExpanded();

//        change text color
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.search_text));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.hint_text));

        searchView.setQueryHint("输入地点");
        searchView.setQuery(mLocation, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    Toast.makeText(getBaseContext(), "请输入地点", Toast.LENGTH_SHORT).show();
                    return false;
                }

                searchView.clearFocus();
                mLocation = query;
                mSp = "0";
                getLoader().setParams(mLocation, mSp);
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
    public Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> onCreateLoader(int id, Bundle args) {
        return new ItemListLoader(this, mLocation);
    }

    @Override
    public void onLoadFinished(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader, AsyncTaskLoader.LoaderResult<ResponseBean> result) {
        if (result.hasException()) {
            Toast.makeText(this, "结果列表加载失败 - 网络错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requireScrollToTop) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
            requireScrollToTop = false;
        }

        mAdapter.setDataSource(result.mResult);

        onLoading = false;
        LogUtils.d(TAG, "onLoadFinished called");
    }

    @Override
    public void onLoaderReset(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader) {
        mAdapter.setDataSource(null);
        LogUtils.d(TAG, "onLoaderReset called");
    }

    private ItemListLoader getLoader() {
        return (ItemListLoader) getSupportLoaderManager().<AsyncTaskLoader.LoaderResult<ResponseBean>>getLoader(0);
    }

    @Override
    public boolean onItemOpen(View view, Item item) {

//        chrome custom tabs
        final Uri uri = Uri.parse(Item.buildUrlFromId(item.tid));
        final CustomTabsIntent.Builder builder = CustomTabsHelper.getBuilder(ResultActivity.this);
        builder.build().launchUrl(ResultActivity.this, uri);

        return true;
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
                if ((pastItems + visibleItemCount) >= (totalItemCount - 10)) {

                    LogUtils.d(TAG, "scrolled to bottom, loading more");
                    onLoading = true;

                    final ItemListLoader loader = getLoader();
                    if (loader == null) {
                        return;
                    }
                    loader.setParams(mLocation, mSp);
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
