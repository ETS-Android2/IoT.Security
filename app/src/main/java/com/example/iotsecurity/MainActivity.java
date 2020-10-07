package com.example.iotsecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);

        // fragment 전용 adapter
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        // 제품 리스트 fragment
        ProductFragment productFragment = new ProductFragment();
        adapter.addItem(productFragment);   // adapter에 추가

        // home fragment
        HomeFragment homeFragment = new HomeFragment();
        adapter.addItem(homeFragment);

        // adapter 설정
        pager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));     // tab 선택 여부를 확인해 주는 listener
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));       // tab 선택에 따라 pager에 해당하는 page 정보를 tab에 넘겨주는 listener

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addItem(Fragment item) {
            fragments.add(item);
        }
    }
}