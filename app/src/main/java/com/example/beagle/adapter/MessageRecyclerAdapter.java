package com.example.beagle.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beagle.R;
import com.example.beagle.model.Message;

import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Message> messageList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewQuestion;
        private final TextView textViewAnswer;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textViewQuestion = view.findViewById(R.id.textViewQuestion);
            textViewAnswer = view.findViewById(R.id.textViewAnswer);
        }

        public TextView getTextViewQuestion() {
            return textViewQuestion;
        }


        public TextView getTextViewAnswer() {
            return textViewAnswer;
        }
    }


    public MessageRecyclerAdapter(int layout, List<Message> messageList) {
        this.layout = layout;
        this.messageList = messageList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (messageList.get(position).isFromUser()) {
            viewHolder.getTextViewQuestion().setText(messageList.get(position).getMessageContent());
            viewHolder.textViewAnswer.setVisibility(View.GONE);
        // Else message is AI reply
        } else {
            viewHolder.textViewQuestion.setVisibility(View.GONE);
            viewHolder.getTextViewAnswer().setText(messageList.get(position).getMessageContent());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messageList.size();
    }
}