package com.boildcoffee.bfrefreshview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boildcoffee.bfrefreshview.R;
import com.boildcoffee.library.widget.BfPullToRefreshView;
import com.boildcoffee.library.widget.listener.OnRefreshListener;

public class ScrollViewActivity extends AppCompatActivity {
    BfPullToRefreshView mPullToRefreshView;
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);

        mPullToRefreshView = findViewById(R.id.bfprv);
        mScrollView = findViewById(R.id.sc);

        mPullToRefreshView.setRefreshListener(new OnRefreshListener() {
            @Override
            public void refreshListener(View view) {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup viewGroup = (ViewGroup) mScrollView.getChildAt(0);
                        viewGroup.removeAllViews();
                        viewGroup.addView(createTv());
                        mPullToRefreshView.setRefreshComplete();
                    }
                },3000);
            }
        });
    }

    private TextView createTv(){
        TextView tv = new TextView(this);
        tv.setText("aaaaaaaaaa");
        tv.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        return tv;
    }
}
