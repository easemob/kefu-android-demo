package com.easemob.veckit.ui.flow;


import android.widget.TextView;

public class FlowBean {
    public String tenantId;
    public int score;
    public String createDateTime;
    public String updateDateTime;


    public int id;
    public String tagName;
    TextView textView;
    boolean isSelected;


    public FlowBean(int id, String tenantId, int score, String tagName, String createDateTime, String updateDateTime) {
        this.id = id;
        this.tenantId = tenantId;
        this.score = score;
        this.tagName = tagName;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    void clear(){
        if (textView != null){
            textView.setOnClickListener(null);
        }
        textView = null;
    }
}
