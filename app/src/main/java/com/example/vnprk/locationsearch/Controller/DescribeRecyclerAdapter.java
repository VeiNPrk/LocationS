package com.example.vnprk.locationsearch.Controller;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vnprk.locationsearch.Model.UserClass;
import com.example.vnprk.locationsearch.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VNPrk on 20.10.2018.
 */

public class DescribeRecyclerAdapter extends RecyclerView.Adapter<DescribeRecyclerAdapter.DescribeViewHolder>{

    Context context;
    public static final String MSG_NO_DATA="Адрес неопознан";
    private SparseBooleanArray selectedItems;
    private DescribeClickListener describeClickListener;
    class DescribeViewHolder extends RecyclerView.ViewHolder {

        TextView tvDescribeName;
        TextView tvDescribeStatus;

        DescribeViewHolder(View itemView) {
            super(itemView);
            initViews(itemView);
        }

        void initViews(View itemView) {
            tvDescribeName = (TextView)itemView.findViewById(R.id.tv_describe_name);
            tvDescribeStatus = (TextView)itemView.findViewById(R.id.tv_describe_status);
        }
    }

    List<UserClass> data;

    public DescribeRecyclerAdapter(Context _context, DescribeClickListener _noteClickListener, List<UserClass> data) {
        this.data = data;
        context=_context;
        describeClickListener=_noteClickListener;
        selectedItems = new SparseBooleanArray();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void setData(List<UserClass> users){
        data=users;
        notifyDataSetChanged();
    }
/*
    public void addData(NoteClass newData, int position) {
        data.add(position, newData);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }*/

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    @Override
    public DescribeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item, parent, false);
        //final MyCustomView ivIconNote = (MyCustomView)view.findViewById(R.id.iv_icon_note);
        final DescribeViewHolder viewHolder = new DescribeViewHolder(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = viewHolder.getAdapterPosition();
                describeClickListener.onNoteClick(position);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = viewHolder.getAdapterPosition();
                describeClickListener.onNoteLongClick(position);
                return true;
            }
        });
        return viewHolder/*new NoteViewHolder(view)*/;
    }

    @Override
    public void onBindViewHolder(DescribeViewHolder holder, int i) {
        final UserClass selectedNote = data.get(i);
        holder.tvDescribeName.setText(data.get(i).getStrName());
        if(data.get(i).getStatus()==0) {
            holder.tvDescribeStatus.setText(context.getString(R.string.rv_item_status_wait));
            holder.tvDescribeStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        else {
            holder.tvDescribeStatus.setText(context.getString(R.string.rv_item_status_done));
            holder.tvDescribeStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        holder.itemView.setActivated(selectedItems.get(i, false));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static String getAddressByLoc(double latitude, double longitude, Context context) {

        final Geocoder geo = new Geocoder(context);
        List<Address> list = null;
        try {
            list = geo.getFromLocation(latitude, longitude, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

        if (list.isEmpty()) return MSG_NO_DATA;

        Address a = list.get(0);
        final int index = a.getMaxAddressLineIndex();
        String postal = null;
        if (index >= 0) {
            postal = a.getAddressLine(index);
        }

        StringBuilder builder = new StringBuilder();
        final String sep = ", ";
        builder.append(postal).append(sep)
                .append(a.getCountryName()).append(sep)
                .append(a.getThoroughfare()).append(sep)
                .append(a.getSubThoroughfare());

        return builder.toString();
    }

    public interface DescribeClickListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }
}