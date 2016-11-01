package com.hyphenate.helpdesk.easeui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.helpdesk.easeui.ui.BaseChatActivity;
import com.hyphenate.helpdesk.model.AgentIdentityInfo;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;

public class IntentBuilder {

    private Context mContext;
    private Class<? extends Activity> mActivityClass;
    private String toChatUsername;
    private AgentIdentityInfo agentIdentityInfo;
    private QueueIdentityInfo queueIdentityInfo;
    private boolean showUserNick;
    private VisitorInfo visitorInfo;
    private Bundle bundle;

    public IntentBuilder(Context context) {
        this.mContext = context;
    }

    public IntentBuilder setTargetClass(Class<? extends Activity> targetClass) {
        this.mActivityClass = targetClass;
        return this;
    }

    public IntentBuilder setServiceIMNumber(String toChatUsername) {
        this.toChatUsername = toChatUsername;
        return this;
    }

    public IntentBuilder setScheduleAgent(AgentIdentityInfo info) {
        agentIdentityInfo = info;
        return this;
    }

    public IntentBuilder setScheduleQueue(QueueIdentityInfo info) {
        this.queueIdentityInfo = info;
        return this;
    }

    public IntentBuilder setVisitorInfo(VisitorInfo visitorInfo) {
        this.visitorInfo = visitorInfo;
        return this;
    }

    public IntentBuilder setShowUserNick(boolean showNick) {
        this.showUserNick = showNick;
        return this;
    }

    public IntentBuilder setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public Intent build() {
        if (mActivityClass == null) {
            mActivityClass = BaseChatActivity.class;
        }
        Intent intent = new Intent(mContext, mActivityClass);
        if (!TextUtils.isEmpty(toChatUsername)) {
            intent.putExtra(Config.EXTRA_SERVICE_IM_NUMBER, toChatUsername);
        }
        if (visitorInfo != null) {
            intent.putExtra(Config.EXTRA_VISITOR_INFO, visitorInfo);
        }

        if (agentIdentityInfo != null) {
            intent.putExtra(Config.EXTRA_AGENT_INFO, agentIdentityInfo);
        }

        if (queueIdentityInfo != null) {
            intent.putExtra(Config.EXTRA_QUEUE_INFO, queueIdentityInfo);
        }
        intent.putExtra(Config.EXTRA_SHOW_NICK, showUserNick);
        if (bundle != null) {
            intent.putExtra(Config.EXTRA_BUNDLE, bundle);
        }
        return intent;
    }


}
