package com.boildcoffee.bfrefreshview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.boildcoffee.bfrefreshview.R;
import com.boildcoffee.bfrefreshview.util.DataUtils;
import com.boildcoffee.library.widget.BfPullToRefreshView;
import com.boildcoffee.library.widget.listener.OnLoadMoreListener;
import com.boildcoffee.library.widget.listener.OnRefreshListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewActivity extends AppCompatActivity {
    BfPullToRefreshView mPullToRefreshView;
    RecyclerView mRecyclerView;
    BaseQuickAdapter<String,BaseViewHolder> mBaseQuickAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        mPullToRefreshView = findViewById(R.id.bfprv);
        mRecyclerView = findViewById(R.id.rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBaseQuickAdapter = new BaseQuickAdapter<String,BaseViewHolder>(R.layout.item,DataUtils.generateData(0,20)) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv,item);
            }
        };
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        mPullToRefreshView.setRefreshListener(new OnRefreshListener() {
            @Override
            public void refreshListener(View view) {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBaseQuickAdapter.setNewData(DataUtils.generateData(0,20));
                        mPullToRefreshView.setRefreshComplete();
                    }
                },3000);
            }
        });
        mPullToRefreshView.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMoreListener(View v) {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBaseQuickAdapter.addData(DataUtils.generateData(20,40));
                        mPullToRefreshView.setLoadMoreComplete();
                    }
                },3000);
            }
        });
    }


}
