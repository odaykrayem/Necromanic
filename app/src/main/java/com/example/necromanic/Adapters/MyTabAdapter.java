package com.example.necromanic.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.necromanic.Fragments.FileViewerFragment;
import com.example.necromanic.Fragments.RecordFragment;


public class MyTabAdapter extends FragmentPagerAdapter {

    String [] titles = {"Record","Saved Recording"};
    public MyTabAdapter(FragmentManager fm){
        super(fm);


    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
              return new RecordFragment();
            case 1:
              return new FileViewerFragment()  ;

        }
            return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
