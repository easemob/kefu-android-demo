package com.hyphenate.helpdesk.videokit.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.helpdesk.videokit.uitls.CecBlankSpaceUtils;


public class CecBlankSpaceActivity extends Activity implements CecBlankSpaceUtils.IBlankSpace {

    public static void startBlankSpaceActivity(Context context){
        Intent intent = new Intent(context, CecBlankSpaceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setBackgroundColor(Color.TRANSPARENT);
        setContentView(textView);

        try{
            CecBlankSpaceUtils.getCecBlankSpaceUtils().setIBlankSpace(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void pageFinish() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            CecBlankSpaceUtils.getCecBlankSpaceUtils().clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
