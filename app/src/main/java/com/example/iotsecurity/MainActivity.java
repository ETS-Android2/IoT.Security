package com.example.iotsecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    static Handler handler = new Handler();

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        ProductFragment productFragment = new ProductFragment();
        adapter.addItem(productFragment);
        HomeFragment homeFragment = new HomeFragment();
        adapter.addItem(homeFragment);

        pager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        public MyPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
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