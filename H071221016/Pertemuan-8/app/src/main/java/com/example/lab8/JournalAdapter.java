package com.example.lab8;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{

    private Context context;
    private List<Journal> notesList;

    public JournalAdapter(Context context, List<Journal> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = notesList.get(position);
        holder.tvTitle.setText(journal.getJudul());
        holder.tvDesc.setText(journal.getDeskripsi());

        String timestampText;
        if (journal.getCreatedAt().equals(journal.getUpdatedAt())) {
            timestampText = "Created at " + journal.getCreatedAt();
        } else {
            timestampText = "Updated at " + journal.getUpdatedAt();
        }
        holder.tvTimestamp.setText(timestampText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JournalActivity.class);
            intent.putExtra("record_id", journal.getId());
            context.startActivity(intent);
        });
    }




    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvTimestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}

