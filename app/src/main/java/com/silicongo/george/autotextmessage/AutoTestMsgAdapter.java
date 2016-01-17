package com.silicongo.george.autotextmessage;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suxch on 2016/1/3.
 */
public class AutoTestMsgAdapter extends RecyclerView.Adapter<AutoTestMsgAdapter.ViewHolder> {
    private ArrayList<TextMsgInfo> mDataset;
    private AutoTextClickListener mListener;

    public static int position = -1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener, View.OnCreateContextMenuListener {
        // each data item is just a string in this case
        public ImageView ivContactImage;
        public TextView tvPhoneNumber;
        public TextView tvTime;
        public TextView tvTextMessage;

        private AutoTextClickListener mListener;

        public ViewHolder(View v, AutoTextClickListener listener) {
            super(v);
            ivContactImage = (ImageView) v.findViewById(R.id.ivContactImage);
            tvPhoneNumber = (TextView) v.findViewById(R.id.tvPhoneNumber);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            tvTextMessage = (TextView) v.findViewById(R.id.tvTextMessage);

            mListener = listener;

            v.setOnCreateContextMenuListener(this);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            //menu.setHeaderTitle("Select The Action");
            menu.add(0, R.id.settingItemEdit, 0, R.string.edit);//groupId, itemId, order, title
            menu.add(0, R.id.settingItemDelete, 0, R.string.delete);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getAdapterPosition());
                position = getAdapterPosition();
            }
        }

        @Override
        public boolean onLongClick(View arg0) {
            if(mListener != null){
                mListener.onItemLongClick(arg0, getAdapterPosition());
                position = getAdapterPosition();
            }
            return false;
        }
    }

    public interface AutoTextClickListener {
        void onItemClick(View view,int postion);
        void onItemLongClick(View view,int postion);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AutoTestMsgAdapter(ArrayList<TextMsgInfo> myDataset, AutoTextClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AutoTestMsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_msg_item, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvPhoneNumber.setText(mDataset.get(position).get(TextMsgInfo.ROW_PHONE_NUMBER).getString());
        holder.tvTime.setText(mDataset.get(position).get(TextMsgInfo.ROW_TIME_HOUR).getInt() +
                ":" + mDataset.get(position).get(TextMsgInfo.ROW_TIME_MINUTE).getInt());
        holder.tvTextMessage.setText(mDataset.get(position).get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * 向指定位置添加元素
     * @param position
     * @param value
     */
    public void add(int position, TextMsgInfo value) {
        if(position > mDataset.size()) {
            position = mDataset.size();
        }
        if(position < 0) {
            position = 0;
        }
        mDataset.add(position, value);
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        notifyItemInserted(position);
    }

    /**
     * 移除指定位置元素
     * @param position
     * @return
     */
    public TextMsgInfo remove(int position) {
        if(position > mDataset.size()-1) {
            return null;
        }
        TextMsgInfo value = mDataset.remove(position);
        notifyItemRemoved(position);
        return value;
    }

    public TextMsgInfo get(int position) {
        if(position > mDataset.size()-1) {
            return null;
        }
        TextMsgInfo value = mDataset.get(position);
        return value;
    }
}
