package com.hyphenate.helpdesk.easeui.util;


import com.hyphenate.agora.FunctionIconItem;

import java.util.ArrayList;
import java.util.List;

public class FlatFunctionUtils {
    private static FlatFunctionUtils sFlatFunctionUtils;
    private List<FunctionIconItem> mIconItems = new ArrayList<>();
    private volatile boolean mIsEnable;
    private FlatFunctionUtils(){

    }

    public static FlatFunctionUtils get(){
        if (sFlatFunctionUtils == null){
            sFlatFunctionUtils = new FlatFunctionUtils();
        }
        return sFlatFunctionUtils;
    }

    public void setIconItems(List<FunctionIconItem> iconItems){
        if (mIconItems != null){
            synchronized (FlatFunctionUtils.class){
                mIconItems.clear();
                mIconItems.addAll(iconItems);
            }
        }
    }

    public List<FunctionIconItem> getIconItems() {
        return mIconItems;
    }

    public void clear(){
        synchronized (FlatFunctionUtils.class){
            mIconItems.clear();
        }
    }

    public void setVideoSwitch(boolean isEnable) {
        this.mIsEnable = isEnable;
    }

    public boolean isEnableVideo() {
        return mIsEnable;
    }
}
