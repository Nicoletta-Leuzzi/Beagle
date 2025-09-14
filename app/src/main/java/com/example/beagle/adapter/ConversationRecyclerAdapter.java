package com.example.beagle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beagle.R;
import com.example.beagle.database.DataRoomDatabase;
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
        private final TextView tvActivePet;
        private final TextView tvLastModified;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvTitle = view.findViewById(R.id.tvTitle);
            tvActivePet = view.findViewById(R.id.tvActivePet);
            tvLastModified = view.findViewById(R.id.tvLastModified);

            view.setOnClickListener(this);

        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvActivePet() {
            return tvActivePet;
        }

        public TextView getTvLastModified() {
            return tvLastModified;
        }

        // TODO: funzionalità elimina conversazione
        /* public void onButtonPressed(DeleteButton deleteButton, boolean isPressed) {
            if (isPressed)
                DataRoomDatabase.getDatabase(viewHolder.getContext()).ConversationDAO.delete(conversationList.get(position));
        } */

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
        viewHolder.getTvTitle().setText(""+conversationList.get(position).getConversationTitle());
        viewHolder.getTvActivePet().setText(""+conversationList.get(position).getPetId()); // TODO: deve ritornare nome pet, non id
        viewHolder.getTvLastModified().setText(""+conversationList.get(position).getCreatedAt()); // TODO: deve ritornare data ultimo messaggio in formato "DD month YYYY"


    }


    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}


