package com.hyphenate.helpdesk.easeui.emojicon;

import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.helpdesk.emojicon.Emojicon;

public class DefaultEmojiconDatas {

    private static String[] emojis = new String[]{
            SmileUtils.ee_1,
            SmileUtils.ee_2,
            SmileUtils.ee_3,
            SmileUtils.ee_4,
            SmileUtils.ee_5,
            SmileUtils.ee_6,
            SmileUtils.ee_7,
            SmileUtils.ee_8,
            SmileUtils.ee_9,
            SmileUtils.ee_10,
            SmileUtils.ee_11,
            SmileUtils.ee_12,
            SmileUtils.ee_13,
            SmileUtils.ee_14,
            SmileUtils.ee_15,
            SmileUtils.ee_16,
            SmileUtils.ee_17,
            SmileUtils.ee_18,
            SmileUtils.ee_19,
            SmileUtils.ee_20,
            SmileUtils.ee_21,
            SmileUtils.ee_22,
            SmileUtils.ee_23,
            SmileUtils.ee_24,
            SmileUtils.ee_25,
            SmileUtils.ee_26,
            SmileUtils.ee_27,
            SmileUtils.ee_28,
            SmileUtils.ee_29,
            SmileUtils.ee_30,
            SmileUtils.ee_31,
            SmileUtils.ee_32,
            SmileUtils.ee_33,
            SmileUtils.ee_34,
            SmileUtils.ee_35,

    };

    private static int[] icons = new int[]{
            R.drawable.e_e_1,
            R.drawable.e_e_2,
            R.drawable.e_e_3,
            R.drawable.e_e_4,
            R.drawable.e_e_5,
            R.drawable.e_e_6,
            R.drawable.e_e_7,
            R.drawable.e_e_8,
            R.drawable.e_e_9,
            R.drawable.e_e_10,
            R.drawable.e_e_11,
            R.drawable.e_e_12,
            R.drawable.e_e_13,
            R.drawable.e_e_14,
            R.drawable.e_e_15,
            R.drawable.e_e_16,
            R.drawable.e_e_17,
            R.drawable.e_e_18,
            R.drawable.e_e_19,
            R.drawable.e_e_20,
            R.drawable.e_e_21,
            R.drawable.e_e_22,
            R.drawable.e_e_23,
            R.drawable.e_e_24,
            R.drawable.e_e_25,
            R.drawable.e_e_26,
            R.drawable.e_e_27,
            R.drawable.e_e_28,
            R.drawable.e_e_29,
            R.drawable.e_e_30,
            R.drawable.e_e_31,
            R.drawable.e_e_32,
            R.drawable.e_e_33,
            R.drawable.e_e_34,
            R.drawable.e_e_35,
    };


    private static final Emojicon[] DATA = createData();

    private static Emojicon[] createData(){
        Emojicon[] datas = new Emojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new Emojicon(icons[i], emojis[i], Emojicon.Type.NORMAL);
        }
        return datas;
    }

    public static Emojicon[] getData(){
        return DATA;
    }
}