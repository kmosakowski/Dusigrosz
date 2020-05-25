package com.example.dusigrosz.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dusigrosz.R
import com.example.dusigrosz.entity.Borrower

class BorrowerListAdapter(private var activity: Activity, private var borrowers: ArrayList<Borrower>): BaseAdapter(){

    private class ViewHolder(row: View?) {
        var name: TextView? = null
        var surname: TextView? = null
        var amount: TextView? = null

        init {
            this.name = row?.findViewById(R.id.name)
            this.surname = row?.findViewById(R.id.surname)
            this.amount = row?.findViewById(R.id.amount)
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.borrowerlist_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val borrower = borrowers[position]
        viewHolder.name?.text = borrower.name
        viewHolder.surname?.text = borrower.surname
        viewHolder.amount?.text = borrower.amount.toString()

        return view as View
    }

    override fun getItem(position: Int): Borrower {
        return borrowers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return borrowers.size
    }

}