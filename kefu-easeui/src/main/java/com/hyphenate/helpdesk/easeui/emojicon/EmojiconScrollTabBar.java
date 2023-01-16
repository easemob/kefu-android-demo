package com.hyphenate.helpdesk.easeui.emojicon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.hyphenate.helpdesk.R;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class EmojiconScrollTabBar extends RelativeLayout{

    private Context context;
    private HorizontalScrollView scrollView;
    private LinearLayout tabContainer;
    
    private List<View> tabList = new ArrayList<>();
    private EaseScrollTabBarItemClickListener itemClickListener;
    
    private int tabWidth = 60;

    public EmojiconScrollTabBar(Context context) {
        this(context, null);
    }

    public EmojiconScrollTabBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EmojiconScrollTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.hd_widget_emojicon_tab_bar, this);
        
        scrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);
        tabContainer = (LinearLayout) findViewById(R.id.tab_container);
    }

    /**
     * 添加tab
     * @param content
     */
    public void addTab(String content) {
        View tabView = View.inflate(context, R.layout.hd_scroll_tab_text_item, null);
        TextView textView = (TextView) tabView.findViewById(R.id.tv_text);
        textView.setText(content);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, tabWidth), LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(imgParams);
        tabContainer.addView(tabView);
        tabList.add(textView);
        final int position = tabList.size() -1;
        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 添加tab
     * @param icon
     */
    public void addTab(int icon){
        View tabView = View.inflate(context, R.layout.hd_scroll_tab_item, null);
        ImageView imageView = (ImageView) tabView.findViewById(R.id.iv_icon);
        imageView.setImageResource(icon);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, tabWidth), LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imgParams);
        tabContainer.addView(tabView);
        tabList.add(imageView);
        final int position = tabList.size() -1;
        imageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }
    
    /**
     * 移除tab
     * @param position
     */
    public void removeTab(int position){
        tabContainer.removeViewAt(position);
        tabList.remove(position);
    }

    public void removeAllTab() {
        tabContainer.removeAllViews();
        tabList.clear();
    }

    public void selectedTo(int position){
        scrollTo(position);
        for (int i = 0; i < tabList.size(); i++) {
            if (position == i) {
                tabList.get(i).setBackgroundColor(getResources().getColor(R.color.emojicon_tab_selected));
            } else {
                tabList.get(i).setBackgroundColor(getResources().getColor(R.color.emojicon_tab_nomal));
            }
        }
    }
    
    private void scrollTo(final int position){
        int childCount = tabContainer.getChildCount();
        if(position < childCount){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    int mScrollX = tabContainer.getScrollX();
                    int childX = (int) ViewCompat.getX(tabContainer.getChildAt(position));

                    if(childX < mScrollX){
                        scrollView.scrollTo(childX,0);
                        return;
                    }

                    int childWidth = tabContainer.getChildAt(position).getWidth();
                    int hsvWidth = scrollView.getWidth();
                    int childRight = childX + childWidth;
                    int scrollRight = mScrollX + hsvWidth;

                    if(childRight > scrollRight){
                        scrollView.scrollTo(childRight - scrollRight,0);
                    }
                }
            });
        }
    }
    
    
    public void setTabBarItemClickListener(EaseScrollTabBarItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    
    
    public interface EaseScrollTabBarItemClickListener{
        void onItemClick(int position);
    }
    

}
