package com.lyue.aw_an;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.an.an_base.mvp.base.SuperActivity;
import com.an.an_base.mvp.view.DCustomDialog;
import com.an.an_base.struts.utils.DUtilsDialog;
import com.cundong.recyclerview.RecyclerViewUtils;
import com.lyue.aw_an.activity.BluetoothUi;
import com.lyue.aw_an.activity.CoordinatorLayoutActivity;
import com.lyue.aw_an.activity.EndlessGridLayoutActivity;
import com.lyue.aw_an.activity.EndlessLinearLayoutActivity;
import com.lyue.aw_an.activity.EndlessStaggeredGridLayoutActivity;
import com.lyue.aw_an.activity.LinearLayoutActivity;
import com.lyue.aw_an.activity.PachongActivity;

import java.util.ArrayList;

/*
* 类的声明次序依次是：TAG类--View控件--静态成员变量--普通类成员变量
* public 在最前方的位置。
* private 在public的位置后面。
* */
public class MainActivity extends SuperActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static String TIME_CHANGED_ACTION = "com.lyue.aw_an.TIME_CHANGED_ACTION";
    private Button btn2;
    private Button btn;
    private Button btnCoord;
    private LinearLayout anLlBack;
    private TextView anTvTitle;
    private RecyclerView mRecyclerView = null;
    private DrawerLayout drawerLayout;
    private NavigationView nv_main_navigation;
    private static TextView time = null;

    private static ProgressDialog mProgress;

    private DCustomDialog dialog;
    private DUtilsDialog dUtilsDialog;
    private Dialog mDialog;
    private Intent timeService = null;
    UITimeReceiver receiver;
    private DataAdapter mDataAdapter = null;
    private ArrayList<ListItem> mDataList = null;

    private static final String[] TITLE = {"LinearLayoutSample", "EndlessLinearLayoutActivity", "EndlessGridLayoutActivity", "EndlessStaggeredGridLayoutActivity"};
    private static final Class<?>[] ACTIVITY = {LinearLayoutActivity.class, EndlessLinearLayoutActivity.class, EndlessGridLayoutActivity.class, EndlessStaggeredGridLayoutActivity.class};


    public static ProgressDialog getmProgress() {
        return mProgress;
    }

    public void setmProgress(ProgressDialog mProgress) {
        this.mProgress = mProgress;
    }


    public static TextView getTime() {
        return time;
    }

    public static void setTime(TextView time) {
        MainActivity.time = time;
    }

    @Override
    public void initView() {
        setContentView(R.layout.sst_activity_main);
        mProgress = new ProgressDialog(MainActivity.this);
        dialog = new DCustomDialog(this, R.style.AnCustomDialog, "loading...");
        dUtilsDialog = DUtilsDialog.getInstance();
        mDialog = createLoadingDialog(MainActivity.this, "loading...", true);
        btn2 = (Button) findViewById(R.id.btn2);
        btn = (Button) findViewById(R.id.btn);
        btnCoord = (Button) findViewById(R.id.btnCoord);
        anLlBack = (LinearLayout) findViewById(R.id.anLlBack);
        anTvTitle = (TextView) findViewById(R.id.anTvTitle);
        time = (TextView) findViewById(R.id.textView);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        nv_main_navigation = (NavigationView) findViewById(R.id.nv_main_navigation);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn2.setOnClickListener(this);
        btn.setOnClickListener(this);
        btnCoord.setOnClickListener(this);
        anLlBack.setVisibility(View.INVISIBLE);
        anTvTitle.setText("Aw_an");
        anTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT, true);
            }
        });
        setupDrawerContent(nv_main_navigation);
        mDataList = new ArrayList<>();
        for (int i = 0; i < TITLE.length; i++) {
            ListItem item = new ListItem();
            item.title = TITLE[i];
            item.activity = ACTIVITY[i];
            mDataList.add(item);
        }

        registerBroadcastReceiver();
        startTimeService();

        mDataAdapter = new DataAdapter(this);
        mDataAdapter.setData(mDataList);
        mRecyclerView.setAdapter(mDataAdapter);
    }

    protected Dialog createLoadingDialog(Context context, String msg, boolean isCancle) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(com.an.an_base.R.layout.base_progress_dialogutils_standard, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(com.an.an_base.R.id.anLlHeadView);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(com.an.an_base.R.id.anPsUtilsIv);
        TextView tipTextView = (TextView) v.findViewById(com.an.an_base.R.id.anPsUtilsTv);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, com.an.an_base.R.anim.base_anim_loading);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, com.an.an_base.R.style.AnCustomDialogUtils);
        loadingDialog.setCancelable(isCancle);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    @Override
    public void onNetChange(int netModile) {
        // TODO Auto-generated method stub
        //在这个判断，根据需要做处理,判断网络的变化
        if (netModile == 1) {
//            wangluo.setText("inspectNet：连接wifi");
            System.out.println("inspectNet：连接wifi");
        } else if (netModile == 0) {
//            wangluo.setText("inspectNet:连接移动数据");
            System.out.println("inspectNet:连接移动数据");
        } else if (netModile == -1) {
//            wangluo.setText("inspectNet:当前没有网络");
            System.out.println("inspectNet:当前没有网络");
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        Toast.makeText(getApplication(), menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
    }

    private void registerBroadcastReceiver() {
        receiver = new UITimeReceiver();
        IntentFilter filter = new IntentFilter(TIME_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    private void startTimeService() {
        timeService = new Intent(this, MyService.class);
        startService(timeService);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                Intent it = new Intent(this, BluetoothUi.class);
                startActivity(it);
                break;
            case R.id.btn2:
                showMessage("正在扫描...");
                break;
            case R.id.btnCoord:
                mDialog.show();
                Intent intent = new Intent(this, CoordinatorLayoutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void showMessage(String msg) {
        mProgress.setMessage(msg);
        mProgress.setCancelable(false);
        mProgress.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message m = mHandler.obtainMessage();
                m.what = 5;
                mHandler.sendMessage(m);
            }
        }, 1000);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(getApplicationContext(), PachongActivity.class);
            startActivity(intent);
            mProgress.dismiss();
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    private static class ListItem {
        public String title;
        public Class<?> activity;
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
                        dialog.show();
                        ListItem listItem = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, ViewHolder.this));
                        startActivity(new Intent(MainActivity.this, listItem.activity));
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
            dialog.dismiss();
            mDialog.dismiss();
        }
    }

    public interface BluetoothListener {
        void addDevice(BluetoothDevice device);

        void connectSuccess();

        void disconnect();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
        stopService(timeService);
    }

}
