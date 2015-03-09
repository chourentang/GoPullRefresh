package com.jack.gopullrefreshlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * pull to refresh layout with listview in content
 * Created by jack on 15-3-9.
 */
public class ListViewPullRefreshLayout extends PullRefreshLayout {
//    private static final String TAG = ListViewPullRefreshLayout.class.getSimpleName();

    public ListViewPullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isTop() {
        ListView listView = (ListView)getContentView();
        return listView.getFirstVisiblePosition() == 0;
    }

    @Override
    public boolean isBottom() {
        ListView listView = (ListView)getContentView();
        int size = listView.getAdapter().getCount();
        return listView.getLastVisiblePosition() == size -1;
    }

}
