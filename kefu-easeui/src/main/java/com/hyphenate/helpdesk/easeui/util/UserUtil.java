package com.hyphenate.helpdesk.easeui.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;

/**
 */
public class UserUtil {

    public static void setAgentNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView){
        AgentInfo agentInfo = MessageHelper.getAgentInfo(message);
        if (agentInfo != null) {
            if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                usernickView.setText(agentInfo.getNickname());
            }
            if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                String strUrl = agentInfo.getAvatar();
                // 设置客服头像
                if (!TextUtils.isEmpty(strUrl)) {
                    if (!strUrl.startsWith("http")) {
                        strUrl = "http:" + strUrl;
                    }
                    //正常的string路径
                    Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(userAvatarView);
                } else {
                    Glide.with(context).load(R.drawable.ease_default_avatar).into(userAvatarView);
                }

            }
        } else {
            userAvatarView.setImageResource(R.drawable.ease_default_avatar);
            usernickView.setText(message.getFrom());
        }
    }

    public static void setCurrentUserNickAndAvatar(Context context,ImageView userAvatarView, TextView userNickView){

    }

}
