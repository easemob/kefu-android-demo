package com.easemob.helpdeskdemo.ui.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.domain.TicketEntity;
import com.easemob.helpdeskdemo.utils.ISO8601DateFormat;
import com.easemob.util.DateUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.text.ParseException;
import java.util.TimeZone;

public class TicketListHolder extends BaseViewHolder<TicketEntity> {
    private TextView tvName;
    private TextView tvTime;
    private TextView tvContent;

    private ISO8601DateFormat dateFormat = new ISO8601DateFormat();
    public TicketListHolder(ViewGroup parent) {
        super(parent, R.layout.em_row_ticket);
        tvName = $(R.id.tv_name);
        tvTime = $(R.id.tv_date);
        tvContent = $(R.id.tv_content);
        dateFormat.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public void setData(TicketEntity data) {
        super.setData(data);
        if (data != null) {
            tvName.setText("ID:" + data.getId());
            try {
                tvTime.setText(DateUtils.getTimestampString(dateFormat.parse(data.getUpdated_at())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvContent.setText(data.getContent());
        }


    }
}
