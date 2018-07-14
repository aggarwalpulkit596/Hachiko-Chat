package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.R;

public class ChatLeftViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = ChatRightViewHolder.class.getSimpleName();
    TextView messageText;
    //        public CircleImageView profileImage;
    TextView time1;
    LinearLayout msg;

    public ChatLeftViewHolder(View itemView) {
        super(itemView);
        //TODO initialize your xml views
        messageText = itemView.findViewById(R.id.message_text);
        time1 = itemView.findViewById(R.id.text_message_time);
        msg = itemView.findViewById(R.id.message_layout);
    }

    public void bind(final Messages chatModel, Context mContext) {
        //TODO set data to xml view via textivew.setText();
        messageText.setText(chatModel.getMessage());
        String time = DateUtils.formatDateTime(mContext, chatModel.getTime(), DateUtils.FORMAT_SHOW_TIME);

        time1.setText(time);
    }
}