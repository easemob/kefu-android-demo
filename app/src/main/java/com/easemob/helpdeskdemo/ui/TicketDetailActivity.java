package com.easemob.helpdeskdemo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.interfaces.IListener;
import com.easemob.helpdeskdemo.utils.CommonUtils;
import com.easemob.helpdeskdemo.utils.ListenerManager;
import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.CommentEntity;
import com.hyphenate.helpdesk.domain.CommentListResponse;
import com.hyphenate.helpdesk.domain.TicketEntity;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.util.ISO8601DateFormat;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.PathUtil;
import com.wefika.flowlayout.FlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 留言详情界面,用于展示留言的具体内容和评论内容
 */
public class TicketDetailActivity extends BaseActivity implements IListener {

    private static final String TAG = TicketDetailActivity.class.getSimpleName();

    private static final int REQUEST_CODE_NEW_COMMENT = 0x01;
    /**
     * 返回按钮
     */
    private RelativeLayout btnBack;
    /**
     * 这里放在了listview的headview中,展示留言详情信息
     */
    private TextView tvContent;
    /**
     * 从留言列表中选中的留言Item
     */
    private TicketEntity ticketEntity;

    /**
     * 此根据留言ID(ticketId)获取的此条留言的评论列表
     */
    private List<CommentEntity> mCommentDatas = Collections.synchronizedList(new ArrayList<CommentEntity>());
    /**
     * demo用最基础的listview展示评论内容,APP可以用更好的其他控件
     */
    private ListView mListView;

    /**
     * 自定义留言列表适配器
     */
    private CommentAdapter adapter;
    /**
     * 留言详情放到了listview的headview中
     */
    private View mHeaderView;


    /**
     * loading等待dialog
     */
    private ProgressDialog pd;

    /**
     * 回复按钮
     */
    private Button buttonReply;

    private ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    /**
     * 初始化View
     */
    private void initView() {
        mListView = $(R.id.listView);
        btnBack = $(R.id.rl_back);
        buttonReply = $(R.id.button_reply);
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.em_ticket_detail_header, null);
        tvContent = (TextView) mHeaderView.findViewById(R.id.ticketContent);
    }

    /**
     * 设置监听
     */
    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentEntity commentEntity = (CommentEntity) parent.getItemAtPosition(position);
//
            }
        });
        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id", ticketEntity.getId());
                intent.setClass(TicketDetailActivity.this, NewCommentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_COMMENT);
            }
        });
    }

    /**
     * 加载并填充数据
     */
    private void loadData() {
        ticketEntity = getIntent().getParcelableExtra("ticket");
        if (ticketEntity != null) {
            TicketEntity.CreatorBean creatorBean = ticketEntity.getCreator();
            if (creatorBean != null && !TextUtils.isEmpty(creatorBean.getName()) && !creatorBean.getName().equals("null")) {
                ((TextView)mHeaderView.findViewById(R.id.tv_ticket_name)).setText(creatorBean.getName());
            }
            if (creatorBean != null) {
                if (!TextUtils.isEmpty(creatorBean.getPhone())) {
                    ((TextView)mHeaderView.findViewById(R.id.tv_ticket_phone)).setText(creatorBean.getPhone());

                }
                if (!TextUtils.isEmpty(creatorBean.getEmail())) {
                    ((TextView)mHeaderView.findViewById(R.id.tv_ticket_email)).setText(creatorBean.getEmail());
                }
            }
            ((TextView)mHeaderView.findViewById(R.id.tv_ticket_theme)).setText(ticketEntity.getSubject());
            ((TextView)mHeaderView.findViewById(R.id.tv_ticket_detail)).setText(ticketEntity.getContent());
            try {
                ((TextView)mHeaderView.findViewById(R.id.tv_ticket_date))
                        .setText(DateUtils.getTimestampString(dateFormat.parse(ticketEntity.getUpdated_at())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            loadAllComments(ticketEntity.getId());
        } else {
            tvContent.setText("");
        }
        mListView.addHeaderView(mHeaderView);
        mListView.setAdapter(adapter = new CommentAdapter(mCommentDatas));

    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_fragment_ticket_detail);

        initView();
        initListener();
        loadData();
        ListenerManager.getInstance().registerListener(this);
    }


    /**
     * 加载留言的所有评论
     *
     * @param ticketId 留言的ID
     */
    private void loadAllComments(String ticketId) {
        if (!ChatClient.getInstance().isLoggedInBefore()) {
            ToastHelper.show(this, R.string.login_user_noti);
            return;
        }
        String target = Preferences.getInstance().getCustomerAccount();
        String tenantId = Preferences.getInstance().getTenantId();
        String projectId = Preferences.getInstance().getProjectId();

        ChatClient.getInstance().leaveMsgManager().getLeaveMsgComments(projectId, ticketId, target, new ValueCallBack<String>(){

            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        CommentListResponse commentListResponse = gson.fromJson(value, CommentListResponse.class);
                        if (commentListResponse != null && commentListResponse.getSize() != 0) {
                            mCommentDatas.clear();
                            mCommentDatas.addAll(commentListResponse.getEntities());
                            Collections.sort(mCommentDatas, new Comparator<CommentEntity>() {
                                @Override
                                public int compare(CommentEntity lhs, CommentEntity rhs) {
                                    return lhs.getCreated_at().compareTo(rhs.getCreated_at());
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }

            @Override
            public void onError(int error, final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "errorMsg:" + errorMsg);
                        ToastHelper.show(getBaseContext(), R.string.comment_load_fail);
                    }
                });
            }
        }, 0, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_NEW_COMMENT){
                // 添加了新的评论,刷新列表
                loadAllComments(ticketEntity.getId());
            }
        }

    }

    public void closeDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().unRegisterListener(this);
        closeDialog();
    }


    @Override
    public void notifyEvent(String eventStr, Object eventObj) {
        if (!TextUtils.isEmpty(eventStr) && eventStr.equals("CommentCreatedEvent")) {
            // 评论发生改变
            if (eventObj != null && eventObj instanceof JSONObject) {
                JSONObject ticketJson = (JSONObject) eventObj;
                try {
                    long ticketId = ticketJson.getLong("id");
                    if (ticketEntity.getId().equals(String.valueOf(ticketId))) {
                        loadAllComments(ticketEntity.getId());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class CommentAdapter extends BaseAdapter {
        private List<CommentEntity> mList;
        private ISO8601DateFormat dateFormat = new ISO8601DateFormat();
        private LayoutInflater inflater;

        public CommentAdapter(List<CommentEntity> commentEntities) {
            this.mList = commentEntities;
            inflater = LayoutInflater.from(getBaseContext());
        }


        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public CommentEntity getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.em_row_item_comment, null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.timestamp);
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.content);
                viewHolder.flowLayout = (FlowLayout) convertView.findViewById(R.id.flowLayout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                CommentEntity entity = getItem(position);
                viewHolder.tvName.setText(entity.getCreator().getName());
                viewHolder.tvTime.setText(DateUtils.getTimestampString(dateFormat.parse(entity.getUpdated_at())));
                viewHolder.tvContent.setText(entity.getContent());
                final List<CommentEntity.AttachmentsBean> attachments = entity.getAttachments();
                if (attachments != null && attachments.size() > 0){
                    viewHolder.flowLayout.setVisibility(View.VISIBLE);
                    viewHolder.flowLayout.removeAllViews();
                    for (final CommentEntity.AttachmentsBean bean :attachments) {
                        final String remoteUrl = bean.getUrl();
                        final String localName = CommonUtils.stringToMD5(bean.getUrl()) + "-" + bean.getName();
                        final String localPath = PathUtil.getInstance().getFilePath() + File.separator + localName;
                        final String fileType = bean.getType();
                        if (fileType != null && fileType.equals("audio")){
                            View audioView = inflater.inflate(R.layout.em_comment_audio_view, null);
                            audioView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    File file = new File(localPath);
                                    if (file.exists()){
                                        playVoiceItem(v, localPath);
                                    }else{
                                        downloadFile(remoteUrl, localName, fileType);
                                    }
                                }
                            });
                            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(DensityUtil.dip2px(getBaseContext(), 50), DensityUtil.dip2px(getBaseContext(), 30));
                            lp.topMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            lp.bottomMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            lp.leftMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            lp.rightMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            viewHolder.flowLayout.addView(audioView, lp);

                        } else {
                            TextView textView = (TextView) inflater.inflate(R.layout.em_comment_file_textview, null);
                            textView.setText(bean.getName());
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    File file = new File(localPath);
                                    if (file.exists()) {
                                        openLocalFile(file);
                                    } else {
                                        downloadFile(remoteUrl, localName, fileType);
                                    }

                                }
                            });
                            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(getBaseContext(), 30));
                            lp.topMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            lp.bottomMargin = DensityUtil.dip2px(getBaseContext(), 5);
                            viewHolder.flowLayout.addView(textView, lp);
                        }
                    }

                }else{
                    viewHolder.flowLayout.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvTime;
            TextView tvContent;
            FlowLayout flowLayout;
        }

        private View animView;

        private void playVoiceItem(View v, String voiceLocalPath){
            //播放动画
            if (animView != null){
                animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
                animView = null;
            }

            animView = v.findViewById(R.id.id_recorder_anim);
            animView.setBackgroundResource(R.drawable.hd_voice_from_icon);
            AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
            anim.start();

            //播放音频
            MediaManager.playSound(getBaseContext(), voiceLocalPath, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
                }
            });

        }

        private void downloadFile(String remoteUrl,String localName, String fileType){
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), FileDownloadActivity.class);
            intent.putExtra("remoteUrl", remoteUrl);
            intent.putExtra("localName", localName);
            intent.putExtra("type", fileType);
            startActivity(intent);
        }

        /**
         * 打开文件
         * @param file
         */
        private void openLocalFile(File file){
            if (file != null && file.exists()) {
                String suffix = "";
                try {
                    String fileName = file.getName();
                    suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                } catch (Exception e) {
                }
                try{
                    com.hyphenate.helpdesk.easeui.util.CommonUtils.openFileEx(file, com.hyphenate.helpdesk.easeui.util.CommonUtils.getMap(suffix), getBaseContext());
                }catch (Exception e){
                    ToastHelper.show(getBaseContext(), "未安装能打开此文件的软件");
                }
            }
        }

    }


}
