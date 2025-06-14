package com.example.projectpambaru;

import static com.example.projectpambaru.DashboardActivity.formatAngka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.TargetViewHolder> {
    private List<Target> targetList;
    private TargetActivity activity;
//    private static final String SUPABASE_URL = "https://bisvlneeendtwzxtygpj.supabase.co";
//    private static final String SUPABASE_BUCKET = "outcome";
//    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM";


    public TargetAdapter(List<Target> targetList, TargetActivity activity) {
        this.targetList = targetList;
        this.activity = activity;
    }

    public class TargetViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTarget;
        public TextView tvNominal;
        public Button markAsDone;
        public ImageView imageViewTarget;
        public TargetViewHolder(View itemView) {
            super(itemView);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvNominal = itemView.findViewById(R.id.tvNominal); // diperbaiki
            markAsDone = itemView.findViewById(R.id.btn_mark_as_done);
            imageViewTarget = itemView.findViewById(R.id.ivTarget);
        }

        public void bind(Target target) {
            if (target != null) {
                tvTarget.setText(target.target);
                String format = formatAngka(target.nominal);
                tvNominal.setText("Rp" + format);
                //Tampilkan gambar
                Glide.with(itemView.getContext())
                        .load(target.gambarUrl)
                        .into(imageViewTarget);
            }
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
        holder.markAsDone.setOnClickListener(v -> activity.targetDelete(target.id));
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }
}
