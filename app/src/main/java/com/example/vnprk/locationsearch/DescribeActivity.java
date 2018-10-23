package com.example.vnprk.locationsearch;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VNPrk on 20.10.2018.
 */

public class DescribeActivity extends AppCompatActivity implements ActionMode.Callback, LoaderManager.LoaderCallbacks<Cursor>,
        DescribeDialogFragment.DescribeDialogListener{
    List<String> elements;
    List<Integer> selectedPositions;
    List<UserClass> users;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    DescribeRecyclerAdapter adapter;
    MenuItem deleteItem;
    RecyclerView rvUsers;
    ActionMode actionMode;
    FloatingActionButton fabAddUser;
    DescribeDialogFragment dialog;
    int nowPositionList = -1;
    int countChecked = 0;
    //int idDescribe = 0;

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
        setContentView(R.layout.activity_describe);

        //Создаем массив элементов для списка
        users = new ArrayList<UserClass>();
        elements = new ArrayList<String>();
        selectedPositions = new ArrayList<Integer>();
        initViews();
        initData();


        //getContentResolver().registerContentObserver(MyNoteProvider.NOTE_CONTENT_URI, true, observer);
        //runTimePermissions();
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

    private void initViews(){
        dialog = new DescribeDialogFragment();
        rvUsers = (RecyclerView)findViewById(R.id.rv_users);
        fabAddUser = (FloatingActionButton)findViewById(R.id.fab_add);
    }

    private void initData(){
        getSupportLoaderManager().initLoader(MapActivity.LOADER_USERS, Bundle.EMPTY, this);
        //users = new Select().from(.class).queryList();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new DescribeRecyclerAdapter(this, rvClickListener, users);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setHasFixedSize(true);
        rvUsers.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvUsers.addItemDecoration(itemDecoration);
        rvUsers.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        //editItem = menu.findItem(R.id.menu_edit);
        deleteItem = menu.findItem(R.id.menu_delete);
        fabAddUser.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete:
                deleteElement(selectedPositions);
                actionMode.finish();
                return true;
            default:
                actionMode.finish();
                return true;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        adapter.clearSelections();
        fabAddUser.setVisibility(View.VISIBLE);
    }

    public void addFab(View view) {
        addElementView();
    }

    private void myToggleSelection(int idx) {
        adapter.toggleSelection(idx);
        selectedPositions=adapter.getSelectedItems();
        if(selectedPositions.size()==0)
            actionMode.finish();
    }

    private void clearList() {
        users.clear();
        elements.clear();
        adapter.notifyDataSetChanged();
    }

    private void deleteElement(List<Integer> positions) {
        if(positions.size()>0)
        {
            for(int i:positions) {
                users.get(i).delete();
                adapter.notifyItemRemoved(i);
            }
            refreshData();
            //AppWidget.sendUpdateMessage(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
            refreshData();
    }

    private void addElementView(){
        dialog.show(getFragmentManager(), "DescribeDialog");
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        /*Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra(NoteActivity.KEY_CODE, requestCodeAdd);
        startActivityForResult(intent, requestCodeAdd);*/
        // }
    }
/*
    private void elementView(NoteClass _note, View noteImage){
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra(NoteActivity.KEY_NOTE, _note);
        intent.putExtra(NoteActivity.KEY_CODE, requestCodeView);
        //startActivityTransition(intent, requestCodeView, noteImage);
    }

  /*  private void startActivityTransition(Intent intent, int requestCode, View noteImage){
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, noteImage, getString(R.string.activity_image_trans));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, requestCodeView, options.toBundle());
        }
        else
            startActivityForResult(intent, requestCode);
    }*/

    public void refreshData(){
        users.clear();
        users = new Select().from(UserClass.class).where(UserClass_Table.type.eq(0)).queryList();
        //setTittleList();
        adapter.setData(users);
        nowPositionList=-1;
        countChecked=0;
    }

    private ContentObserver observer = new ContentObserver(new Handler()){
        @Override
        public void onChange(boolean selfChange){
            super.onChange(selfChange);
            refreshData();
        }
    };

    private DescribeRecyclerAdapter.DescribeClickListener rvClickListener = new DescribeRecyclerAdapter.DescribeClickListener() {
        @Override
        public void onNoteClick(View noteImage, int position){
            if (actionMode != null) {
                myToggleSelection(position);
                return;
            }
            //elementView(users.get(position),noteImage);
        }

        @Override
        public void onNoteLongClick(int position){
            if (actionMode != null) {
                return;
            }
            actionMode = startActionMode(DescribeActivity.this);
            myToggleSelection(position);
        }
    };


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MapActivity.LOADER_USERS:
                return new UserLoader(this);
            case DescribeLoader.NEW_DESCRIBE:
                return new DescribeLoader(this, args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == MapActivity.LOADER_USERS) {
            if (data != null) {
                users = DataBase.getAllUsers();
            }
            setRecyclerView();
        }
        if (id == DescribeLoader.NEW_DESCRIBE) {
            if (data != null) {
                users = DataBase.getAllUsers();
                adapter.setData(users);
            }

        }
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onYesClicked(DialogFragment dialog, String dataDescribe) {
        Toast.makeText(this, dataDescribe, Toast.LENGTH_LONG).show();
        int idDescribe = Integer.valueOf(dataDescribe);
        if(idDescribe>0) {
            Bundle bundle = new Bundle();
            bundle.putInt(DescribeLoader.IDDEPEND_KEY, idDescribe);
            bundle.putInt(DescribeLoader.STATUS_KEY, 0);
            bundle.putInt(DescribeLoader.OPERATION_KEY, DescribeLoader.NEW_DESCRIBE);
            getSupportLoaderManager().initLoader(DescribeLoader.NEW_DESCRIBE, bundle, this);
        }
    }

    @Override
    public void onNoClicked(DialogFragment dialog) {

    }
}
