package com.jb.dev.cabapp_admin.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.model.AreaModel;

public class AreaAdapter extends FirestoreRecyclerAdapter<AreaModel, AreaAdapter.CabHolder> {
    SharedPreferences sp;
    private onItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AreaAdapter(@NonNull FirestoreRecyclerOptions<AreaModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CabHolder holder, int position, @NonNull AreaModel model) {
        holder.textViewAreaName.setText(model.getAreaName());
    }

    @NonNull
    @Override
    public CabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_area, parent, false);
        return new CabHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public String getName(int position) {
        String s = getSnapshots().getSnapshot(position).get(Constants.AREA_NAME).toString();
        return s;
    }

    public String getId(int position) {
        String s = getSnapshots().getSnapshot(position).getId();
        return s;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    class CabHolder extends RecyclerView.ViewHolder {
        public TextView textViewAreaName;

        public CabHolder(@NonNull View itemView) {
            super(itemView);
            textViewAreaName = itemView.findViewById(R.id.card_area_txt_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });
        }
    }
}
