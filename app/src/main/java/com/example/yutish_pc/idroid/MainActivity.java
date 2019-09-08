package com.example.yutish_pc.idroid;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                                     //toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("iDroid");
        toolbar.setTitleTextColor(Color.WHITE);

        Drawable drawable = toolbar.getOverflowIcon();                                              //toolbar designing
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), getResources().getColor(R.color.white));
            toolbar.setOverflowIcon(drawable);
        }

        mAuth = FirebaseAuth.getInstance();                                                         // firebase instance

        //Tab for mainActivity
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        myTabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

    }

    @Override
    public void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            LogOutUser();
        }
    }

    private void LogOutUser() {                                                                     //for logging out user

        Intent StartPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        StartPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(StartPageIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                 //menu in the toolbar

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //action on item selected from the menu
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_butt) {
            mAuth.signOut();

            LogOutUser();
        }

        if (item.getItemId() == R.id.main_all_users_butt) {
            Intent allUsersActivity = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUsersActivity);                                                        // jumping to all users activity
        }
        return true;
    }

}













