package com.jb.dev.cabapp_admin.adapter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.model.DriverModel;

public class DriverBookingAdapter extends FirestoreRecyclerAdapter<DriverModel, DriverBookingAdapter.DriverHolder> {
    SharedPreferences sp;
    private onItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public DriverBookingAdapter(@NonNull FirestoreRecyclerOptions<DriverModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final DriverHolder holder, final int position, @NonNull DriverModel model) {
        holder.textViewDriverName.setText(model.getName());
        holder.textViewDriverEmail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public DriverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_booking_driver, parent, false);
        return new DriverHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public String getId(int position) {
        String s = getSnapshots().getSnapshot(position).get(Constants.DRIVER_EMAIL_KEY).toString();
        return s;
    }

    class DriverHolder extends RecyclerView.ViewHolder {
        public TextView textViewDriverName;
        public TextView textViewDriverAddress;
        public TextView textViewDriverEmail;
        public TextView textViewDriverPhone;

        public DriverHolder(@NonNull final View itemView) {
            super(itemView);
            textViewDriverName = itemView.findViewById(R.id.card_booking_txt_name);
            textViewDriverEmail = itemView.findViewById(R.id.card_booking_txt_email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion, textViewDriverEmail.getText().toString());
                    }
                }


            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String name);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
