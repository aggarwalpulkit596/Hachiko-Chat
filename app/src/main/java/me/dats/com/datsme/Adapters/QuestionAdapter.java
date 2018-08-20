package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import me.dats.com.datsme.Activities.QuestionsActivity;
import me.dats.com.datsme.R;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private List<String> questionsList;
    private List<String> saveList;
    private Context mContext;
    private QuestionsActivity activity;
    private boolean onlyShow;

    public QuestionAdapter(List<String> questionsList, List<String> s, Context mContext,QuestionsActivity Activity) {
        this.questionsList = questionsList;
        this.mContext = mContext;
        this.onlyShow = false;
        this.saveList = s;
        this.activity=Activity;
    }

    public QuestionAdapter(List<String> questionsList, Context mContext) {
        this.questionsList = questionsList;
        this.mContext = mContext;
        this.onlyShow = true;
    }


    @Override
    public QuestionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_layout, parent, false);

        return new QuestionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionAdapter.MyViewHolder holder, int position) {

        holder.question.setText(questionsList.get(position).toString());
        if (onlyShow) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.question.setVisibility(View.VISIBLE);
            holder.addIcon.setVisibility(View.INVISIBLE);
            holder.numbering.setVisibility(View.INVISIBLE);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference quesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Questions");
                    quesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                if (dsp.getValue().equals(holder.question.getText())) {
                                    dsp.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });
        } else {

            holder.delete.setVisibility(View.INVISIBLE);
            holder.question.setVisibility(View.VISIBLE);
            holder.addIcon.setVisibility(View.VISIBLE);
            holder.numbering.setVisibility(View.VISIBLE);

            String p = String.valueOf(position + 1);
            holder.numbering.setText(p + ".");
            holder.addIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (saveList.contains(holder.question.getText().toString())) {
                        saveList.remove(holder.question.getText().toString());
                        holder.addIcon.setImageResource(R.drawable.addcircle);
                    } else {
                        if (saveList.size() < 5) {
                            saveList.add(holder.question.getText().toString());
                            holder.addIcon.setImageResource(R.drawable.ic_action_name);
                        } else {
                            Toast.makeText(mContext, "Maximum 5 questions", Toast.LENGTH_SHORT).show();
                        }

                    }
                    activity.hidesave();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public List<String> getSaveList() {
        return saveList;
    }
    public void setSaveList(List<String> l)
    {
        saveList=l;
    }

    public List<String> getQuestionList() {
        return this.questionsList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView question, numbering;
        public ImageView addIcon, delete;


        public MyViewHolder(View view) {
            super(view);

            question = view.findViewById(R.id.question);
            numbering = view.findViewById(R.id.numbering);
            addIcon = view.findViewById(R.id.addIcon);
            delete = view.findViewById(R.id.deleteIcon);
        }
    }
}

