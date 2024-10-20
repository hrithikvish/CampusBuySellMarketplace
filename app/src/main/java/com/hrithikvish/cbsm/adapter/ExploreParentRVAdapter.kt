package com.hrithikvish.cbsm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hrithikvish.cbsm.R
import com.hrithikvish.cbsm.adapter.ExploreParentRVAdapter.ParentViewHolder
import com.hrithikvish.cbsm.model.ParentItemModelClassForRV

class ExploreParentRVAdapter(
    var context: Context?,
    var collegesList: ArrayList<ParentItemModelClassForRV>?
) :
    RecyclerView.Adapter<ParentViewHolder>() {
    fun filterList(filteredList: ArrayList<ParentItemModelClassForRV>) {
        collegesList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.explore_page_parent_rv_item, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentModelClass = collegesList!![position]

        holder.collegeName.text = parentModelClass.collegeName

        val childRVAdapter = ExploreChildRVAdapter(context!!, parentModelClass.postList)
        holder.childRV.setHasFixedSize(true)
        holder.childRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.childRV.adapter = childRVAdapter
        childRVAdapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return collegesList!!.size
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var collegeName: TextView = itemView.findViewById(R.id.collegeName)
        var childRV: RecyclerView = itemView.findViewById(R.id.childRV)
    }
}
