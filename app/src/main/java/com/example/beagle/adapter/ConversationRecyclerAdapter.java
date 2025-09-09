package com.example.beagle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beagle.R;
import com.example.beagle.model.Conversation;

import java.util.List;

public class ConversationRecyclerAdapter extends RecyclerView.Adapter<ConversationRecyclerAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onClick(Conversation conversation);
    }

    private int layout;
    private List<Conversation> conversationList;
    private Context context;
    private final OnItemClickListener onItemClickListener;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvTime;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvTitle = view.findViewById(R.id.tvTitle);
            tvSubtitle = view.findViewById(R.id.tvSubtitle);
            tvTime = view.findViewById(R.id.tvTime);

            view.setOnClickListener(this);

        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvSubtitle() {
            return tvSubtitle;
        }

        public TextView getTvTime() {
            return tvTime;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onClick(conversationList.get(getAdapterPosition()));
        }
    }


    public ConversationRecyclerAdapter(int layout, List<Conversation> conversationList, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.conversationList = conversationList;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTvTitle().setText(""+conversationList.get(position).getConversationId());
        viewHolder.getTvSubtitle().setText(""+conversationList.get(position).getPetId());
        viewHolder.getTvTime().setText(""+conversationList.get(position).getCreatedAt());


    }


    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}


