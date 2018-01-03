package ivring.mycloudreader.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ivring.mycloudreader.R;
import ivring.mycloudreader.utils.PerfectClickListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 是没有title的Fragment
 */
public abstract class BaseFragment<SV extends ViewDataBinding> extends Fragment {

    // 子Fragment的具体内容布局view
    protected SV bindingView;
    // fragment是否显示了
    protected boolean mIsVisible = false;
    // 加载中
    private LinearLayout mLlProgressBar;
    // 加载失败
    private LinearLayout mRefreshError;
    // 内容布局
    protected RelativeLayout mContainer;
    // 加载中动画
    private AnimationDrawable mAnimationDrawable;
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 初始化数据
     *
     * @author IVRING
     * @time 2017/12/29 10:50
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 创建碎片布局
     *
     * @author IVRING
     * @time 2017/12/29 10:51
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, null);
    }

    /**
     * 给碎片布局上的ui控件初始化
     * view就是上个方法的返回值
     *
     * @author IVRING
     * @time 2017/12/29 10:51
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //加载子类Fragment的布局
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), setContent(), null, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        设置子布局为matchparent
        bindingView.getRoot().setLayoutParams(params);
        //获取baseFragment布局的container容器，将子布局加进来
        mContainer = view.findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
    }

    /**
     * 用于添加子Fragment的布局
     *
     * @return
     */
    protected abstract int setContent();

    /**
     * 实现fragment的懒加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //获取当前Fragment是否可见
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    /**
     * fragment不可见时操作
     */
    protected void onInvisible() {
    }

    /**
     * Fragment可见的时候加载数据
     *
     * @author IVRING
     * @time 2017/12/29 11:06
     */
    protected void onVisible() {
        loadData();
    }
    /**
     * 显示时加载数据,需要这样的使用
     * 注意声明 isPrepared，先初始化
     * 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected void loadData() {
    }
    /**
     * 给碎片布局上的Ui控件赋值
     *
     * @author IVRING
     * @time 2017/12/29 10:52
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //加载动画框
        mLlProgressBar = getView(R.id.ll_progress_bar);
        //加载动画
        ImageView imageView = getView(R.id.img_progress);

        //加载动画
        mAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
        //默认进入界面开始动画
        if (!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
        //加载失败布局
        mRefreshError = getView(R.id.ll_error_refresh);
        //点击加载失败布局
        mRefreshError.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //显示加载中状态
                showLoading();
                //
                onRefresh();
            }
        });
    }

    /**
     * 加载失败后操作，在具体Fragment中去重写方法
     */
    protected void onRefresh() {

    }

    /**
      * 显示加载中状态
      * @author IVRING
      * @time 2017/12/29 14:05
      */
    protected void showLoading() {
        //如果加载中布局没有显示，就让他显示出来
        if (mLlProgressBar.getVisibility() != View.VISIBLE){
            mLlProgressBar.setVisibility(View.VISIBLE);
        }
        //开始加载中动画
        if (!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
        //隐藏掉具体子Fragment的布局
        if (bindingView.getRoot().getVisibility() != View.GONE){
            bindingView.getRoot().setVisibility(View.GONE);
        }
        //隐藏加载失败布局
        if (mRefreshError.getVisibility() !=View.GONE){
            mRefreshError.setVisibility(View.GONE);
        }
    }
    /**
      * 加载完成的状态
      * @author IVRING
      * @time 2017/12/29 14:16
      */
    protected void showContentView(){
        //隐藏正在加载框
        if (mLlProgressBar.getVisibility() != View.GONE) {
            mLlProgressBar.setVisibility(View.GONE);
        }
        // 停止正在加载动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        //隐藏加载失败框
        if (mRefreshError.getVisibility() != View.GONE) {
            mRefreshError.setVisibility(View.GONE);
        }
        //显示出子fragment的内容
        if (bindingView.getRoot().getVisibility() != View.VISIBLE) {
            bindingView.getRoot().setVisibility(View.VISIBLE);
        }
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError() {
        if (mLlProgressBar.getVisibility() != View.GONE) {
            mLlProgressBar.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefreshError.getVisibility() != View.VISIBLE) {
            mRefreshError.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }

    /**
     * rxBus绑定监听
     * @param s
     */
    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    /**
     * 移除监听
     */
    public void removeSubscription() {
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
    /**
     * 简化findview
     * @param id
     * @param <T> 泛型
     * @return
     */
    protected <T extends View> T getView(@IdRes int id){
        return (T) getView().findViewById(id);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
