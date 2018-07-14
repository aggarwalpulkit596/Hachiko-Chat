package me.dats.com.datsme.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dats.com.datsme.R;

public class DateViewHolder extends RecyclerView.ViewHolder {
    TextView messageText;
    //        public CircleImageView profileImage

    DateViewHolder(View itemView) {
        super(itemView);
        //TODO initialize your xml views
        messageText = itemView.findViewById(R.id.dateTextView);

    }

    public void bind(final String date) {
        //TODO set data to xml view via textivew.setText();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        if (date.equals(formatter.format(currentDate)))
            messageText.setText("Today");
        else if (date.equals(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)))
            messageText.setText("Yesterday");
        else {
//            formatter = new SimpleDateFormat("dd/MMM/yy");
            messageText.setText(date);
        }
    }
}
