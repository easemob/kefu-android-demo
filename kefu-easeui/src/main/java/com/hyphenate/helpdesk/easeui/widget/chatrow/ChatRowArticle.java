package com.hyphenate.helpdesk.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.google.gson.Gson;
import com.hyphenate.util.DensityUtil;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by tiancruyff on 2017/7/18.
 */

public class ChatRowArticle extends ChatRow {

	private MsgArticles msgArticles = null;

	private LinearLayout artticlesContainer;

	public ChatRowArticle(final Context context, Message message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
		JSONObject jsonArticle = null;
		try {
			jsonArticle = message.getJSONObjectAttribute("msgtype");
			if (jsonArticle != null) {
				Gson gson = new Gson();
				msgArticles = gson.fromJson(jsonArticle.toString(), MsgArticles.class);
			}

			addViews();

		} catch (Exception e) {
		}

	}

	@Override
	protected void onInflatView() {
		inflater.inflate(R.layout.hd_row_received_articles, this);
	}

	@Override
	protected void onFindViewById() {
		artticlesContainer = (LinearLayout) findViewById(R.id.articlesContainer);

	}

	@Override
	protected void onUpdateView() {
	}

	@Override
	protected void onSetUpView() {
		if (msgArticles == null || msgArticles.getArticles() == null) {
			return;
		}
		userAvatarView.setVisibility(GONE);
		usernickView.setVisibility(GONE);
	}

	@Override
	protected void onBubbleClick() {
	}


	private void addViews() {
		if (msgArticles == null || msgArticles.getArticles() == null)
			return;

		if (msgArticles.getArticles().size() == 1) {
			final MsgArticles.ArticlesBean bean = msgArticles.getArticles().get(0);
			View view = inflater.inflate(R.layout.hd_row_article_single_main, null);

			if (view == null) {
				return;
			}

			if (bean.getTitle() != null) {
				((TextView) view.findViewById(R.id.article_main_title)).setText(bean.getTitle());
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(bean.getCreatedTime()));
			if (getResources().getConfiguration().locale == Locale.SIMPLIFIED_CHINESE) {
				((TextView) view.findViewById(R.id.article_create_time)).setText((calendar.get(Calendar.MONTH)+1) + context.getString(R.string.date_month)
						+ (calendar.get(Calendar.DAY_OF_MONTH)) + context.getString(R.string.date_day));
			} else {
				((TextView) view.findViewById(R.id.article_create_time)).setText((calendar.get(Calendar.MONTH)+1) + ":"	+ (calendar.get(Calendar.DAY_OF_MONTH)));
			}

			Glide.with(getContext()).load(bean.getThumbUrl()).error(R.drawable.hd_img_missing).into((ImageView) view.findViewById(R.id.article_main_pic));

			if (bean.getDigest() != null) {
				((TextView) view.findViewById(R.id.article_main_digit)).setText(bean.getDigest());
			}

			(view.findViewById(R.id.ll_article_detail)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getUrl()));
					activity.startActivity(intent);
				}
			});

			artticlesContainer.addView(view);

		} else if (msgArticles.getArticles().size() > 1) {
			artticlesContainer.addView(createArticles(msgArticles.getArticles().get(0), true));
			for (int i = 1; i < msgArticles.getArticles().size(); i++) {
				View divide = new View(context);
				ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 1));
				divide.setBackgroundColor(context.getResources().getColor(R.color.articles_divider_color));
				artticlesContainer.addView(divide, layoutParams);
				artticlesContainer.addView(createArticles(msgArticles.getArticles().get(i), false));
			}
		}
	}


	private View createArticles(final MsgArticles.ArticlesBean bean, Boolean isFirst) {

		RelativeLayout view;

		if (isFirst) {
			view = (RelativeLayout)inflater.inflate(R.layout.hd_row_article_main, null);
			RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.rl_main);
			ImageView mainImage = (ImageView) view.findViewById(R.id.iv_main);
			TextView mainText = (TextView) view.findViewById(R.id.tv_main);
			LinearLayout mainTextLayout = (LinearLayout) view.findViewById(R.id.ll_main_text);

			if (bean.getTitle() != null && bean.getTitle().length() > 0) {
				mainText.setText(bean.getTitle());
				mainTextLayout.setVisibility(VISIBLE);
			}
			Glide.with(getContext()).load(bean.getThumbUrl()).error(R.drawable.hd_img_missing).into(mainImage);
			mainLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getUrl()));
					activity.startActivity(intent);
				}
			});

		} else {
			view = new RelativeLayout(context);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
			int lppadding = DensityUtil.dip2px(context, 10);
			view.setPadding(0, lppadding, 0, lppadding);

			ImageView imageView = new ImageView(context);
			final int imageViewId = new Random().nextInt();
			imageView.setId(imageViewId);
			RelativeLayout.LayoutParams ivLp = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context, 48), DensityUtil.dip2px(context, 48));
			ivLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			Glide.with(getContext()).load(bean.getThumbUrl()).error(R.drawable.hd_img_missing).into(imageView);
			view.addView(imageView, ivLp);

			TextView textView = new TextView(context);
			textView.setPadding(0, 0, DensityUtil.dip2px(context, 10), 0);
			textView.setTextColor(context.getResources().getColor(R.color.article_row_text_color));
			textView.setMaxLines(3);
			textView.setTextSize(17);
			textView.setEllipsize(TextUtils.TruncateAt.END);

			RelativeLayout.LayoutParams tvLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			tvLp.addRule(RelativeLayout.LEFT_OF, imageViewId);

			textView.setText(bean.getDigest());
			view.addView(textView, tvLp);


			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getUrl()));
					activity.startActivity(intent);
				}
			});
		}

		return view;
	}



	public static class MsgArticles {

		private List<ArticlesBean> articles;

		public List<ArticlesBean> getArticles() {
			return articles;
		}

		public void setArticles(List<ArticlesBean> articles) {
			this.articles = articles;
		}

		public static class ArticlesBean {
			/**
			 * title : 城市
			 * digest : s&#39;derwer​​​​​​​
			 * url : http://sandbox.kefu.easemob.com/v1/Tenants/28359/robot/article/html/bdd4d8d1-2eb9-40c5-95c8-21cd7723c61d
			 * thumbUrl : http://sandbox.kefu.easemob.com/v1/Tenant/28359/MediaFiles/thumbnail-5509df90-956c-419a-a6a2-54206fb4a418
			 * createdTime : 1499660562000
			 */

			private String title;
			private String digest;
			private String url;
			private String thumbUrl;
			private long createdTime;

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getDigest() {
				return digest;
			}

			public void setDigest(String digest) {
				this.digest = digest;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getThumbUrl() {
				return thumbUrl;
			}

			public void setThumbUrl(String thumbUrl) {
				this.thumbUrl = thumbUrl;
			}

			public long getCreatedTime() {
				return createdTime;
			}

			public void setCreatedTime(long createdTime) {
				this.createdTime = createdTime;
			}
		}
	}
}
