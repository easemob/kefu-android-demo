package com.hyphenate.helpdesk.easeui.domain;

import java.util.List;

/**
 * Created by tiancruyff on 2017/8/29.
 */

public class ArtcleEntity {
	private List<ArticlesBean> articles;

	public List<ArticlesBean> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticlesBean> articles) {
		this.articles = articles;
	}

	public static class ArticlesBean {
		/**
		 * date : 2017-08-29 17:13:23
		 * description : test
		 * <p>
		 * picurl : http://sandbox.kefu.easemob.com/v1/Tenant/28359/MediaFiles/8d5028be-16ef-4af0-93ac-47c06397be1bMS0xNjEyMDQyMzQwMzUuanBn
		 * title : 知识1
		 * url : http://sandbox.kefu.easemob.com/v1/tenants/28359/knowledge/entries/html/62
		 */

		private String date;
		private String description;
		private String picurl;
		private String title;
		private String url;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getPicurl() {
			return picurl;
		}

		public void setPicurl(String picurl) {
			this.picurl = picurl;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
