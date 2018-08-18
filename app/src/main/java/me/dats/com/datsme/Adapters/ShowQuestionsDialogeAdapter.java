package me.dats.com.datsme.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.dats.com.datsme.R;

public class ShowQuestionsDialogeAdapter extends RecyclerView.Adapter<ShowQuestionsDialogeAdapter.questionsViewHolder> {

    private ArrayList<String> messages;
    private Context mContext;
    private AlertDialog alertDialog;
    private String OtherUserId, myuserId;

    public ShowQuestionsDialogeAdapter(ArrayList<String> messages, Context mContext, AlertDialog alertDialog, String OtherUserId, String myusedId) {
        this.messages = messages;
        this.myuserId = myusedId;
        this.mContext = mContext;
        this.OtherUserId = OtherUserId;
        this.alertDialog = alertDialog;

    }

    @Override
    public ShowQuestionsDialogeAdapter.questionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.otherprofilequestion, parent, false);
        return new ShowQuestionsDialogeAdapter.questionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final questionsViewHolder holder, final int position) {

        holder.question.setText(messages.get(position).toString());
        holder.AnswerQuestion.setText("");
        holder.sendPrivate.setChecked(false);
        holder.AnswerQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        holder.sendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.AnswerQuestion.getText().toString().trim().length() > 0) {
                    DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Answers").child(OtherUserId);
                    String key = query.push().getKey();
                    Map<String, Object> Map = new HashMap<>();
                    Map.put("Sender", myuserId);
                    Map.put("question", holder.question.getText().toString());
                    Map.put("Answer", holder.AnswerQuestion.getText().toString());
                    Map.put("time", ServerValue.TIMESTAMP);
                    if (holder.sendPrivate.isChecked()) {
                        Map.put("privacy", "private");
                    } else {
                        Map.put("privacy", "public");
                    }

                    Map.put("aprroval", false);
                    Map.put("seen", false);
                    query.child(key).updateChildren(Map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            messages.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
//        holder.sendAnswer.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View view) {
//                if (holder.AnswerQuestion.getText().toString().trim().length() > 0) {
//
//
//                    DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Answers").child(OtherUserId).child("MyQuestionsKey");
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Boolean found=false;
//                            if (dataSnapshot.exists())
//                            {
//                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//
//                                    String s = dsp.getValue().toString();
//                                    if (s.equals(messages.get(position))) {
//                                        found = true;
//                                        Log.d("questionkeyis", "onDataChange: " + dsp.getKey().toString());
//                                        makeEntrytoDatabase(holder, position, dsp.getKey().toString());
//                                        break;
//                                    }
//                                }
//                            }
//                            if(!found)
//                            {
//                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Answers").child(OtherUserId).child("MyQuestionsKey");
//                                final String questionkey = db.push().getKey();
//                                db.child(questionkey).setValue(messages.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        makeEntrytoDatabase(holder,position,questionkey);
//                                    }
//                                });
//                            }
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                }
//            }
//        });
    }

    public void makeEntrytoDatabase(final questionsViewHolder holder, final int position, String key) {
        String privacy = "public";
        DatabaseReference query2 = FirebaseDatabase.getInstance().getReference().child("Answers").child(OtherUserId).child("MyQuestionsAnswers").child(key);
        String newkey = query2.push().getKey();

        Map<String, Object> Map = new HashMap<>();
        Map.put("Sender", myuserId);
        Map.put("question", holder.question.getText().toString());
        Map.put("Answer", holder.AnswerQuestion.getText().toString());
        Map.put("time", ServerValue.TIMESTAMP);
        Map.put("privacy", privacy);
        Map.put("aprroval", false);
        Map.put("seen", false);

        query2.child(newkey).updateChildren(Map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                messages.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                Toast.makeText(mContext, "" + holder.AnswerQuestion.getText().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "send", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class questionsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        EditText AnswerQuestion;
        Button sendAnswer;
        CheckBox sendPrivate;

        questionsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            sendPrivate = mView.findViewById(R.id.sendprivate);
            sendPrivate.setChecked(false);
            question = mView.findViewById(R.id.textQuestion);
            AnswerQuestion = mView.findViewById(R.id.AnswerQuestion);
            sendAnswer = mView.findViewById(R.id.sendAnswer);

        }

    }
}


