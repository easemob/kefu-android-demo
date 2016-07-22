package com.easemob.helpdeskdemo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.easemob.helpdeskdemo.domain.TicketEntity;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class TicketListAdapter extends RecyclerArrayAdapter<TicketEntity> {

    public TicketListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new TicketListHolder(parent);
    }
}
