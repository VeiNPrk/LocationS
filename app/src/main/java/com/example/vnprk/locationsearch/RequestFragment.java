package com.example.vnprk.locationsearch;

import android.support.v4.app.LoaderManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by VNPrk on 27.10.2018.
 */

public class RequestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    RecyclerView rvRequest = null;
    View view=null;
    List<UserClass> requestUsers;
    DescribeRecyclerAdapter adapter;
    int nowPositionList = -1;
    int countChecked = 0;
    DescribeDialogFragment dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request, container, false);
        initViews();
        initData();
        return view;
    }

    private void initViews(){
        rvRequest = (RecyclerView)view.findViewById(R.id.rv_request);
    }

    private void initData(){
        Bundle bundle = new Bundle();
        bundle.putInt(UserLoader.OPERATION_KEY, UserLoader.REQUEST_USERS);
        getLoaderManager().initLoader(MapActivity.LOADER_USERS, bundle, this);
        //users = new Select().from(.class).queryList();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new DescribeRecyclerAdapter(view.getContext(), rvClickListener, requestUsers);
        rvRequest.setLayoutManager(layoutManager);
        rvRequest.setHasFixedSize(true);
        rvRequest.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvRequest.addItemDecoration(itemDecoration);
        rvRequest.setItemAnimator(new DefaultItemAnimator());
    }

    private void deleteElement(List<Integer> positions) {
        if(positions.size()>0)
        {
            for(int i:positions) {
                requestUsers.get(i).delete();
                adapter.notifyItemRemoved(i);
            }
            refreshData();
            //AppWidget.sendUpdateMessage(this);
        }
    }

    private void addElementView(){
        dialog.show(getFragmentManager(), "DescribeDialog");
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        /*Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra(NoteActivity.KEY_CODE, requestCodeAdd);
        startActivityForResult(intent, requestCodeAdd);*/
        // }
    }

    public void refreshData(){
        requestUsers.clear();
        requestUsers = new Select().from(UserClass.class).where(UserClass_Table.type.eq(2)).queryList();
        //setTittleList();
        adapter.setData(requestUsers);
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

        }

        @Override
        public void onNoteLongClick(int position){
        }
    };
/*
    @Override
    public void onYesClicked(DialogFragment dialog, String dataDescribe) {
        Toast.makeText(view.getContext(), dataDescribe, Toast.LENGTH_LONG).show();
        int idDescribe = Integer.valueOf(dataDescribe);
        if(idDescribe>0) {
            Bundle bundle = new Bundle();
            bundle.putInt(DescribeLoader.IDDEPEND_KEY, idDescribe);
            bundle.putInt(DescribeLoader.STATUS_KEY, 0);
            bundle.putInt(DescribeLoader.OPERATION_KEY, DescribeLoader.NEW_DESCRIBE);
            getLoaderManager().initLoader(DescribeLoader.NEW_DESCRIBE, bundle,  this);
        }
    }

    @Override
    public void onNoClicked(DialogFragment dialog) {

    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MapActivity.LOADER_USERS:
                return new UserLoader(view.getContext(), args);
            /*case DescribeLoader.NEW_DESCRIBE:
                return new DescribeLoader(view.getContext(), args);*/
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == MapActivity.LOADER_USERS) {
            if (data != null) {
                requestUsers = DataBase.getRequestUsers();
            }
            setRecyclerView();
        }

        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
