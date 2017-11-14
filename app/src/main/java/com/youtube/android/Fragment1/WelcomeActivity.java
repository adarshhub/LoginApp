package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener  {

    int layout[]={R.layout.firstslide,R.layout.secondslide,R.layout.thirdslide,R.layout.fourth_slide};
    ViewPager viewPager;
    Button bnskip,bnnext;
    TabLayout tabLayout;
    PagerAdapter adapter;
    int pos=0;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        preferences=new Preferences(this);
        if (preferences.checkpreferences())
            loadHome();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout= (TabLayout) findViewById(R.id.welcome_tablayout);
        tabLayout.setupWithViewPager(viewPager);
        bnskip=(Button)findViewById(R.id.skip);
        bnnext= (Button) findViewById(R.id.next);
        bnskip.setOnClickListener(this);
        bnnext.setOnClickListener(this);
        adapter =new PageAdapter(layout,this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                pos=position;

                if (position == layout.length-1)
                {
                    preferences.writepreferences();
                    loadHome();
                }

                if (position>= layout.length-2)
                {
                    bnnext.setText(getResources().getText(R.string.home));
                    bnskip.setVisibility(View.INVISIBLE);
                }
                else
                {
                    bnnext.setText(getResources().getText(R.string.nxt_btn));
                    bnskip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.skip:
                preferences.writepreferences();
                loadHome();
                break;
            case R.id.next:
                nextslide();
                break;
        }
    }

    public void loadHome()
    {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    public void nextslide()
    {
        int nxt_slide = viewPager.getCurrentItem()+1;
        if (nxt_slide<layout.length)
        {
            viewPager.setCurrentItem(nxt_slide);
        }
        else {
            preferences.writepreferences();
            loadHome();
        }
    }
}
