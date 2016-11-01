package com.hyphenate.helpdesk.easeui.provider;


import android.widget.BaseAdapter;

import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;

/**
 * 自定义chat row提供者
 */
public interface CustomChatRowProvider {
    /**
     * 获取多少种类型的自定义chatrow<br/>
     * 注意，每一种chatrow至少有两种type：发送type和接收type
     * @return
     */
    int getCustomChatRowTypeCount();

    /**
     * 获取chatrow type，必须大于0, 从1开始有序排列
     * @return
     */
    int getCustomChatRowType(Message message);

    /**
     * 根据给定message返回chat row
     * @return
     */
    ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter);
}