package com.easemob.helpdeskdemo.ui;

import android.os.Bundle;
import android.view.View;

import com.easemob.helpdeskdemo.R;

/**
 * Created by liyuzhao on 16/03/2017.
 */

public class SpecialActivity extends DemoBaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        int selected = getIntent().getIntExtra("selected", -1);
        if (selected == 1){
            setContentView(R.layout.em_activity_special1);
        }else if (selected == 2){
            setContentView(R.layout.em_activity_special2);
        }else if (selected == 3){
            setContentView(R.layout.em_activity_special3);
        }


    }

    public void back(View view){
        finish();
    }


}
