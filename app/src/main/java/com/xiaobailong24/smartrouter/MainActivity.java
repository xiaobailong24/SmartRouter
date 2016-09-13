package com.xiaobailong24.smartrouter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.xiaobailong24.library.View.AdvancedWebView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_back)
    FloatingActionButton backFab;
    @BindView(R.id.fab_forward)
    FloatingActionButton forwardFab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.login_web)
    AdvancedWebView loginWeb;

    private String url = "192.168.1.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //初始化WebView
        initWebView();

        //初始化Fab
        initFabView();

    }


    private void initFabView() {
        Logger.d(TAG, "initFabView: Start");

        //下一页
        if (backFab != null) {
            backFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginWeb.goBack();
                }
            });
        }
        if (forwardFab != null) {
            forwardFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginWeb.goForward();
                }
            });
        }
    }


    private void initWebView() {
        Logger.d(TAG, "initWebView: ");

        //WebView加载web资源
        //设置获取焦点
        loginWeb.setFocusable(true);
        Intent mIntent = getIntent();
        url = mIntent.getStringExtra("URL");
        Logger.e(TAG, "initWebView: URL-->" + url);
        loginWeb.loadUrl("http://" + url);

        //下拉刷新
        //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        mSwipeLayout.setProgressViewOffset(true, 0, 100);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginWeb.reload();
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        Logger.d(TAG, "initWebView: End");
    }


    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            doExitApp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Logger.d(TAG, "onOptionsItemSelected: action_about-->" + "AppUpdater");
            new AppUpdater(this)
                    .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                    .start();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loginWeb.loadUrl("http://" + url);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_send) {
            sendAdvice();
        } else if (id == R.id.nav_rate) {
            rateApp();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 分享
     */
    public void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, MainActivity.this.getTitle()
                + "  ---" + "xiaobailong24");//分享的标题
        shareIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_text) + "\n"
                + "https://play.google.com/store/apps/details?id=" + getPackageName());//分享的内容
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, getText(R.string.nav_share)));
    }


    /**
     * 发送反馈
     */
    public void sendAdvice() {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("mailto:xiaobailong24@gmail.com"));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.mail_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(sendIntent);
    }

    /**
     * 评分
     */
    public void rateApp() {

        // 建立一個Intent - 在這個Intent 上使用 Google Play Store 的連結
        // E.G. market://details?id=
        // 之後用 getPackageName 這個功能來取後這個程式的 Namespace.
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));

        try {
            // 之後開始一個新的Activity 去這個Intent
            startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            // 如果有錯誤的話 使用正常的網址來連接到 Google Play Store的網頁
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

}
