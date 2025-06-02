package com.example.projectpambaru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.TargetViewHolder> {
    private List<Target> targetList;
    private onTargetDeleteListener deleteListener;

    public interface onTargetDeleteListener {
        void onDelete(String id); // id dari Firebase
    }

    public TargetAdapter(List<Target> targetList, onTargetDeleteListener deleteListener) {
        this.targetList = targetList;
        this.deleteListener = deleteListener;
    }

    public class TargetViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTarget;
        public TextView tvNominal;
        public Button markAsDone;


        public TargetViewHolder(View itemView) {
            super(itemView);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvNominal = itemView.findViewById(R.id.tvNominal); // diperbaiki
            markAsDone = itemView.findViewById(R.id.btn_mark_as_done);

        }

        public void bind(Target target) {
            tvTarget.setText(target.target);
            tvNominal.setText(String.valueOf(target.nominal));
            markAsDone.setOnClickListener(v -> {
                if (deleteListener != null && target.id != null) {
                    deleteListener.onDelete(target.id); // kirim ID ke MainActivity
                }
            });
        }
    }

    @NonNull
    @Override
    public TargetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_target, parent, false);
        return new TargetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TargetViewHolder holder, int position) {
        Target target = targetList.get(position);
        holder.bind(target); // panggil bind()
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }
}
