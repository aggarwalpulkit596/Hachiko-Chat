package me.dats.com.datsme.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.UserAnswers;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class ExpendableRecyclerViewAdapter extends RecyclerView.Adapter<ExpendableRecyclerViewAdapter.ViewHolder> {

    private List<UserAnswers> data;
    private Context context;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    public ExpendableRecyclerViewAdapter(List<UserAnswers> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.expendablelistitem, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UserAnswers userAnswer = data.get(position);
        holder.setIsRecyclable(false);
        holder.expandableLayout.setInRecyclerView(true);
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(data.get(position).getSender().toString());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users otherUser = dataSnapshot.getValue(Users.class);
                holder.bind(userAnswer, otherUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                expandState.put(position, false);
            }
        });

        holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);
        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
            }
        });
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout textView;
        public RelativeLayout buttonLayout;
        public TextView textnotification, question, answer, sendprivately;
        public CircleImageView image;
        public Button approve, reject;
        public LinearLayout buttoncontainer;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            buttonLayout = v.findViewById(R.id.button);
            expandableLayout = v.findViewById(R.id.expandableLayout);
            textnotification = v.findViewById(R.id.text_notification);
            image = v.findViewById(R.id.image_notification);
            question = v.findViewById(R.id.expendablequestiontv);
            answer = v.findViewById(R.id.expendableanswertv);
            approve = v.findViewById(R.id.expendableapprovebt);
            reject = v.findViewById(R.id.expendablerejectbt);
            sendprivately = v.findViewById(R.id.sendprivately);
            buttoncontainer = v.findViewById(R.id.expandablebutton);


        }

        public void bind(UserAnswers userAnswer, Users OtherUser) {
            Picasso.get()
                    .load(OtherUser.getThumb_image())
                    .placeholder(R.drawable.default_avatar)
                    .centerCrop()
                    .fit()
                    .into(image);
            textnotification.setText(OtherUser.getName() + " has answered your question.");

            question.setText(userAnswer.getQuestion().toString());
            answer.setText(userAnswer.getAnswer().toString());
            if (userAnswer.getPrivacy().equals("public")) {
                sendprivately.setVisibility(View.INVISIBLE);
                buttoncontainer.setVisibility(View.VISIBLE);
            } else {
                sendprivately.setVisibility(View.VISIBLE);
                buttoncontainer.setVisibility(View.GONE);
            }


        }
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}
