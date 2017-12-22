package com.sctw.bonniedraw.utility;

import android.support.v7.util.DiffUtil;

import com.sctw.bonniedraw.bean.WorkInfoBean;

import java.util.List;

/**
 * Created by Fatorin on 2017/12/21.
 */

public class DiffCallBack extends DiffUtil.Callback {
    private List<WorkInfoBean> mOldData, mNewData;

    public DiffCallBack(List<WorkInfoBean> mOldData, List<WorkInfoBean> mNewData) {
        this.mOldData = mOldData;
        this.mNewData = mNewData;
    }

    @Override
    public int getOldListSize() {
        return mOldData != null ? mOldData.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewData != null ? mNewData.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldData.get(oldItemPosition).getWorkId().equals(mNewData.get(newItemPosition).getWorkId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        WorkInfoBean oldBean = mOldData.get(oldItemPosition);
        WorkInfoBean newBean = mNewData.get(newItemPosition);
        if (!oldBean.getLikeCount().equals(newBean.getLikeCount())) {
            return false;//如果有内容不同，就返回false
        }
        if (!oldBean.getMsgCount().equals(newBean.getMsgCount())) {
            return false;//如果有内容不同，就返回false
        }
        if (oldBean.getIsFollowing() != newBean.getIsFollowing()) {
            return false;//如果有内容不同，就返回false
        }
        if (oldBean.isCollection() && newBean.isCollection()){
            return false;
        }
            return true;
    }

    /*@Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        WorkInfoBean oldBean = mOldData.get(oldItemPosition);
        WorkInfoBean newBean = mNewData.get(newItemPosition);
        Bundle payload=new Bundle();
        if (!oldBean.getLikeCount().equals(newBean.getLikeCount())) {
            payload.putInt("likeCount",newBean.getLikeCount());
        }
        if (oldBean.getIsFollowing() != newBean.getIsFollowing()) {
            payload.putInt("isFollowing",newBean.getIsFollowing());
        }
        if (oldBean.isCollection() && newBean.isCollection()){
            payload.putBoolean("isCollection",newBean.isCollection());
        }
        if (payload.size() == 0)
            return null;
        return payload;
    }*/
}
