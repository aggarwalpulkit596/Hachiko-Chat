package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.R;


public class ChatRightViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = ChatRightViewHolder.class.getSimpleName();
    TextView messageText;
    //        public CircleImageView profileImage;
    TextView time1;
    LinearLayout msg;
    ImageView img;
    RelativeLayout lay;

    ChatRightViewHolder(View itemView) {
        super(itemView);
        //TODO initialize your xml views
        messageText = itemView.findViewById(R.id.message_text);
        time1 = itemView.findViewById(R.id.text_message_time);
        msg = itemView.findViewById(R.id.message_layout);
        img = itemView.findViewById(R.id.img);
        lay = itemView.findViewById(R.id.message_root_layout);
    }

    public void bind(final Messages chatModel, Context mContext) {
        //TODO set data to xml view via textivew.setText();
        Linkify.addLinks(messageText, Linkify.WEB_URLS);
        final String message = chatModel.getMessage();
        if (chatModel.getType().equals("text")) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(chatModel.getMessage());
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.GONE);
            if (!message.equals("default"))
                Picasso.get()
                        .load(message)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar)
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                if (!message.equals("default"))
                                    Picasso.get()
                                            .load(message)
                                            .placeholder(R.drawable.default_avatar)
                                            .into(img);
                            }

                        });
        }
        String time = DateUtils.formatDateTime(mContext, chatModel.getTime(), DateUtils.FORMAT_SHOW_TIME);

        time1.setText(time);
    }
}
