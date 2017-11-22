package com.easemob.helpdeskdemo.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.helpdeskdemo.ui.BaiduMapActivity;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.util.LatLng;

public class ChatRowLocation extends ChatRow {

    private TextView locationView;
    private EMLocationMessageBody locBody;

    public ChatRowLocation(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                com.hyphenate.helpdesk.R.layout.hd_row_received_location : com.hyphenate.helpdesk.R.layout.hd_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
        locationView = (TextView) findViewById(com.hyphenate.helpdesk.R.id.tv_location);
    }


    @Override
    protected void onSetUpView() {
        locBody = (EMLocationMessageBody) message.body();
        locationView.setText(locBody.getAddress());

        // deal with send message
        if (message.direct() == Message.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
//                sendMsgInBackground(message);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("latitude", locBody.getLatitude());
        intent.putExtra("longitude", locBody.getLongitude());
        intent.putExtra("address", locBody.getAddress());
        activity.startActivity(intent);
    }

    /*
	 * 点击地图消息listener
	 */
    protected class MapClickListener implements OnClickListener {

        LatLng location;
        String address;

        public MapClickListener(LatLng loc, String address) {
            location = loc;
            this.address = address;

        }

        @Override
        public void onClick(View v) {

        }

    }

}
