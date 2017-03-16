package com.easemob.helpdeskdemo.widget.chatrow;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.ui.SpecialActivity;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;

public class ChatRowSpecialText extends ChatRow{

    private TextView tvTitle;
    private ImageView ivLogo;
    private TextView tvContent;
    private View llSpecial1;
    private View llSpecial2;
    private int selected = -1;

    public ChatRowSpecialText(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ?
                R.layout.em_row_received_message_special4 : com.hyphenate.helpdesk.R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvContent = (TextView) findViewById(R.id.tv_content);
        llSpecial1 = findViewById(R.id.ll_special1);
        llSpecial2 = findViewById(R.id.ll_special2);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String content = txtBody.getMessage();
        setEnable1(true);
        if (content.equals("_zx_fe27b0e97ba939f18d7f4c1e98f92174")){//fe27b0e97ba939f18d7f4c1e98f92174  信金保业务都有什么内容
            selected = 2;
            tvTitle.setText("信金保业务");
            ivLogo.setImageResource(R.drawable.special4);
            tvContent.setText("“信金保” 具有中信证券自主品牌，是中信证券与优秀基金公司强强联合，为投资者打造的 …");
        }else if (content.equals("_zx_b26ea3b6ce6d0a7dd34e78027e914ba4")){//b26ea3b6ce6d0a7dd34e78027e914ba4  开户需要准备什么
            selected = 3;
            tvTitle.setText("开户准备");
            ivLogo.setImageResource(R.drawable.special5);
            tvContent.setText("开户，您只需准备：\n" +
                    "有效期内本人二代身份证原件 · 本人借记卡 · 本人手机");

        }else if (content.equals("_zx_86e998b0da000c8786235469bdba2520")){//86e998b0da000c8786235469bdba2520  投资顾问信息都有什么
            selected = 1;
            tvTitle.setText("信·策略 25期：\n" +
                    "立足平衡 寻找确定性 ");
            ivLogo.setImageResource(R.drawable.special6);
            tvContent.setText("经历了前连个月的疯涨，市场稳\n" +
                    "步前行到了三个月。影响市场的\n" +
                    "核心因素是否有变化? 市场整 …");

        }
    }

    private void setEnable1(boolean enable){
        if (enable){
            llSpecial1.setVisibility(View.VISIBLE);
            llSpecial2.setVisibility(View.GONE);
        }else{
            llSpecial1.setVisibility(View.GONE);
            llSpecial2.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onBubbleClick() {
        // TODO Auto-generated method stub
        if (selected > 0){
            Intent intent = new Intent(getContext(), SpecialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("selected", selected);
            ((Activity)getContext()).startActivity(intent);
        }


    }



}