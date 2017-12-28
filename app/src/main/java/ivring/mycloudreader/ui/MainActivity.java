package ivring.mycloudreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import ivring.mycloudreader.R;
import ivring.mycloudreader.adapter.MyFragmentPagerAdapter;
import ivring.mycloudreader.app.ConstantsImageUrl;
import ivring.mycloudreader.databinding.ActivityMainBinding;
import ivring.mycloudreader.databinding.NavHeaderMainBinding;
import ivring.mycloudreader.utils.CommonUtils;
import ivring.mycloudreader.utils.ImgLoadUtil;
import ivring.mycloudreader.utils.PerfectClickListener;
import ivring.mycloudreader.utils.SPUtils;
import ivring.mycloudreader.view.statusBar.StatusBarUtil;

public class MainActivity extends AppCompatActivity{
    private FrameLayout llTitleMenu;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private ViewPager vpContent;

    // 一定需要对应的bean
    private ActivityMainBinding mBinding;
    //抽屉的布局
    private NavHeaderMainBinding bind;
    private ImageView llTitleGank;
    private ImageView llTitleOne;

    private ImageView llTitleDou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        initStatusView();
        initId();
//        initRxBus();
//      用于设置抽屉NavigationView拉出来的时候状态栏样式为透明
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, drawerLayout,
                CommonUtils.getColor(R.color.colorTheme));
//      初始化Fragment
        initContentFragment();
//      設置toolBar
        initToolBar();
//      加载抽屉布局
        initDrawerLayout();
//      设置Toolbar和FloatButton监听
        initListener();
    }

    private void initStatusView() {
//        ViewGroup.LayoutParams layoutParams = mBinding.include.viewStatus.getLayoutParams();
//        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
//        mBinding.include.viewStatus.setLayoutParams(layoutParams);
    }

    /**
     * 初始化控件
     */
    private void initId() {
        drawerLayout = mBinding.drawerLayout;
        navView = mBinding.navView;
        fab = mBinding.include.fab;
        toolbar = mBinding.include.toolbar;
        llTitleMenu = mBinding.include.llTitleMenu;
        vpContent = mBinding.include.vpContent;
        fab.setVisibility(View.GONE);

        llTitleGank = mBinding.include.ivTitleGank;
        llTitleOne = mBinding.include.ivTitleOne;
        llTitleDou = mBinding.include.ivTitleDou;
    }

    /**
     * 绑定Toolbar和浮动按钮监听器
     */
    private void initListener() {
        //Toolbar三杠图标
        llTitleMenu.setOnClickListener(onClickListener);
        //toolBar三个按钮
        mBinding.include.ivTitleGank.setOnClickListener(onClickListener);
        mBinding.include.ivTitleDou.setOnClickListener(onClickListener);
        mBinding.include.ivTitleOne.setOnClickListener(onClickListener);
        //右下角浮动按钮
        fab.setOnClickListener(onClickListener);
    }

    /**
     * 抽屉的布局加载
     * inflateHeaderView 进来的布局要宽一些
     */
    private void initDrawerLayout() {
        //设置抽屉盒和Toolbar上边的图标配合,这个默认只是设置左侧的抽屉，如果在右侧就需要自定义，重写 onOptionsItemSelected
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, //Activity上下文对象
                drawerLayout,  //抽屉
                toolbar,  //toobar
                R.string.app_name, //
                R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //加载抽屉布局
        navView.inflateHeaderView(R.layout.nav_header_main);
        //NavigationView获取内部view的方法，这里就是NestedScrollView
        View headerView = navView.getHeaderView(0);
        //初始化抽屉具体布局。
        bind = DataBindingUtil.bind(headerView);
        //dataBinding为加载进来的布局设置绑定方法
        bind.setListener(this);
        //用于做夜间模式的，目前没有
        bind.dayNightSwitch.setChecked(SPUtils.getNightMode());
        //加载圆形头像
        ImgLoadUtil.displayCircle(bind.ivAvatar, ConstantsImageUrl.IC_AVATAR);
        //设置点击事件
        bind.llNavExit.setOnClickListener(onClickListener);
        bind.ivAvatar.setOnClickListener(onClickListener);

        bind.llNavHomepage.setOnClickListener(listener);
        bind.llNavScanDownload.setOnClickListener(listener);
        bind.llNavDeedback.setOnClickListener(listener);
        bind.llNavAbout.setOnClickListener(listener);
        bind.llNavLogin.setOnClickListener(listener);
    }

    private void initContentFragment() {
        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new BlankFragment());
        mFragmentList.add(new BlankFragment());
        mFragmentList.add(new BlankFragment());
        // 注意使用的是：getSupportFragmentManager
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        vpContent.setAdapter(adapter);
        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(onPageChangeListener);
        mBinding.include.ivTitleGank.setSelected(true);
        vpContent.setCurrentItem(0);
    }

    /**
     * 设置toolbar
     */
    private void initToolBar() {
//        设置toolbar支持ActionBar功能
        setSupportActionBar(toolbar);
//        这时获取ActionBar就是Toolbar了
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * 用于Viewpager监听
     */
    private ViewPager.OnPageChangeListener onPageChangeListener =  new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    //设置toolbar上的按钮跟随变化
                    llTitleGank.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleDou.setSelected(false);
                    break;
                case 1:
                    llTitleOne.setSelected(true);
                    llTitleGank.setSelected(false);
                    llTitleDou.setSelected(false);
                    break;
                case 2:
                    llTitleDou.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleGank.setSelected(false);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    /**
     * 用于抽屉上的按钮，点击完后跳转，并关闭的
     */
    private PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(final View v) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            mBinding.drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
//                        case R.id.ll_nav_homepage:// 主页
//                            NavHomePageActivity.startHome(MainActivity.this);
//                            break;
//                        case R.id.ll_nav_scan_download://扫码下载
//                            NavDownloadActivity.start(MainActivity.this);
//                            break;
//                        case R.id.ll_nav_deedback:// 问题反馈
//                            NavDeedBackActivity.start(MainActivity.this);
//                            break;
//                        case R.id.ll_nav_about:// 关于云阅
//                            NavAboutActivity.start(MainActivity.this);
//                            break;
//                        case R.id.ll_nav_login:// 登录GitHub账号
//                            WebViewActivity.loadUrl(v.getContext(), "https://github.com/login", "登录GitHub账号");
//                            break;
                    }
                }
            }, 260);
        }
    };
    /**
     * 用于点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_title_menu:// 开启菜单
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.iv_title_gank:// 干货栏
                    if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                        llTitleGank.setSelected(true);
                        llTitleOne.setSelected(false);
                        llTitleDou.setSelected(false);
                        vpContent.setCurrentItem(0);
                    }
                    break;
                case R.id.iv_title_one:// 电影栏
                    if (vpContent.getCurrentItem() != 1) {
                        llTitleOne.setSelected(true);
                        llTitleGank.setSelected(false);
                        llTitleDou.setSelected(false);
                        vpContent.setCurrentItem(1);
                    }
                    break;
                case R.id.iv_title_dou:// 书籍栏
                    if (vpContent.getCurrentItem() != 2) {
                        llTitleDou.setSelected(true);
                        llTitleOne.setSelected(false);
                        llTitleGank.setSelected(false);
                        vpContent.setCurrentItem(2);
                    }
                    break;
                case R.id.iv_avatar: // 头像进入GitHub
//                WebViewActivity.loadUrl(v.getContext(),CommonUtils.getString(R.string.string_url_cloudreader),"CloudReader");
                    break;
                case R.id.ll_nav_exit:// 退出应用
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 夜间模式待完善
     */
    public boolean getNightMode() {
        return SPUtils.getNightMode();
    }

    public void onNightModeClick(View view) {
        if (!SPUtils.getNightMode()) {
//            SkinCompatManager.getInstance().loadSkin(Constants.NIGHT_SKIN);
        } else {
            // 恢复应用默认皮肤
//            SkinCompatManager.getInstance().restoreDefaultTheme();
        }
        SPUtils.setNightMode(!SPUtils.getNightMode());
        bind.dayNightSwitch.setChecked(SPUtils.getNightMode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_search:
////                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * 返回键，当抽屉打开时先关抽屉
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 每日推荐点击"新电影热映榜"跳转
     */
//    private void initRxBus() {
//        RxBus.getDefault().toObservable(RxCodeConstants.JUMP_TYPE_TO_ONE, RxBusBaseMessage.class)
//                .subscribe(new Action1<RxBusBaseMessage>() {
//                    @Override
//                    public void call(RxBusBaseMessage integer) {
//                        mBinding.include.vpContent.setCurrentItem(1);
//                    }
//                });
//    }
}
