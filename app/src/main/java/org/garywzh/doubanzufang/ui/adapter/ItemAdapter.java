package org.garywzh.doubanzufang.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final OnItemActionListener mListener;
    private ResponseBean mResponseBean;

    public ItemAdapter(@NonNull OnItemActionListener listener) {
        mListener = listener;
        setHasStableIds(true);
    }

    public void setDataSource(ResponseBean responseBean) {
        mResponseBean = responseBean;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
            return new VHItem(mListener, view);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
            return new VHHeader(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHItem) {
            final Item item = mResponseBean.items.get(position - 1);
            ((VHItem) holder).fillData(item);
        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            ((VHHeader) holder).fillData(mResponseBean);
        }
    }

    @Override
    public long getItemId(int position) {
        return position == 0 ? 1 : Integer.parseInt(mResponseBean.items.get(position - 1).tid);
    }

    @Override
    public int getItemCount() {
        return mResponseBean == null ? 1 : mResponseBean.items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView mTitle;
        public final TextView mTime;
        public final TextView mAuthor;
        public final TextView mGroup;

        private final OnItemActionListener mListener;
        private Item mItem;

        public VHItem(View view) {
            this(null, view);
        }

        public VHItem(OnItemActionListener listener, View view) {
            super(view);
            mListener = listener;

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            mTitle = ((TextView) view.findViewById(R.id.tv_title));
            mTime = ((TextView) view.findViewById(R.id.tv_time));
            mAuthor = ((TextView) view.findViewById(R.id.tv_author));
            mGroup = ((TextView) view.findViewById(R.id.tv_group));
        }

        public void fillData(Item item) {
            if (item.equals(mItem)) {
                return;
            }
            mItem = item;

            mTitle.setText(item.ttl);
            mTime.setText(item.tcr);
            mAuthor.setText(item.anm);
            mGroup.setText(item.dgd);
        }

        @Override
        public void onClick(View v) {
            if (mListener == null) {
                return;
            }
            mListener.onItemOpen(v, mItem);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener == null) {
                return false;
            }
            mListener.onItemLongClick(v, mItem);
            return true;
        }
    }

    public static class VHHeader extends RecyclerView.ViewHolder {

        public TextView mUpdateTime;

        public VHHeader(View headerView) {
            super(headerView);

            mUpdateTime = ((TextView) headerView.findViewById(R.id.tv_updatetime));
        }

        public void fillData(ResponseBean responseBean) {
            if (responseBean == null){
                mUpdateTime.setText("数据加载中...");

            }else {
                mUpdateTime.setText("数据更新时间: " + responseBean.last_update_time);
            }
        }
    }

    public interface OnItemActionListener {
        /**
         * @return should refresh data
         */
        boolean onItemOpen(View view, Item item);

        void onItemLongClick(View view, Item item);
    }
}
