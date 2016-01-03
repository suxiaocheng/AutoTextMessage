package com.silicongo.george.autotextmessage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by suxch on 2016/1/3.
 */
public class AutoTestMsgAdapter extends RecyclerView.Adapter<AutoTestMsgAdapter.ViewHolder> {
    private TextMsgInfo[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView ivContactImage;
        public TextView tvPhoneNumber;
        public TextView tvTime;
        public TextView tvTextMessage;
        public CheckBox cbAutoTextEnabled;

        public ViewHolder(View v) {
            super(v);
            ivContactImage = (ImageView) v.findViewById(R.id.ivContactImage);
            tvPhoneNumber = (TextView) v.findViewById(R.id.tvPhoneNumber);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            tvTextMessage = (TextView) v.findViewById(R.id.tvTextMessage);
            cbAutoTextEnabled = (CheckBox) v.findViewById(R.id.cbAutoTextEnabled);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AutoTestMsgAdapter(TextMsgInfo[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AutoTestMsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_msg_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvPhoneNumber.setText(mDataset[position].get(TextMsgInfo.ROW_PHONE_NUMBER).getString());
        holder.tvTime.setText(mDataset[position].get(TextMsgInfo.ROW_TIME_HOUR).getInt() +
                ":" + mDataset[position].get(TextMsgInfo.ROW_TIME_MINUTE).getInt());
        holder.tvTextMessage.setText(mDataset[position].get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());
        holder.cbAutoTextEnabled.setChecked(mDataset[position].get(TextMsgInfo.ROW_ENABLE).getBool());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
