package com.easemob.easeuix.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.helpdeskdemo.R;
import com.easemob.util.DensityUtil;

/**
 * 按？按钮出来的扩展按钮
 * 
 */
public class EaseChatExtendPrompt extends GridView {

	protected Context context;
	private List<ChatPromptItemModel> itemModels = new ArrayList<ChatPromptItemModel>();

	public EaseChatExtendPrompt(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public EaseChatExtendPrompt(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public EaseChatExtendPrompt(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatExtendMenu);
		int numColumns = ta.getInt(R.styleable.EaseChatExtendMenu_numColumns, 3);
		ta.recycle();

		setNumColumns(numColumns);
//		setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		setGravity(Gravity.CENTER_VERTICAL);
		setVerticalSpacing(DensityUtil.dip2px(context, 6));
	}

	/**
	 * 初始化
	 */
	public void init() {
		setAdapter(new ItemAdapterX(context, itemModels));
	}

	/**
	 * 注册常用语 prompt item
	 * 
	 * @param name
	 *            item的名字
	 * 
	 * @param itemId
	 *            id
	 * 
	 * @param listener
	 *            item的点击事件
	 */
	public void registerPromptItem(String name, int itemId, EaseChatExtendPromptItemClickListener listener) {
		ChatPromptItemModel item = new ChatPromptItemModel();
		item.name = name;
		item.id = itemId;
		item.clickListener = listener;
		itemModels.add(item);
	}

	/**
	 * 注册常用语 prompt item
	 * 
	 * @param nameRes
	 *            item名字的resource id
	 * 
	 * @param itemId
	 *            id
	 * 
	 * @param listener
	 *            item的点击事件
	 */
	public void registerPromptItem(int nameRes, int itemId, EaseChatExtendPromptItemClickListener listener) {
		registerPromptItem(context.getString(nameRes), itemId, listener);
	}

	private class ItemAdapterX extends ArrayAdapter<ChatPromptItemModel> {
		private Context context;

		public ItemAdapterX(Context context, List<ChatPromptItemModel> objects) {
			super(context, 1, objects);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ChatPromptItem promptItem = null;
			if (convertView == null) {
				convertView = new ChatPromptItem(context);
			}
			promptItem = (ChatPromptItem) convertView;
			promptItem.setText(getItem(position).name);
			promptItem.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (getItem(position).clickListener != null) {
						getItem(position).clickListener.onClick(getItem(position).id, v);
					}
				}
			});

			return convertView;
		}

	}

	public interface EaseChatExtendPromptItemClickListener {
		void onClick(int itemId, View view);
	}

	class ChatPromptItemModel {
		String name;
		int id;
		EaseChatExtendPromptItemClickListener clickListener;
	}

	class ChatPromptItem extends LinearLayout {
		private TextView textView;

		public ChatPromptItem(Context context, AttributeSet attrs, int defStyle) {
			this(context, attrs);
		}

		public ChatPromptItem(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context, attrs);
		}

		public ChatPromptItem(Context context) {
			super(context);
			init(context, null);
		}

		private void init(Context context, AttributeSet attrs) {
			LayoutInflater.from(context).inflate(R.layout.easex_chat_prompt_item, this);
			textView = (TextView) findViewById(R.id.text);
		}

		public void setText(int resid) {
			textView.setText(resid);
		}

		public void setText(String text) {
			textView.setText(text);
		}

	}

}
