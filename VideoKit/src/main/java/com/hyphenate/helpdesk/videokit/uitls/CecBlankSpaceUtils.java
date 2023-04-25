package com.hyphenate.helpdesk.videokit.uitls;

import java.util.ArrayList;
import java.util.List;

public class CecBlankSpaceUtils {
    private final static CecBlankSpaceUtils S_CEC_BLANK_SPACE_UTILS = new CecBlankSpaceUtils();

    private final List<IBlankSpace> mList = new ArrayList<>();

    public static CecBlankSpaceUtils getCecBlankSpaceUtils() {
        return S_CEC_BLANK_SPACE_UTILS;
    }

    public void setIBlankSpace(IBlankSpace blankSpace) {

        if (mList != null && blankSpace != null){
            mList.add(blankSpace);
        }
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
