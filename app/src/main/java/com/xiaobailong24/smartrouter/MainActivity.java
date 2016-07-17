package com.xiaobailong24.smartrouter;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xiaobailong24.smartrouter.Utils.WebViewUpload;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;
    private SwipeRefreshLayout mSwipeLayout;
    private WebView loginWeb;

    private String url = "192.168.1.1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //初始化WebView
        initWebView();

        //初始化Fab
        initFabView();

    }


    // TODO: 2016/5/7 Initialize fab
    private void initFabView() {
        Log.e(TAG, "initFabView()");
        FloatingActionButton backFab = (FloatingActionButton) findViewById(R.id.fab_back);   //上一页
        FloatingActionButton forwardFab = (FloatingActionButton) findViewById(R.id.fab_forward);  //下一页
        if (backFab != null) {
            backFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginWeb.goBack();
                    //                    Snackbar.make(v, "上一页", Snackbar.LENGTH_SHORT)
                    //                            .setAction("Action", null).show();
                }
            });
        }
        if (forwardFab != null) {
            forwardFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginWeb.goForward();
                    //                    Snackbar.make(v, "下一页", Snackbar.LENGTH_SHORT)
                    //                            .setAction("Action", null).show();
                }
            });
        }
    }


    // TODO: 2016/7/10
    private void initWebView() {
        Log.e(TAG, "initWebView()");
        loginWeb = (WebView) findViewById(R.id.login_web);
        loginWeb.setWebViewClient(new MyWebViewClient());

        //设置WebChromeClient
        WebViewUpload mWebViewUpload = new WebViewUpload();
        mWebViewUpload.setmActivity(MainActivity.this);
        mWebViewUpload.setmWebView(loginWeb);
        loginWeb.setWebChromeClient(mWebViewUpload);

        //WebView加载web资源
        Intent mIntent = getIntent();
        url = mIntent.getStringExtra("URL");
        Log.e(TAG, "initWebView: URL-->" + url);
        loginWeb.loadUrl("http://" + url);

        //设置支持JavaScript
        WebSettings webSettings = loginWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //隐藏缩放控制条
        webSettings.setDisplayZoomControls(false);
        //访问文件
        webSettings.setAllowFileAccess(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //设置获取焦点
        loginWeb.setFocusable(true);

        //下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
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

        Log.e(TAG, "initWebView: End");

    }


    // TODO: 2016/7/10
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult jsResult) {
            final JsResult finalJsResult = jsResult;
            new AlertDialog.Builder(view.getContext()).setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finalJsResult.confirm();
                }
            }).setCancelable(false).create().show();
            return true;
        }
        //上传下载文件

    }

    // TODO: 2016/7/10
    public class MyWebViewClient extends WebViewClient {
        //override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            handler.proceed("admin", "19881989");
            Log.e("MyWebViewClient", "onReceivedHttpAuthRequest");
        }

        //override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面

            view.loadUrl(url);
            Log.e("MyWebViewClient", "shouldOverrideUrlLoading");
            // 记得消耗掉这个事件。给不知道的朋友再解释一下，Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉

            return true;

        }
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    // TODO: 2016/7/17
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
