package com.hyphenate.helpdesk.easeui.emojicon;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.emojicon.Emojicon;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情图片控件
 */
public class EmojiconMenu extends EmojiconMenuBase{

    private int emojiconColumns;
    private int bigEmojiconColumns;
    private final int defaultBigColumns = 4;
    private final int defaultColumns = 7;
    private EmojiconScrollTabBar tabBar;
    private EmojiconIndicatorView indicatorView;
    private EmojiconPagerView pagerView;

    private List<EmojiconGroupEntity> emojiconGroupList = new ArrayList<EmojiconGroupEntity>();


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EmojiconMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EmojiconMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiconMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.hd_widget_emojicon, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HDEmojiconMenu);
        emojiconColumns = ta.getInt(R.styleable.HDEmojiconMenu_hdEmojiconColumns, defaultColumns);
        bigEmojiconColumns = ta.getInt(R.styleable.HDEmojiconMenu_hdBigEmojiconRows, defaultBigColumns);
        ta.recycle();

        pagerView = (EmojiconPagerView) findViewById(R.id.pager_view);
        indicatorView = (EmojiconIndicatorView) findViewById(R.id.indicator_view);
        tabBar = (EmojiconScrollTabBar) findViewById(R.id.tab_bar);

    }

    public synchronized void init(List<EmojiconGroupEntity> groupEntities){
        if(groupEntities == null || groupEntities.size() == 0){
            return;
        }
        for(EmojiconGroupEntity groupEntity : groupEntities){
            emojiconGroupList.add(groupEntity);
            if (TextUtils.isEmpty(groupEntity.getName())) {
                tabBar.addTab(groupEntity.getIcon());
            } else {
                tabBar.addTab(groupEntity.getName());
            }
        }

        pagerView.setPagerViewListener(new EmojiconPagerViewListener());
        pagerView.init(emojiconGroupList, emojiconColumns,bigEmojiconColumns);

        tabBar.setTabBarItemClickListener(new EmojiconScrollTabBar.EaseScrollTabBarItemClickListener() {

            @Override
            public void onItemClick(int position) {
                pagerView.setGroupPostion(position);
            }
        });

    }


    /**
     * 添加表情组
     * @param groupEntity
     */
    public synchronized void addEmojiconGroup(EmojiconGroupEntity groupEntity){
        emojiconGroupList.add(groupEntity);
        pagerView.addEmojiconGroup(groupEntity, true);
        if (TextUtils.isEmpty(groupEntity.getName())) {
            tabBar.addTab(groupEntity.getIcon());
        } else {
            tabBar.addTab(groupEntity.getName());
        }
    }

    /**
     * 添加一系列表情组
     * @param groupEntitieList
     */
    public synchronized void addEmojiconGroup(List<EmojiconGroupEntity> groupEntitieList){
        for(int i= 0; i < groupEntitieList.size(); i++){
            EmojiconGroupEntity groupEntity = groupEntitieList.get(i);
            emojiconGroupList.add(groupEntity);
            pagerView.addEmojiconGroup(groupEntity, i == groupEntitieList.size() - 1);
            if (TextUtils.isEmpty(groupEntity.getName())) {
                tabBar.addTab(groupEntity.getIcon());
            } else {
                tabBar.addTab(groupEntity.getName());
            }
        }

    }

    /**
     * 移除全部表情组
     */
    public synchronized void removeAllEmojiconGroup() {
        emojiconGroupList.clear();
        pagerView.removeAllEmojiconGroup();
        tabBar.removeAllTab();
    }

    public void setTabBarVisibility(boolean isVisible){
        if(!isVisible){
            tabBar.setVisibility(View.GONE);
        }else{
            tabBar.setVisibility(View.VISIBLE);
        }
    }


    private class EmojiconPagerViewListener implements EmojiconPagerView.EaseEmojiconPagerViewListener {

        @Override
        public void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize) {
            indicatorView.init(groupMaxPageSize);
            indicatorView.updateIndicator(firstGroupPageSize);
            tabBar.selectedTo(0);
        }

        @Override
        public void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup) {
            indicatorView.updateIndicator(pagerSizeOfGroup);
            tabBar.selectedTo(groupPosition);
        }

        @Override
        public void onGroupInnerPagePostionChanged(int oldPosition, int newPosition) {
            indicatorView.selectTo(oldPosition, newPosition);
        }

        @Override
        public void onGroupPagePostionChangedTo(int position) {
            indicatorView.selectTo(position);
        }

        @Override
        public void onGroupMaxPageSizeChanged(int maxCount) {
            indicatorView.updateIndicator(maxCount);
        }

        @Override
        public void onDeleteImageClicked() {
            if(listener != null){
                listener.onDeleteImageClicked();
            }
        }

        @Override
        public void onExpressionClicked(Emojicon emojicon) {
            if(listener != null){
                listener.onExpressionClicked(emojicon);
            }
        }

    }

}