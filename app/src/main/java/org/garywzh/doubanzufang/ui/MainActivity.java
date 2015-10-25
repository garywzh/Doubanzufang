package org.garywzh.doubanzufang.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.ui.loader.AsyncTaskLoader;
import org.garywzh.doubanzufang.ui.loader.ItemListLoader;
import org.garywzh.doubanzufang.utils.LogUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<AsyncTaskLoader.LoaderResult<ResponseBean>> {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> onCreateLoader(int id, Bundle args) {
        return new ItemListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader, AsyncTaskLoader.LoaderResult<ResponseBean> result) {
        if (result.hasException()) {
            Toast.makeText(this, "视频列表加载失败 - 网络错误", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncTaskLoader.LoaderResult<ResponseBean>> loader) {
        LogUtils.d(TAG, "onLoaderReset called");
    }

    private ItemListLoader getLoader() {
        return (ItemListLoader) getSupportLoaderManager().<AsyncTaskLoader.LoaderResult<ResponseBean>>getLoader(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
