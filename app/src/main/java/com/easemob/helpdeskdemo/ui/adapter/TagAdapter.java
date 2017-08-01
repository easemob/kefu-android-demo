package com.easemob.helpdeskdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;
import com.hyphenate.helpdesk.model.EvaluationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyuzhao on 12/07/2017.
 */

public class TagAdapter<T> extends BaseAdapter {

	private final Context mContext;
	private final List<T> mDataList;

	public TagAdapter(Context context){
		this.mContext = context;
		this.mDataList = new ArrayList<>();
	}


	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public T getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_tag);
		T t = mDataList.get(position);
		if (t instanceof String){
			textView.setText((String) t);
		}else if (t instanceof  EvaluationInfo.TagInfo){
			textView.setText(((EvaluationInfo.TagInfo) t).getName());
		}
		return view;
	}

	public void onlyAddAll(List<T> datas){
		mDataList.addAll(datas);
		notifyDataSetChanged();
	}

	public void clearAndAndAll(List<T> datas){
		mDataList.clear();
		onlyAddAll(datas);
	}

}
