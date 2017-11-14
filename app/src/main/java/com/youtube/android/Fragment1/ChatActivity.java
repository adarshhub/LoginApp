package com.youtube.android.Fragment1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatActivity extends AppCompatActivity {


    private DatabaseReference mRef= null;
    TabLayout mtabLayout;
    FragmentAdapter mfragmentAdapter;
    private FirebaseAuth mAuth;
    private ViewPager mviewPager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_bar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mviewPager = (ViewPager) findViewById(R.id.chatview);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavview);
        mtabLayout = (TabLayout) findViewById(R.id.tabbar);
        FirebaseUser user = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("online");
        mfragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mfragmentAdapter);
        mtabLayout.setupWithViewPager(mviewPager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.bottom_setting:
                        startActivity(new Intent(getBaseContext(),SettingsActivity.class));
                        break;
                    case  R.id.bottom_camera:
                        Intent imageintent;
                        imageintent = new Intent(getBaseContext(),SettingsActivity.class);
                        imageintent.putExtra("Camera",123);
                        startActivity(imageintent);
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.logout :
                if (mRef != null)
                    mRef.setValue(0);
                mAuth.signOut();
                signedout();
                break;
            case R.id.settings :
                Intent intent = new Intent(getBaseContext(),SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.users :
                Intent userintent = new Intent(getBaseContext(),UsersActivity.class);
                startActivity(userintent);
                break;
            case R.id.request:
                Intent requestIntent = new Intent(getBaseContext(),RequestActivity.class);
                startActivity(requestIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void signedout()
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("binary",0);
        startActivity(intent);
        finish();
    }
}
