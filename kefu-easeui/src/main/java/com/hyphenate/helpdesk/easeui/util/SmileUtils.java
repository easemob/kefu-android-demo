package com.hyphenate.helpdesk.easeui.util;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.ImageSpan;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.emojicon.DefaultEmojiconDatas;
import com.hyphenate.helpdesk.emojicon.Emojicon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";

    public static final String ee_1 = "[):]";
    public static final String ee_2 = "[:D]";
    public static final String ee_3 = "[;)]";
    public static final String ee_4 = "[:-o]";
    public static final String ee_5 = "[:p]";
    public static final String ee_6 = "[(H)]";
    public static final String ee_7 = "[:@]";
    public static final String ee_8 = "[:s]";
    public static final String ee_9 = "[:$]";
    public static final String ee_10 = "[:(]";
    public static final String ee_11 = "[:'(]";
    public static final String ee_12 = "[:|]";
    public static final String ee_13 = "[(a)]";
    public static final String ee_14 = "[8o|]";
    public static final String ee_15 = "[8-|]";
    public static final String ee_16 = "[+o(]";
    public static final String ee_17 = "[<o)]";
    public static final String ee_18 = "[|-)]";
    public static final String ee_19 = "[*-)]";
    public static final String ee_20 = "[:-#]";
    public static final String ee_21 = "[:-*]";
    public static final String ee_22 = "[^o)]";
    public static final String ee_23 = "[8-)]";
    public static final String ee_24 = "[(|)]";
    public static final String ee_25 = "[(u)]";
    public static final String ee_26 = "[(S)]";
    public static final String ee_27 = "[(*)]";
    public static final String ee_28 = "[(#)]";
    public static final String ee_29 = "[(R)]";
    public static final String ee_30 = "[({)]";
    public static final String ee_31 = "[(})]";
    public static final String ee_32 = "[(k)]";
    public static final String ee_33 = "[(F)]";
    public static final String ee_34 = "[(W)]";
    public static final String ee_35 = "[(D)]";

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();


    static {
        Emojicon[] emojicons = DefaultEmojiconDatas.getData();
        for(int i = 0; i < emojicons.length; i++){
            addPattern(emojicons[i].getEmojiText(), emojicons[i].getIcon());
        }
        UIProvider.EmojiconInfoProvider emojiconInfoProvider = UIProvider.getInstance().getEmojiconInfoProvider();
        if(emojiconInfoProvider != null && emojiconInfoProvider.getTextEmojiconMapping() != null){
            for(Map.Entry<String, Object> entry : emojiconInfoProvider.getTextEmojiconMapping().entrySet()){
                addPattern(entry.getKey(), entry.getValue());
            }
        }

    }

    /**
     * 添加文字表情mapping
     * @param emojiText emoji文本内容
     * @param icon 图片资源id或者本地路径
     */
    public static void addPattern(String emojiText, Object icon){
        emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
    }


    /**
     * replace existing spannable with smiles
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Object> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    Object value = entry.getValue();
                    if(value instanceof String && !((String) value).startsWith("http")){
                        File file = new File((String) value);
                        if(!file.exists() || file.isDirectory()){
                            return false;
                        }
                        spannable.setSpan(new ImageSpan(context, Uri.fromFile(file)),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else{
                        spannable.setSpan(new ImageSpan(context, (Integer)value),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    public static boolean containsKey(String key){
        boolean b = false;
        for (Map.Entry<Pattern, Object> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }

        return b;
    }

    public static int getSmilesSize(){
        return emoticons.size();
    }

}
