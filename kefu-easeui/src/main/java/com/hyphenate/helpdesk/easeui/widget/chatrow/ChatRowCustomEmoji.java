package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.ui.ShowBigImageActivity;
import com.hyphenate.helpdesk.manager.EmojiconManager.EmojiconEntity;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.util.EMLog;

import java.io.File;


/**
 * Created by tiancruyff on 2017/10/23.
 */

public class ChatRowCustomEmoji extends ChatRow {

	protected ImageView imageView;
	protected String remoteUrl;
	protected EmojiconEntity emojiconEntity;

	public ChatRowCustomEmoji(Context context, Message message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.hd_row_received_custom_emoji : R.layout.hd_row_sent_custom_emoji, this);
	}

	@Override
	protected void onFindViewById() {
		percentageView = (TextView) findViewById(R.id.percentage);
		imageView = (ImageView) findViewById(R.id.image);
		imageView.setVisibility(GONE);
	}


	@Override
	protected void onSetUpView() {
		remoteUrl = MessageHelper.getCustomEmojiMessage(message);
		emojiconEntity = ChatClient.getInstance().emojiconManager().getEmojicon(remoteUrl);
		if (emojiconEntity == null) {
			return;
		}

		if (!TextUtils.isEmpty(emojiconEntity.origin.remoteUrl)) {
			File localOrigin = new File(emojiconEntity.origin.localUrl);
			if (localOrigin.exists()) {
				Glide.with(getContext()).load(emojiconEntity.origin.localUrl).apply(RequestOptions.placeholderOf(R.drawable.hd_default_image)).into(imageView);
//				Glide.with(getContext()).load(emojiconEntity.origin.localUrl).placeholder(R.drawable.hd_default_image).into(imageView);
			} else {
				Glide.with(getContext()).load(emojiconEntity.origin.remoteUrl).apply(RequestOptions.placeholderOf(R.drawable.hd_default_image)).into(imageView);
//				Glide.with(getContext()).load(emojiconEntity.origin.remoteUrl).placeholder(R.drawable.hd_default_image).into(imageView);
			}
		} else if (!TextUtils.isEmpty(emojiconEntity.thumbnail.remoteUrl)) {
			File localThumb = new File(emojiconEntity.thumbnail.localUrl);
			if (localThumb.exists()) {
				Glide.with(getContext()).load(emojiconEntity.thumbnail.localUrl).apply(RequestOptions.placeholderOf(R.drawable.hd_default_image)).into(imageView);
//				Glide.with(getContext()).load(emojiconEntity.thumbnail.localUrl).placeholder(R.drawable.hd_default_image).into(imageView);
			} else {
				Glide.with(getContext()).load(emojiconEntity.thumbnail.remoteUrl).apply(RequestOptions.placeholderOf(R.drawable.hd_default_image)).into(imageView);
//				Glide.with(getContext()).load(emojiconEntity.thumbnail.remoteUrl).placeholder(R.drawable.hd_default_image).into(imageView);
			}
		} else {
			EMLog.e(TAG, "emojiconEntity date wrong");
			return;
		}
		progressBar.setVisibility(View.GONE);
		percentageView.setVisibility(View.GONE);
		imageView.setVisibility(VISIBLE);
	}

	@Override
	protected void onUpdateView() {
		if (adapter instanceof MessageAdapter) {
			((MessageAdapter) adapter).refresh();
		} else {
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void onBubbleClick() {
		if (emojiconEntity == null || TextUtils.isEmpty(emojiconEntity.origin.remoteUrl)) {
			return;
		}
		Intent intent = new Intent(context, ShowBigImageActivity.class);
		File file = new File(emojiconEntity.origin.localUrl);
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			intent.putExtra("uri", uri);
		} else {
			EMLog.e(TAG, "no local file.");
			return;
		}
		context.startActivity(intent);
	}

}


