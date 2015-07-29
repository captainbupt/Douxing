package com.badou.mworking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badou.mworking.adapter.ChatterListAdapter;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.LevelTextView;
import com.badou.mworking.widget.NoScrollListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能描述: 我的圈页面
 * 逻辑过于简单，不必要MVP
 */
public class ChatterUserActivity extends BaseNoTitleActivity {

    public static final String KEY_USER_CHATTER = "user";
    @Bind(R.id.back_image_view)
    ImageView mBackImageView;
    @Bind(R.id.about_image_view)
    ImageView mAboutImageView;
    @Bind(R.id.title_text_view)
    TextView mTitleTextView;
    @Bind(R.id.head_image_view)
    ImageView mHeadImageView;
    @Bind(R.id.level_text_view)
    LevelTextView mLevelTextView;
    @Bind(R.id.name_text_view)
    TextView mNameTextView;

    public static Intent getIntent(Context context, UserChatterInfo userChatterInfo) {
        Intent intent = new Intent(context, ChatterUserActivity.class);
        intent.putExtra(KEY_USER_CHATTER, userChatterInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_user);
        final UserChatterInfo userInfo = (UserChatterInfo) mReceivedIntent.getSerializableExtra(KEY_USER_CHATTER);
        final ChatterListFragment chatterListFragment = ChatterListFragment.getFragment(null, userInfo.getUid());
        getSupportFragmentManager().beginTransaction().replace(R.id.base_container, chatterListFragment).commit();
        new Handler().postDelayed(new Runnable() {  // 等待fragment渲染完成
            @Override
            public void run() {
                chatterListFragment.setHeaderView(getHeaderView(userInfo));
            }
        },200);
    }

    private View getHeaderView(UserChatterInfo userInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_center_header, null);
        ButterKnife.bind(this, view);
        mAboutImageView.setVisibility(View.GONE);
        String selfUid = UserInfo.getUserInfo().getUid();
        if (userInfo.getUid().equals(selfUid)) {
            mTitleTextView.setText(R.string.chatter_user_title_myself);
        } else {
            mTitleTextView.setText(R.string.chatter_user_title_other);
        }
        ImageViewLoader.setCircleImageViewResource(mHeadImageView, userInfo.getHeadUrl(), getResources().getDimensionPixelSize(R.dimen.user_center_image_head_size));
        mNameTextView.setText(userInfo.getName() + "\n" + userInfo.getDepartment());
        mLevelTextView.setLevel(userInfo.getLevel());
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        return view;
    }
}
