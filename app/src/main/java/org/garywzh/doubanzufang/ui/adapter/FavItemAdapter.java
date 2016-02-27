package org.garywzh.doubanzufang.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import org.garywzh.doubanzufang.R;
import org.garywzh.doubanzufang.model.Item;

import java.util.List;

/**
 * Created by garywzh on 2016/2/24.
 */
public class FavItemAdapter extends RecyclerView.Adapter<FavItemAdapter.MyViewHolder> {

    private final OnItemActionListener mListener;
    private static final int TYPE_ITEM = 1;

    public List<Item> mItems;
    private MultiSelector mMultiSelector;

    public FavItemAdapter(@NonNull final OnItemActionListener listener) {
        mListener = listener;
        setHasStableIds(true);
        mMultiSelector = new MultiSelector();
    }

    public void setDataSource(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate your layout and pass it to view holder
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
        return new MyViewHolder(mListener, view, mMultiSelector);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.fillData(mItems.get(position));
        holder.setSelectionModeBackgroundDrawable(getHighlightedBackground());
    }

    private StateListDrawable getHighlightedBackground() {
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor((Context) mListener, R.color.colorPrimary));
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16843518}, colorDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, null);
        return stateListDrawable;
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(mItems.get(position).tid);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public static class MyViewHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView mTitle;
        public final TextView mTime;
        public final TextView mAuthor;
        public final TextView mGroup;
        private final MultiSelector mMultiSelector;

        private final OnItemActionListener mListener;
        private Item mItem;

        public MyViewHolder(OnItemActionListener listener, View view, MultiSelector multiSelector) {
            super(view, multiSelector);
            mListener = listener;
            mMultiSelector = multiSelector;

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
            if (!mMultiSelector.tapSelection(MyViewHolder.this)) {
                if (mListener == null) {
                    return;
                }
                mListener.onItemOpen(v, mItem);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!mMultiSelector.isSelectable()) {
                mListener.onStartActionMode(mMultiSelector);
                mMultiSelector.setSelectable(true);
                mMultiSelector.setSelected(MyViewHolder.this, true);
                return true;
            }
            return false;
        }
    }

    public interface OnItemActionListener {
        /**
         * @return should refresh data
         */
        boolean onItemOpen(View view, Item item);

        void onStartActionMode(MultiSelector multiSelector);
    }
}