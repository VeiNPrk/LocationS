package com.example.vnprk.locationsearch;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VNPrk on 20.10.2018.
 */

public class DescribeActivity extends AppCompatActivity {
    List<String> elements;
    List<Integer> selectedPositions;
    List<UserClass> users;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    DescribeRecyclerAdapter adapter;
    public static final String FRAGMENT_DATA = "fragment_data";
    MenuItem deleteItem;
    RecyclerView rvUsers;
    ActionMode actionMode;
    FloatingActionButton fabAddUser;
    DescribeDialogFragment dialog;
    int nowPositionTab = 0;
    int countChecked = 0;
    //int idDescribe = 0;

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static void openActivity(Context context){
        Intent intent = new Intent(context, DescribeActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*if (!(context instanceof Activity))
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);*/
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe_request);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            nowPositionTab = extras.getInt(FRAGMENT_DATA, 0);
        }
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        //Создаем массив элементов для списка
        tabLayout.setScrollPosition(nowPositionTab,0f,true);
        viewPager.setCurrentItem(nowPositionTab);


        //getContentResolver().registerContentObserver(MyNoteProvider.NOTE_CONTENT_URI, true, observer);
        //runTimePermissions();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DescribeFragment(), "Подписки");
        adapter.addFragment(new RequestFragment(), "Запросы");
        viewPager.setAdapter(adapter);

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                clearList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}
