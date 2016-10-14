package com.lyue.aw_an.activity;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.an.an_base.mvp.base.SuperActivity;
import com.lyue.aw_an.R;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：CoordinatorLayoutActivity
 * @文件作者：staryumou@163.com
 * @创建时间：2016/10/12
 * @文件描述：null
 * @修改历史：2016/10/12
 **********************************************************/
public class CoordinatorLayoutActivity extends SuperActivity {

    private static final String[] TITLE = {"LinearLayoutSample", "EndlessLinearLayoutActivity", "EndlessGridLayoutActivity", "EndlessStaggeredGridLayoutActivity", "EndlessLinearLayoutActivity", "EndlessLinearLayoutActivity", "EndlessLinearLayoutActivity", "EndlessLinearLayoutActivity", "LinearLayoutSample", "EndlessLinearLayoutActivity", "EndlessGridLayoutActivity", "LinearLayoutSample", "EndlessLinearLayoutActivity", "EndlessGridLayoutActivity", "LinearLayoutSample", "EndlessLinearLayoutActivity", "EndlessGridLayoutActivity"};

    private RecyclerView mRecyclerView = null;
    private DataAdapter mDataAdapter = null;

    private ArrayList<ListItem> mDataList = null;

    @Override
    public void initView() {
        setContentView(R.layout.sst_activity_coordinator);
//        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mDataList = new ArrayList<>();
//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("我的课程");
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        for (int i = 0; i < TITLE.length; i++) {
//            ListItem item = new ListItem();
//            item.title = TITLE[i];
//            mDataList.add(item);
//        }
//        mDataAdapter = new DataAdapter(this);
//        mDataAdapter.setData(mDataList);
//        mRecyclerView.setAdapter(mDataAdapter);
    }

    private static class ListItem {
        public String title;
    }

    private class DataAdapter extends RecyclerView.Adapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<ListItem> mDataList = new ArrayList<>();

        public DataAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<ListItem> list) {
            this.mDataList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.sample_item_text, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ListItem listItem = mDataList.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.textView.setText(listItem.title);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.info_text);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ListItem listItem = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, MainActivity.DataAdapter.ViewHolder.this));
//                        startActivity(new Intent(MainActivity.this, listItem.activity));
                    }
                });
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

        } else {
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
