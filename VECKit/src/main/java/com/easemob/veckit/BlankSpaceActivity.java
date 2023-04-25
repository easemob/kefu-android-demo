package com.easemob.veckit;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.easemob.veckit.utils.BlankSpaceUtils;

public class BlankSpaceActivity extends Activity implements BlankSpaceUtils.IBlankSpace {

    public static void startBlankSpaceActivity(Context context){
        Intent intent = new Intent(context, BlankSpaceActivity.class);
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
            BlankSpaceUtils.getBlankSpaceUtils().setIBlankSpace(this);
            // 如果视频页面已经不存在了，需要关闭
            if (BlankSpaceUtils.getBlankSpaceUtils().isVecVideoFinish()){
                BlankSpaceUtils.getBlankSpaceUtils().clear();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ooooooooooo","finish");
                        finish();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void pageFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            BlankSpaceUtils.getBlankSpaceUtils().clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
