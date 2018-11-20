package com.example.vnprk.locationsearch.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.vnprk.locationsearch.Controller.DataBase;
import com.example.vnprk.locationsearch.Controller.DescribeRecyclerAdapter;
import com.example.vnprk.locationsearch.Controller.DividerItemDecoration;
import com.example.vnprk.locationsearch.Loaders.DescribeLoader;
import com.example.vnprk.locationsearch.Loaders.UserLoader;
import com.example.vnprk.locationsearch.Model.MessageEvent;
import com.example.vnprk.locationsearch.Model.UserClass;
import com.example.vnprk.locationsearch.R;
import com.example.vnprk.locationsearch.Model.UserClass_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.greenrobot.eventbus.EventBus;

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
    public static final String REQUEST_LOAD_COMPLETE = "request_load_complete";
    public static final int REQUEST_LOAD_COMPL_CODE = 3;
    DescribeDialogFragment dialog;
    ProgressBar progressBar;
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
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar2);
    }

    private void initData(){
        updateData();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new DescribeRecyclerAdapter(view.getContext(), /*rvClickListener*/rvClickListener, requestUsers);
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

    public void updateData(){
        Bundle bundle = new Bundle();
        bundle.putInt(UserLoader.OPERATION_KEY, UserLoader.REQUEST_USERS);
        getLoaderManager().initLoader(MapActivity.LOADER_USERS, bundle, this);
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

    private void deleteRequest(int idReq) {
        Bundle bundle = new Bundle();
        bundle.putInt(DescribeLoader.OPERATION_KEY, DescribeLoader.DELETE_REQUEST);
        bundle.putInt(DescribeLoader.IDDEPEND_KEY, idReq);
        getLoaderManager().initLoader(DescribeLoader.DESCRIBE_LOADER, bundle, this);
    }

    private void succesRequest(int idReq) {
        Bundle bundle = new Bundle();
        bundle.putInt(DescribeLoader.OPERATION_KEY, DescribeLoader.UPDATE_REQUEST);
        bundle.putInt(DescribeLoader.IDDEPEND_KEY, idReq);
        bundle.putInt(DescribeLoader.STATUS_KEY, 1);
        getLoaderManager().initLoader(DescribeLoader.DESCRIBE_LOADER, bundle, this);
    }

    private DescribeRecyclerAdapter.DescribeClickListener rvClickListener = new DescribeRecyclerAdapter.DescribeClickListener() {
        @Override
        public void onNoteClick(final int position){
            //Log.d("rvRequest", "YES");
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setTitle(getString(R.string.request_frg_dlg_tittle))
                    .setPositiveButton(R.string.request_frg_dlg_done,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            succesRequest(requestUsers.get(position).getId());
                            //Toast.makeText(getContext(),"id="+id+" position="+position, Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.request_frg_dlg_del), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteRequest(requestUsers.get(position).getId());
                            //deleteRequest(id);
                        }
                    })
                    .setNeutralButton(getString(R.string.request_frg_dlg_wait), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setMessage(getString(R.string.request_frg_dlg_message));
            adb.create().show();
        }

        @Override
        public void onNoteLongClick(int position){
            Log.d("rvRequest", "YES2");
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
        progressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case MapActivity.LOADER_USERS:
                return new UserLoader(view.getContext(), args);
            case DescribeLoader.DESCRIBE_LOADER:
                return new DescribeLoader(view.getContext(), args);
            /*case DescribeLoader.NEW_DESCRIBE:
                return new DescribeLoader(view.getContext(), args);*/
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == MapActivity.LOADER_USERS || id == DescribeLoader.DESCRIBE_LOADER) {
            if (data != null) {
                requestUsers = DataBase.getRequestUsers();
            }
            setRecyclerView();
        }

        progressBar.setVisibility(View.GONE);
        getLoaderManager().destroyLoader(id);
        EventBus.getDefault().post(new MessageEvent(REQUEST_LOAD_COMPLETE, REQUEST_LOAD_COMPL_CODE, requestUsers.size()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
