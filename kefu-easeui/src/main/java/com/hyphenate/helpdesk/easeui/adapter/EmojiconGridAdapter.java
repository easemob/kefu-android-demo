package com.hyphenate.helpdesk.easeui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.helpdesk.emojicon.Emojicon;
import com.hyphenate.helpdesk.emojicon.Emojicon.Type;

import java.io.File;
import java.util.List;

public class EmojiconGridAdapter extends ArrayAdapter<Emojicon>{

    private Type emojiconType;


    public EmojiconGridAdapter(Context context, int textViewResourceId, List<Emojicon> objects, Type emojiconType) {
        super(context, textViewResourceId, objects);
        this.emojiconType = emojiconType;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            if(emojiconType == Type.BIG_EXPRESSION){
                convertView = View.inflate(getContext(), R.layout.hd_row_big_expression, null);
            }else{
                convertView = View.inflate(getContext(), R.layout.hd_row_expression, null);
            }
        }
        
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
        Emojicon emojicon = getItem(position);
        assert emojicon != null;
        if(textView != null && emojicon.getName() != null){
            textView.setText(emojicon.getName());
        }
        if(SmileUtils.DELETE_KEY.equals(emojicon.getEmojiText())){
            imageView.setImageResource(R.drawable.hd_delete_expression);
        }else{
            if(emojicon.getIcon() != 0){
                imageView.setImageResource(emojicon.getIcon());
            }else {
                File localIcon = null;
                File localBigIcon = null;
                if(!TextUtils.isEmpty(emojicon.getIconPath())) {
                    localIcon = new File(emojicon.getIconPath());
                }
                if (!TextUtils.isEmpty(emojicon.getBigIconPath())) {
                    localBigIcon = new File(emojicon.getBigIconPath());
                }
                if (localIcon != null && localIcon.exists()) {
                    Glide.with(getContext()).load(emojicon.getIconPath()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                    Glide.with(getContext()).load(emojicon.getIconPath()).placeholder(R.drawable.hd_default_expression).into(imageView);
                } else if (localBigIcon != null && localBigIcon.exists()) {
                    Glide.with(getContext()).load(emojicon.getBigIconPath()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                    Glide.with(getContext()).load(emojicon.getBigIconPath()).placeholder(R.drawable.hd_default_expression).into(imageView);
                } else if (!TextUtils.isEmpty(emojicon.getIconRemotePath())) {
                    Glide.with(getContext()).load(emojicon.getIconRemotePath()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                    Glide.with(getContext()).load(emojicon.getIconRemotePath()).placeholder(R.drawable.hd_default_expression).into(imageView);
                } else if (!TextUtils.isEmpty(emojicon.getBigIconRemotePath())) {
                    Glide.with(getContext()).load(emojicon.getBigIconRemotePath()).apply(RequestOptions.placeholderOf(R.drawable.hd_default_expression)).into(imageView);
//                    Glide.with(getContext()).load(emojicon.getBigIconRemotePath()).placeholder(R.drawable.hd_default_expression).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.hd_default_expression);
                }
            }
        }
        
        
        return convertView;
    }
    
}
