/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.helpdeskdemo.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.R;

public class CommonUtils {

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
     * 根据消息内容和消息类型获取消息内容提示
     * 
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct == EMMessage.Direct.RECEIVE) {
                //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                digest = getStrng(context, R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                digest = getStrng(context, R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息
            digest = getStrng(context, R.string.picture);
            break;
        case VOICE:// 语音消息
            digest = getStrng(context, R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = getStrng(context, R.string.video);
            break;
        case TXT: // 文本消息
            if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            }else{
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
            }
            break;
        case FILE: //普通文件消息
            digest = getStrng(context, R.string.file);
            break;
        default:
            System.err.println("error, unknow type");
            return "";
        }

        return digest;
    }
    
    static String getStrng(Context context, int resId){
        return context.getResources().getString(resId);
    }
	
	
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}
	
	public static Bitmap convertBitmap(Bitmap oldBitmap,int reqWidth,int reqHeight)
	{
		//获取图片的宽高
		int width=oldBitmap.getWidth();
		int height=oldBitmap.getHeight();
		
		float scaleWidth = 0;
		float scaleHeight = 0;
		 
		if(width<height)
		{
			if(height<reqHeight){
				int newHeight = reqHeight;
				float newWidth=width*(((float)reqHeight)/height);
				//计算缩放比例
				scaleWidth=((float)newWidth)/width +1;
				scaleHeight=((float)newHeight)/height +1;
			}else{
				//设置想要的大小
				int newWidth=reqWidth;
				float newHeight=height*(((float)reqWidth)/width);
				//计算缩放比例
				scaleWidth = ((float) newWidth) / width;
				scaleHeight = ((float) newHeight) / height;
			}
		}else{
			if(width<reqWidth){
				//设置想要的大小
				int newWidth=reqWidth;
				float newHeight=height*(((float)reqWidth)/width);
				//计算缩放比例
				scaleWidth = ((float) newWidth) / width+1;
				scaleHeight = ((float) newHeight) / height+1;
			}else{
				//设置想要的大小
				int newHeight=reqHeight;
				float newWidth=width*(((float)reqHeight)/height);
				//计算缩放比例
				scaleWidth=((float)newWidth)/width;
				scaleHeight=((float)newHeight)/height;
			}
		}
		//取得想要缩放的matrix参数
		 Matrix matrix=new Matrix();
		 matrix.postScale(scaleWidth, scaleHeight);
		//得到新的图片
		 Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix,
			      true);
		return newbm;
	}
	//转换dip为px 
		public static int convertDip2Px(Context context,int dip)
		{	
			float scale=context.getResources().getDisplayMetrics().density;
			return  (int)(dip*scale + 0.5f*(dip>=0?1:-1)); 
		}
		
		//转换px为dip 
		public static int convertPx2Dip(Context context,int px)
		{
			float scale = context.getResources().getDisplayMetrics().density; 
		     return (int)(px/scale + 0.5f*(px>=0?1:-1)); 
		}

}
