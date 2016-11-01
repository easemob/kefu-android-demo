package com.hyphenate.helpdesk.easeui.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hyphenate.helpdesk.easeui.UIProvider;


public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        //理论上应该放在launcher activity,放在基类中所有集成此库的app都可以避免此问题
//        if(!isTaskRoot()){
//            Intent intent = getIntent();
//            String action = intent.getAction();
//            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
//                finish();
//                return;
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        UIProvider.getInstance().getNotifier().reset();
    }

    /**
     * 通过xml查找相应的ID，通用方法
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }

}
