package com.easemob.helpdeskdemo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.helpdeskdemo.R;
import com.easemob.helpdeskdemo.domain.TicketEntity;
import com.easemob.helpdeskdemo.domain.TicketListResponse;
import com.easemob.helpdeskdemo.interfaces.IListener;
import com.easemob.helpdeskdemo.ui.adapter.TicketListAdapter;
import com.easemob.helpdeskdemo.utils.HelpDeskPreferenceUtils;
import com.easemob.helpdeskdemo.utils.ListenerManager;
import com.easemob.helpdeskdemo.utils.RetrofitAPIManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 留言列表界面
 * 此列表用了Easyrecyclerview (@link https://github.com/Jude95/EasyRecyclerView)
 */
public class TicketListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IListener {

    private static final int MSG_LOAD_MORE_DATA = 0x01;
    private static final int MSG_REFRESH_DATA = 0x02;
    private static final int MSG_LOAD_ERROR = 0x03;
    private static final int PER_PAGE_COUNT = 15;
    private int mCurPageNo;
    private WeakHandler mWeakHandler;

    private List<TicketEntity> ticketEntityList = Collections.synchronizedList(new ArrayList<TicketEntity>());
    private EasyRecyclerView easyRecyclerView;
    private TicketListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.em_fragment_tickets, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWeakHandler = new WeakHandler(this);
        mCurPageNo = 0;
        initView();
        loadFirstDatas();
        ListenerManager.getInstance().registerListener(this);
    }

    private void initView(){
        easyRecyclerView = (EasyRecyclerView) getView().findViewById(R.id.recyclerview);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        easyRecyclerView.setLayoutManager(mLayoutManager);
        //设置分割线
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getActivity().getResources().getDisplayMetrics()));
        itemDecoration.setDrawLastItem(false);
        easyRecyclerView.addItemDecoration(itemDecoration);

        easyRecyclerView.setAdapterWithProgress(mAdapter = new TicketListAdapter(getActivity()));
        mAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //加载更多
                loadMoreData();
            }
        });
        mAdapter.setError(R.layout.view_error);
        mAdapter.setNoMore(R.layout.view_nomore);
        easyRecyclerView.setRefreshListener(this);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TicketEntity ticketEntity = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), TicketDetailActivity.class);
                intent.putExtra("ticket", ticketEntity);
                startActivity(intent);
            }
        });

    }

    @Override
    public void notifyEvent(String str, Object obj) {
        onRefresh();
    }


    private static class WeakHandler extends Handler{
        WeakReference<TicketListFragment> weakReference;
        public WeakHandler(TicketListFragment fragment){
            this.weakReference = new WeakReference<TicketListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TicketListFragment fragment = weakReference.get();
            if (fragment != null){
                switch (msg.what){
                    case MSG_LOAD_MORE_DATA:
                        fragment.updateView((List<TicketEntity>) msg.obj);
                        break;
                    case MSG_REFRESH_DATA:
                        fragment.refreshView((List<TicketEntity>) msg.obj);
                        break;
                    case MSG_LOAD_ERROR:
                        fragment.loadDataError();
                        break;
                }


            }

        }
    }

    public void updateView(List<TicketEntity> entityList){
        if (entityList == null || entityList.size() == 0){
            easyRecyclerView.setRefreshing(false);
            mAdapter.stopMore();
            return;
        }
        ticketEntityList.addAll(entityList);
        mAdapter.addAll(entityList);
        mAdapter.notifyDataSetChanged();
        mAdapter.sort(new Comparator<TicketEntity>() {
            @Override
            public int compare(TicketEntity lhs, TicketEntity rhs) {
                return rhs.getUpdated_at().compareTo(lhs.getUpdated_at());
            }
        });
        mAdapter.pauseMore();
    }

    public void loadDataError(){
        easyRecyclerView.setRefreshing(false);
        easyRecyclerView.showError();

    }

    public void refreshView(List<TicketEntity> entityList){
        if (entityList == null){
            easyRecyclerView.setRefreshing(false);
            mAdapter.pauseMore();
            return;
        }
        ticketEntityList.clear();
        ticketEntityList.addAll(entityList);
        mAdapter.clear();
        mAdapter.addAll(ticketEntityList);
        mAdapter.notifyDataSetChanged();
        mAdapter.sort(new Comparator<TicketEntity>() {
            @Override
            public int compare(TicketEntity lhs, TicketEntity rhs) {
                return rhs.getUpdated_at().compareTo(lhs.getUpdated_at());
            }
        });
        if (entityList.size() < PER_PAGE_COUNT){
            mAdapter.stopMore();
        }
        mAdapter.pauseMore();
    }

    private void loadMoreData(){
        final int nextPage = mCurPageNo + 1;
        RetrofitAPIManager.ApiLeaveMessage apiLeaveMessage = RetrofitAPIManager.retrofit().create(RetrofitAPIManager.ApiLeaveMessage.class);
        String target = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingCustomerAccount();
        String userId = EMChatManager.getInstance().getCurrentUser();
        long tenantId = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingTenantId();
        long projectId = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingProjectId();
        String appkey = EMChat.getInstance().getAppkey();
        Call<TicketListResponse> call = apiLeaveMessage.getTickets(tenantId, projectId, appkey, target, userId, nextPage, PER_PAGE_COUNT);
        call.enqueue(new Callback<TicketListResponse>() {
            @Override
            public void onResponse(Call<TicketListResponse> call, Response<TicketListResponse> response) {
                int statusCode = response.code();
                Message message = mWeakHandler.obtainMessage();
                if (statusCode == 200) {
                    mCurPageNo = nextPage;
                    TicketListResponse ticketListResponse = response.body();
                    message.obj = ticketListResponse.getEntities();
                }
                message.what = MSG_LOAD_MORE_DATA;
                mWeakHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<TicketListResponse> call, Throwable t) {
                Message message = mWeakHandler.obtainMessage();
                message.what = MSG_LOAD_MORE_DATA;
                mWeakHandler.sendMessage(message);
            }
        });

    }


    private void loadFirstDatas() {
        Thread getTicketsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(checkIMToken()){
                    RetrofitAPIManager.ApiLeaveMessage apiLeaveMessage = RetrofitAPIManager.retrofit().create(RetrofitAPIManager.ApiLeaveMessage.class);
                    String target = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingCustomerAccount();
                    String userId = EMChatManager.getInstance().getCurrentUser();
                    long tenantId = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingTenantId();
                    long projectId = HelpDeskPreferenceUtils.getInstance(getActivity()).getSettingProjectId();
                    String appkey = EMChat.getInstance().getAppkey();
                    Call<TicketListResponse> call = apiLeaveMessage.getTickets(tenantId, projectId, appkey, target, userId, 0, PER_PAGE_COUNT);
                    try {
                        Response<TicketListResponse> response = call.execute();
                        int statusCode = response.code();
                        Message message = mWeakHandler.obtainMessage();
                        if (statusCode == 200) {
                            TicketListResponse ticketListResponse = response.body();
                            message.what = MSG_REFRESH_DATA;
                            message.obj = ticketListResponse.getEntities();
                            mCurPageNo = 0;
                        }else{
                            message.what = MSG_LOAD_ERROR;
                        }
                        mWeakHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                       mWeakHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                    }


                }else{
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                easyRecyclerView.setRefreshing(false);
                                easyRecyclerView.showEmpty();
                            }
                        });
                    }


                }
            }
        });

        getTicketsThread.start();

    }


    /**
     * 同步获取token(可能为本地获取也可能为网络获取)
     *
     * @return
     */
    private boolean checkIMToken() {
        // 如果未登录,需要先登录,后获取
        if (!EMChat.getInstance().isLoggedIn()){
            return false;
        }

        try {
            String token = EMChatManager.getInstance().getAccessToken();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().unRegisterListener(this);
    }

    @Override
    public void onRefresh() {
        loadFirstDatas();
    }

}
