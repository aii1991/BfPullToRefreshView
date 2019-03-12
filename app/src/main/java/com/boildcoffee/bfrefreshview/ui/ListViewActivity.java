package com.boildcoffee.bfrefreshview.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.boildcoffee.bfrefreshview.R;
import com.boildcoffee.bfrefreshview.util.DataUtils;
import com.boildcoffee.library.widget.BfPullToRefreshView;
import com.boildcoffee.library.widget.listener.OnLoadMoreListener;
import com.boildcoffee.library.widget.listener.OnRefreshListener;

import java.util.List;

public class ListViewActivity extends AppCompatActivity {
    private MyAdapter mAdapter;
    private BfPullToRefreshView mBfPullToRefreshView;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mList = DataUtils.generateData(0,20);

        ListView mListView = findViewById(R.id.lv);
        mAdapter = new MyAdapter(this,mList);
        mListView.setAdapter(mAdapter);

        mBfPullToRefreshView = findViewById(R.id.bfprv);
        mBfPullToRefreshView.setRefreshListener(new OnRefreshListener() {
            @Override
            public void refreshListener(View view) {
                mBfPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.clear();
                        mList.addAll(DataUtils.generateData(0,20));
                        mAdapter.notifyDataSetChanged();
                        mBfPullToRefreshView.setRefreshComplete();
                    }
                },2000);
            }
        });
        mBfPullToRefreshView.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMoreListener(View v) {
                mBfPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.addAll(DataUtils.generateData(20,40));
                        mAdapter.notifyDataSetChanged();
                        mBfPullToRefreshView.setLoadMoreComplete();
                    }
                },3000);
            }
        });

    }

    static class MyAdapter extends BaseAdapter{
        private List<String> mList;
        private Context mContext;

        public MyAdapter(Context context,List<String> mList) {
            this.mList = mList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);

                viewHolder = new ViewHolder();
                viewHolder.mTextView = convertView.findViewById(R.id.tv);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTextView.setText(mList.get(position));
            return convertView;
        }
    }

    static class ViewHolder{
        TextView mTextView;
    }

}
