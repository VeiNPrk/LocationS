package com.example.vnprk.locationsearch.View;

import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.support.v4.app.Fragment;

import android.database.ContentObserver;
//import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vnprk.locationsearch.App;
import com.example.vnprk.locationsearch.Controller.DataBase;
import com.example.vnprk.locationsearch.Loaders.DescribeLoader;
import com.example.vnprk.locationsearch.Controller.DescribeRecyclerAdapter;
import com.example.vnprk.locationsearch.Controller.DividerItemDecoration;
import com.example.vnprk.locationsearch.R;
import com.example.vnprk.locationsearch.Model.UserClass;
import com.example.vnprk.locationsearch.Model.UserClass_Table;
import com.example.vnprk.locationsearch.Loaders.UserLoader;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VNPrk on 27.10.2018.
 */

public class DescribeFragment extends Fragment implements ActionMode.Callback, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        DescribeDialogFragment.DescribeDialogListener {

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
    ProgressBar progressBar;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_describe, container, false);
        users = new ArrayList<UserClass>();
        elements = new ArrayList<String>();
        selectedPositions = new ArrayList<Integer>();
        initViews();
        setRecyclerView();
        initData();
        return view;

    }


    private void initViews(){
        dialog = new DescribeDialogFragment();
        dialog.setTargetFragment(this, 0);
        rvUsers = (RecyclerView)view.findViewById(R.id.rv_users);
        fabAddUser = (FloatingActionButton)view.findViewById(R.id.fab_add);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addElementView();
            }
        });
    }

    private void initData(){
        getLoaderManager().initLoader(MapActivity.LOADER_USERS, Bundle.EMPTY, this);
        //users = new Select().from(.class).queryList();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new DescribeRecyclerAdapter(view.getContext(), rvClickListener, users);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setHasFixedSize(true);
        rvUsers.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST);
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

    /*public void addFab(View view) {
        addElementView();
    }
*/
    public void updateData(){
        getLoaderManager().initLoader(MapActivity.LOADER_USERS, Bundle.EMPTY, this);
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
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
            refreshData();
    }*/

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
        public void onNoteClick(int position){
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
            actionMode = DescribeFragment.this.getActivity().startActionMode(DescribeFragment.this);
            myToggleSelection(position);
        }
    };

    @Override
    public void onYesClicked(DialogFragment dialog, String dataDescribe) {
        //Toast.makeText(view.getContext(), dataDescribe, Toast.LENGTH_LONG).show();
        int idDescribe = Integer.valueOf(dataDescribe);
        List<UserClass> userIsDone = new ArrayList<UserClass>();
        userIsDone = DataBase.getUser(idDescribe);
        if(userIsDone.size()>0)
        {
            Toast.makeText(getContext(), getContext().getString(R.string.describe_frg_dlg_user_done), Toast.LENGTH_LONG).show();
            return;
        }
        if(idDescribe==App.iam.getId())
        {
            Toast.makeText(getContext(), getContext().getString(R.string.describe_frg_dlg_user_equal), Toast.LENGTH_LONG).show();
            return;
        }
        if(idDescribe>0 ) {
            Bundle bundle = new Bundle();
            bundle.putInt(DescribeLoader.IDDEPEND_KEY, idDescribe);
            bundle.putInt(DescribeLoader.STATUS_KEY, 0);
            bundle.putInt(DescribeLoader.OPERATION_KEY, DescribeLoader.NEW_DESCRIBE);
            getLoaderManager().initLoader(DescribeLoader.NEW_DESCRIBE, bundle,  this);
        }
    }

    @Override
    public void onNoClicked(DialogFragment dialog) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case MapActivity.LOADER_USERS:
                return new UserLoader(view.getContext(), args);
            case DescribeLoader.NEW_DESCRIBE:
                return new DescribeLoader(view.getContext(), args);
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
                adapter.setData(users);
            }

        }
        if (id == DescribeLoader.NEW_DESCRIBE) {
            if (data != null) {
                users = DataBase.getAllUsers();
                adapter.setData(users);
            }

        }
        progressBar.setVisibility(View.GONE);
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
