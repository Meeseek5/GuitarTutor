package com.example.guitartutor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


public class FingerPlacementActivity extends AppCompatActivity {

    private FingerPlacementFirstFragment mFragment1;
    private FingerPlacementSecondFragment mFragment2;

    private FragmentManager mFragmentMgr;

    //private boolean fragmentIsClose = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_placement);

        // 保持縱向
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mFragment1 = new FingerPlacementFirstFragment();
        mFragment2 = new FingerPlacementSecondFragment();

        mFragmentMgr = getSupportFragmentManager();
        mFragmentMgr.beginTransaction()
                .replace(R.id.frame_layout_container, mFragment1, "TAG-mFragment1")
                .commit();

        //.replace(R.id.fragment_container_view, mFragment, "TAG-mFragment")
        //.add(R.id.frame_layout_container, mFragment)



        //留在activity
        //隱藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    //關掉手指對應顏色的fragment
    public void replaceFragment() {
        mFragmentMgr.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.frame_layout_container, mFragment2, "TAG-mFragment2")
                .commit();

    }

}