package com.easemob.helpdeskdemo.utils;

import com.easemob.chat.EMChatManager;
import com.easemob.helpdeskdemo.domain.CommentListResponse;
import com.easemob.helpdeskdemo.domain.NewCommentBody;
import com.easemob.helpdeskdemo.domain.NewTicketBody;
import com.easemob.helpdeskdemo.domain.TicketEntity;
import com.easemob.helpdeskdemo.domain.TicketListResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author liyuzhao
 */
public class RetrofitAPIManager {
    private static final String SERVER_URL = "http://kefu.easemob.com/";

    static Retrofit mRetrofit;

    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericClient()) // Set the custom client when building adapter
                    .build();
        }
        return mRetrofit;
    }

    public static OkHttpClient genericClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Authorization", "Easemob IM " + EMChatManager.getInstance().getAccessToken())
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        return okHttpClient;
    }


    public interface ApiLeaveMessage {

        /**
         * 创建一个新的留言
         *
         * @param tenantId      租户ID
         * @param projectId     留言ProjectId  进入“管理员模式 → 留言”，可以看到这个Project ID
         * @param appkey        集成时设置的appkey
         * @param target        接入环信移动客服系统使用的关联的IM服务号
         * @param userId        登录IM的账号
         * @param newTicketBody
         * @return
         */
        @POST("/tenants/{tenantId}/projects/{projectId}/tickets")
        Call<TicketEntity> createTicket(@Path("tenantId") long tenantId,
                                        @Path("projectId") long projectId,
                                        @Query("easemob-appkey") String appkey,
                                        @Query("easemob-target-username") String target,
                                        @Query("easemob-username") String userId,
                                        @Body NewTicketBody newTicketBody);

        /**
         * 获取所有的留言(默认情况下这个api会返回此project中最新的10个tickets。)
         * <p/>
         * <p>
         * 注:
         * 出于安全考虑，访客端查询的时候，只会返回创建者为此访客的留言，也就是访客只能查看自己创建的留言，而不能查看别人创建的
         * </p>
         *
         * @param tenantId  租户ID
         * @param projectId 留言ProjectId  进入“管理员模式 → 留言”，可以看到这个Project ID
         * @param appkey    集成时设置的appkey
         * @param target    接入环信移动客服系统使用的关联的IM服务号
         * @param userId    登录IM的账号
         * @return
         */
        @GET("/tenants/{tenantId}/projects/{projectId}/tickets")
        Call<TicketListResponse> getTickets(@Path("tenantId") long tenantId,
                                            @Path("projectId") long projectId,
                                            @Query("easemob-appkey") String appkey,
                                            @Query("easemob-target-username") String target,
                                            @Query("easemob-username") String userId);


        /**
         * 查询全部的留言
         * <p/>
         * 其他查询参数
         * <p>
         * statusId 按状态id过滤（可选，默认返回所有状态的留言）
         * categoryId 按分类id过滤（可选，默认返回所有分类的留言，-1表示过滤未分类的留言）
         * assignee 按处理者的id过滤：分配给谁的id或者none（区分大小写）来表示获取所有未分配的留言（可选，默认返回所有的处理人的留言）
         * startTime, endTime : timestamp类型的参数，用来按时间段来查询留言，取创建时间（可选，默认返回所有的创建时间的留言）
         * creator 按创建者的id过滤
         * </p>
         * <p/>
         * <p>
         * 注:
         * 出于安全考虑，访客端查询的时候，只会返回创建者为此访客的留言，也就是访客只能查看自己创建的留言，而不能查看别人创建的。
         * 并且这个api只会返回ticket的基本信息，并不包括所有的comments，是为了供列表展示用。
         * </p>
         *
         * @param tenantId  租户ID
         * @param projectId 留言ProjectId  进入“管理员模式 → 留言”，可以看到这个Project ID
         * @param appkey    集成时设置的appkey
         * @param target    接入环信移动客服系统使用的关联的IM服务号
         * @param userId    登录IM的账号
         * @param page      第几页
         * @param pageSize  每页显示数量
         * @return
         */
        @GET("/tenants/{tenantId}/projects/{projectId}/tickets")
        Call<TicketListResponse> getTickets(@Path("tenantId") long tenantId,
                                            @Path("projectId") long projectId,
                                            @Query("easemob-appkey") String appkey,
                                            @Query("easemob-target-username") String target,
                                            @Query("easemob-username") String userId,
                                            @Query("page") int page,
                                            @Query("size") int pageSize);


        /**
         * 获取一个留言的所有评论
         *
         * @param tenantId  租户ID
         * @param projectId 留言ProjectId  进入“管理员模式 → 留言”，可以看到这个Project ID
         * @param ticketId  留言ID
         * @param appkey    集成时设置的appkey
         * @param target    接入环信移动客服系统使用的关联的IM服务号
         * @param userId    登录IM的账号
         * @return
         */
        @GET("/tenants/{tenantId}/projects/{projectId}/tickets/{ticketId}/comments")
        Call<CommentListResponse> getComments(@Path("tenantId") long tenantId,
                                              @Path("projectId") long projectId,
                                              @Path("ticketId") String ticketId,
                                              @Query("easemob-appkey") String appkey,
                                              @Query("easemob-target-username") String target,
                                              @Query("easemob-username") String userId);


        /**
         * 给一个留言添加评论
         *
         * @param tenantId       租户ID
         * @param projectId      留言ProjectId  进入“管理员模式 → 留言”，可以看到这个Project ID
         * @param ticketId       留言ID
         * @param appkey         集成时设置的appkey
         * @param target         接入环信移动客服系统使用的关联的IM服务号
         * @param userId         登录IM的账号
         * @param newCommentBody
         * @return
         */
        @POST("/tenants/{tenantId}/projects/{projectId}/tickets/{ticketId}/comments")
        Call<ResponseBody> createComment(@Path("tenantId") long tenantId,
                                         @Path("projectId") long projectId,
                                         @Path("ticketId") String ticketId,
                                         @Query("easemob-appkey") String appkey,
                                         @Query("easemob-target-username") String target,
                                         @Query("easemob-username") String userId,
                                         @Body NewCommentBody newCommentBody);


    }
}
