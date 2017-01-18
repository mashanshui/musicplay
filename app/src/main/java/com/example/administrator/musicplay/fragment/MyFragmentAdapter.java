package com.example.administrator.musicplay.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragList;

    String[] titleList={"本地","收藏","下载"};
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragList) {
        super(fm);
        this.fragList=fragList;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return titleList[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }
}
