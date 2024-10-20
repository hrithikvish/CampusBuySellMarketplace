package com.hrithikvish.cbsm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hrithikvish.cbsm.R;
import com.hrithikvish.cbsm.model.ParentItemModelClassForRV;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

import java.util.ArrayList;

public class ExploreParentRVAdapter extends RecyclerView.Adapter<ExploreParentRVAdapter.ParentViewHolder> {

    Context context;
    ArrayList<ParentItemModelClassForRV> collegesList;

    public ExploreParentRVAdapter(Context context, ArrayList<ParentItemModelClassForRV> collegesList) {
        this.context = context;
        this.collegesList = collegesList;
    }

    public void filterList(ArrayList<ParentItemModelClassForRV> filteredList) {
        collegesList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExploreParentRVAdapter.ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_page_parent_rv_item, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreParentRVAdapter.ParentViewHolder holder, int position) {
        ParentItemModelClassForRV parentModelClass = collegesList.get(position);

        holder.collegeName.setText(parentModelClass.getCollegeName());

        ExploreChildRVAdapter childRVAdapter = new ExploreChildRVAdapter(context, parentModelClass.getPostList());
        holder.childRV.setHasFixedSize(true);
        holder.childRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.childRV.setAdapter(childRVAdapter);
        childRVAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return collegesList.size();
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView collegeName;
        RecyclerView childRV;
        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);

            collegeName = itemView.findViewById(R.id.collegeName);
            childRV = itemView.findViewById(R.id.childRV);

        }
    }
}
