package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;

/**
 * 常用语列表
 */
public class ShortCutMsgActivity extends BaseActivity {

    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private TextView txtTitle;
    private ImageButton btnBack;
    //    private String[] promptItemStrings = { getString(R.string.text_fahuo), getString(R.string.text_weight), getString(R.string.text_color), getString(R.string.text_kuaidi) };
    protected int[] promptItemStrings = {R.string.text_fahuo, R.string.text_weight, R.string.text_color, R.string.text_kuaidi};

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.em_activity_shortcut);
        initView();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for (int item : promptItemStrings) {
            mAdapter.add(getString(item));
        }
        mListView.setAdapter(mAdapter);
        txtTitle.setText("常用语");
        btnBack.setOnClickListener(new onBackClickListener());
        mListView.setOnItemClickListener(new ListOnItemClick());
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.list);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnBack = (ImageButton) findViewById(R.id.ib_back);
    }


    class ListOnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String content = parent.getItemAtPosition(position).toString();
            setResult(RESULT_OK, new Intent().putExtra("content", content));
            closeActivity();
        }
    }

    class onBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            closeActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(0, R.anim.activity_close);
    }


}
