package com.example.vnprk.locationsearch.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.MenuItem;

import com.example.vnprk.locationsearch.Controller.DescribeRecyclerAdapter;
import com.example.vnprk.locationsearch.Model.MessageEvent;
import com.example.vnprk.locationsearch.R;
import com.example.vnprk.locationsearch.Model.UserClass;
import com.example.vnprk.locationsearch.Controller.ViewPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    DescribeFragment describeFragment;
    RequestFragment requestFragment;
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
        describeFragment = new DescribeFragment();
        requestFragment = new RequestFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(describeFragment, getString(R.string.tab_layout_describe));
        adapter.addFragment(requestFragment, getString(R.string.tab_layout_request));
        viewPager.setAdapter(adapter);

    }

    private void updateDescribe(){
        describeFragment.updateData();
    }

    private void updateRequest(){
        requestFragment.updateData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event!=null){
            switch (event.typeDescribe)
            {
                case 0:
                    //if(tabLayout.getSelectedTabPosition()==0) {
                        updateDescribe();
                        //Toast.makeText(this, event.message+"0", Toast.LENGTH_SHORT).show();
                    //}
                    break;
                case 1:
                    //if(tabLayout.getSelectedTabPosition()==1) {
                        updateRequest();
                        //Toast.makeText(this, event.message+"1", Toast.LENGTH_SHORT).show();
                   //}
                    break;
                case RequestFragment.REQUEST_LOAD_COMPL_CODE:
                    if(event.count>0)
                        tabLayout.getTabAt(1).setText(getString(R.string.tab_layout_request)+"("+event.count+")");
                    break;
            }
        }
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
