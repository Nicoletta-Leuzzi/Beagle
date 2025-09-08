package com.example.beagle.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beagle.R;
import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(Conversation conversation);
    }

    private final List<Conversation> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ConversationAdapter(List<Conversation> data, OnItemClickListener listener) {
        if (data != null) items.addAll(data);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Conversation c = items.get(position);

        // Titolo semplice: petId o conversationId + data
        String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(c.getCreatedAt()));
        //String title = (c.getPetId() != null ? c.getPetId() : c.getConversationId());
        String title = "pet TItle test";
        h.title.setText(title != null ? title + " • " + date : date);

        // Sottotitolo: anteprima ultimo messaggio
        String preview = "(nessun messaggio)";
        //List<Message> msgs = c.getMessages();
        List<Message> msgs = new ArrayList<>();
        msgs.add(new Message(c.getConversationId(), 0, true, preview));
        if (msgs != null && !msgs.isEmpty()) {
            Message last = msgs.get(msgs.size() - 1);
            if (last.getMessageContent() != null && !last.getMessageContent().isEmpty()) {
                preview = last.getMessageContent();
            }
        }
        h.subtitle.setText(preview);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}