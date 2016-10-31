package com.easemob.helpdeskdemo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;
import com.hyphenate.helpdesk.domain.TicketEntity;
import com.hyphenate.helpdesk.util.ISO8601DateFormat;
import com.hyphenate.util.DateUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.ParseException;
import java.util.TimeZone;

/**
 * Created by liyuzhao on 16/9/8.
 */
public class TicketListAdapter extends RecyclerArrayAdapter<TicketEntity> {
    public TicketListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new TicketListHolder(parent);
    }


    class TicketListHolder extends BaseViewHolder<TicketEntity>{

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


}
