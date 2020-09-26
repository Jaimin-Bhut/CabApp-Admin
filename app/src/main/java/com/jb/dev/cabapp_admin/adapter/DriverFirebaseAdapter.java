package com.jb.dev.cabapp_admin.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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

public class DriverFirebaseAdapter extends FirestoreRecyclerAdapter<DriverModel, DriverFirebaseAdapter.DriverHolder> {
    SharedPreferences sp;
    private onItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public DriverFirebaseAdapter(@NonNull FirestoreRecyclerOptions<DriverModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final DriverHolder holder, int position, @NonNull DriverModel model) {
        holder.textViewDriverName.setText(model.getName());
        holder.textViewDriverAddress.setText(model.getAddress());
        holder.textViewDriverPhone.setText(model.getPhone());
        holder.textViewDriverEmail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public DriverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_driver, parent, false);
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
            textViewDriverName = itemView.findViewById(R.id.card_driver_txt_name);
            textViewDriverAddress = itemView.findViewById(R.id.card_driver_txt_address);
            textViewDriverPhone = itemView.findViewById(R.id.card_driver_txt_phone_number);
            textViewDriverEmail = itemView.findViewById(R.id.card_driver_txt_email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                    final String fname, phonenumber, email, address;
                    fname = textViewDriverName.getText().toString();
                    phonenumber = textViewDriverPhone.getText().toString();
                    email = textViewDriverEmail.getText().toString();
                    address = textViewDriverAddress.getText().toString();
                    sp = v.getContext().getSharedPreferences(Constants.DRIVER_DETAILS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Constants.DRIVER_NAME_KEY, fname);
                    editor.putString(Constants.DRIVER_PHONE_NUMBER_KEY, phonenumber);
                    editor.putString(Constants.DRIVER_EMAIL_KEY, email);
                    editor.putString(Constants.DRIVER_ID, String.valueOf(postion));
                    editor.putString(Constants.DRIVER_ADDRESS_KEY, address);
                    editor.apply();

                }


            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
