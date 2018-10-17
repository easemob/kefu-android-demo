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
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.ArticlesInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.util.DensityUtil;

import java.util.Random;

/**
 * Created by tiancruyff on 2017/7/18.
 */

public class ChatRowArticle extends ChatRow {

	private LinearLayout artticlesContainer;

	public ChatRowArticle(final Context context, Message message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
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
		userAvatarView.setVisibility(GONE);
		usernickView.setVisibility(GONE);

		ArticlesInfo msgArticles;

		if ((msgArticles = MessageHelper.getArticlesMessage(message)) != null) {
			addViews(msgArticles);
		}
	}

	@Override
	protected void onBubbleClick() {
	}


	private void addViews(ArticlesInfo msgArticles) {
		artticlesContainer.removeAllViews();

		if (msgArticles == null || msgArticles.getArticles() == null)
			return;

		if (msgArticles.getArticles().size() == 1) {
			final ArticlesInfo.ArticleItem bean = msgArticles.getArticles().get(0);
			View view = inflater.inflate(R.layout.hd_row_article_single_main, null);

			if (view == null) {
				return;
			}

			if (bean.getTitle() != null) {
				((TextView) view.findViewById(R.id.article_main_title)).setText(bean.getTitle());
			}

			((TextView) view.findViewById(R.id.article_create_time)).setText(bean.getDate());


//			Glide.with(getContext()).load(bean.getPicurl()).error(R.drawable.hd_img_missing).into((ImageView) view.findViewById(R.id.article_main_pic));
			Glide.with(getContext()).load(bean.getPicurl()).apply(RequestOptions.errorOf(R.drawable.hd_img_missing)).into((ImageView) view.findViewById(R.id.article_main_pic));

			if (bean.getDescription() != null) {
				((TextView) view.findViewById(R.id.article_main_digit)).setText(bean.getDescription());
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


	private View createArticles(final ArticlesInfo.ArticleItem bean, Boolean isFirst) {

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
//			Glide.with(getContext()).load(bean.getPicurl()).error(R.drawable.hd_img_missing).into(mainImage);
			Glide.with(getContext()).load(bean.getPicurl()).apply(RequestOptions.errorOf(R.drawable.hd_img_missing)).into(mainImage);
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

//			Glide.with(getContext()).load(bean.getPicurl()).error(R.drawable.hd_img_missing).into(imageView);
			Glide.with(getContext()).load(bean.getPicurl()).apply(RequestOptions.errorOf(R.drawable.hd_img_missing)).into(imageView);
			view.addView(imageView, ivLp);

			TextView textView = new TextView(context);
			textView.setPadding(0, 0, DensityUtil.dip2px(context, 10), 0);
			textView.setTextColor(context.getResources().getColor(R.color.article_row_text_color));
			textView.setMaxLines(3);
			textView.setTextSize(17);
			textView.setEllipsize(TextUtils.TruncateAt.END);

			RelativeLayout.LayoutParams tvLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			tvLp.addRule(RelativeLayout.LEFT_OF, imageViewId);

			textView.setText(bean.getDescription());
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

}
