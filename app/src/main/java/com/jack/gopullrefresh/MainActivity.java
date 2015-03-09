package com.jack.gopullrefresh;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jack.gopullrefreshlibrary.PullRefreshLayout;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private PullRefreshLayout pullRefreshLayout;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.pull_refresh_layout);
        listView = (ListView) findViewById(R.id.pull_refresh_list);

        String[] arr = new String[20];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = "item " + i;
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr));

        pullRefreshLayout.setPullRefreshListener(new PullRefreshLayout.PullRefreshListener() {
            @Override
            public void onPullDown() {
                Log.d(TAG, "do refreshing...");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPullUp() {
                Log.d(TAG, "do refreshing...");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRefreshFinished() {
                Log.d(TAG, "refresh finsished.");
            }
        });
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
