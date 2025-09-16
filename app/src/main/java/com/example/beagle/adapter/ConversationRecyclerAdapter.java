package com.example.beagle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beagle.R;
import com.example.beagle.model.Conversation;

import java.util.List;

public class ConversationRecyclerAdapter extends RecyclerView.Adapter<ConversationRecyclerAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onClick(Conversation conversation);
    }

    public interface OnItemLongClickListener {
        void onLongClick(View view, Conversation conversation);
    }

    private final int layout;
    private final List<Conversation> conversationList;
    private Context context;
    private final OnItemClickListener onItemClickListener;
    private final OnItemLongClickListener onItemLongClickListener;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private final TextView tvConversationTitle;
        private final TextView tvActivePet;
        private final TextView tvLastModified;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvConversationTitle = view.findViewById(R.id.tvConversationTitle);
            tvActivePet = view.findViewById(R.id.tvActivePet);
            tvLastModified = view.findViewById(R.id.tvLastModified);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        public TextView getTvConversationTitle() {
            return tvConversationTitle;
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
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onClick(conversationList.get(position));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener == null) {
                return false;
            }
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return false;
            }
            onItemLongClickListener.onLongClick(view, conversationList.get(position));
            return true;
        }
    }


    public ConversationRecyclerAdapter(int layout, List<Conversation> conversationList,
                                       OnItemClickListener onItemClickListener,
                                       OnItemLongClickListener onItemLongClickListener) {
        this.layout = layout;
        this.conversationList = conversationList;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }


    @NonNull
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
        viewHolder.getTvConversationTitle().setText(""+conversationList.get(position).getConversationTitle());
        viewHolder.getTvActivePet().setText(""+conversationList.get(position).getPetId()); // TODO: deve ritornare nome pet, non id
        viewHolder.getTvLastModified().setText(""+conversationList.get(position).getCreatedAt()); // TODO: deve ritornare data ultimo messaggio in formato "DD month YYYY"


    }


    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}

