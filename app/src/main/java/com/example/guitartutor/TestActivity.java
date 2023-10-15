package com.example.guitartutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.stetho.Stetho;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    // SQLite使用的變數
    private final String DB_NAME = "record.db";
    private String TABLE_NAME = "record";
    private final int DB_VERSION = 1;
    SQLiteDataBaseHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 隱藏ActionBar
        getSupportActionBar().hide();

        // Facebook工具:Stetho
        // 用來查看SQLite的資料
        Stetho.initializeWithDefaults(this);

        // 初始化資料庫
        mDBHelper = new SQLiteDataBaseHelper(this, DB_NAME,
                null, DB_VERSION, TABLE_NAME);

        // 測試新增資料
        // mDBHelper.addData("2022-05-17", "30");

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // 設定ViewPager 和 Pager Adapter
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            // 根據目前tab標籤頁的位置，回傳對應的fragment物件
            switch (position) {
                case 0:
                    fragment = new GameFragment();
                    break;
                case 1:
                    fragment = new HistoricalRecFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "測驗模式";
                case 1:
                    return "歷史紀錄";
                default:
                    return null;
            }
        }
    }

    public SQLiteDataBaseHelper getDataBaseHelper() {
        return mDBHelper;
    }
}