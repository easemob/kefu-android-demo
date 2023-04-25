package com.easemob.veckit.utils;

import java.util.ArrayList;
import java.util.List;

public class BlankSpaceUtils {
    private final static BlankSpaceUtils sBlankSpaceUtils = new BlankSpaceUtils();

    private List<IBlankSpace> mList = new ArrayList<>();
    private volatile boolean mIsVecVideoFinish;

    public static BlankSpaceUtils getBlankSpaceUtils() {
        return sBlankSpaceUtils;
    }

    public void setIBlankSpace(IBlankSpace blankSpace) {
        if (mList != null && blankSpace != null){
            mList.add(blankSpace);
        }
    }

    public void setVecVideoFinish(boolean vecVideoFinish) {
        mIsVecVideoFinish = vecVideoFinish;
    }

    public boolean isVecVideoFinish() {
        return mIsVecVideoFinish;
    }

    public void notifyFinish(){
        if (mList != null){
            for (IBlankSpace blankSpace : mList){
                blankSpace.pageFinish();
            }
            mList.clear();
        }
    }

    public void clear(){
        if (mList != null){
            for (IBlankSpace blankSpace : mList){
                blankSpace.pageFinish();
            }
            mList.clear();
        }
    }

    public interface IBlankSpace{
        void pageFinish();
    }
}
