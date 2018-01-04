package ivring.mycloudreader.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.Random;

import ivring.mycloudreader.R;
import ivring.mycloudreader.app.ConstantsImageUrl;
import ivring.mycloudreader.databinding.ActivityTransitionBinding;
import ivring.mycloudreader.utils.PerfectClickListener;

/**
 * 这是默认Activity启动页面
 */
public class TransitionActivity extends AppCompatActivity {
    private ActivityTransitionBinding mBinding;
    private boolean isIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transition);

        int i = new Random().nextInt(ConstantsImageUrl.TRANSITION_URLS.length);
        // 默认图显示在sytle中写的theme中，这里是显示网络图片上边有跳过按钮
        Glide.with(this)
                .load(ConstantsImageUrl.TRANSITION_URLS[i])
                .placeholder(R.drawable.img_transition_default)
                .error(R.drawable.img_transition_default)
                .into(mBinding.ivPic);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 3500);

//        跳过按钮
        mBinding.tvJump.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                toMainActivity();
            }
        });
    }


    /**
     * 用于跳转Activity，并加上了过度动画
     */
    private void toMainActivity() {
        if (isIn) {
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //第一个参数是当前Activity退出动画，第二个是新Activity的进入动画
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        finish();
        isIn = true;
    }
}
