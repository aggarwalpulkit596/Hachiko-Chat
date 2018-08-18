package me.dats.com.datsme.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import me.dats.com.datsme.R;

public class OtherProfileQuestionsListAdapter extends RecyclerView.Adapter<OtherProfileQuestionsListAdapter.questionsViewHolder> {

    private Map<String, String> map;

    public OtherProfileQuestionsListAdapter(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public OtherProfileQuestionsListAdapter.questionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.otherprofilequestionlist, parent, false);
        return new OtherProfileQuestionsListAdapter.questionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OtherProfileQuestionsListAdapter.questionsViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class questionsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        EditText AnswerQuestion;
        Button sendAnswer;

        questionsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = mView.findViewById(R.id.textQuestion);
            AnswerQuestion = mView.findViewById(R.id.AnswerQuestion);
            sendAnswer = mView.findViewById(R.id.sendAnswer);

        }

    }
}
