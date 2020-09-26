package com.jb.dev.cabapp_admin.adapter;

import android.content.Context;
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
import com.jb.dev.cabapp_admin.model.CabModel;

public class CabFirebaseAdapter extends FirestoreRecyclerAdapter<CabModel, CabFirebaseAdapter.CabHolder> {
    private onItemClickListener listener;
    SharedPreferences sp;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CabFirebaseAdapter(@NonNull FirestoreRecyclerOptions<CabModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CabHolder holder, int position, @NonNull CabModel model) {
        holder.textViewCabName.setText(model.getCabName());
        holder.textViewCabNumber.setText(model.getCabNumber());
        holder.textViewCabPerCapacity.setText(model.getCabPerCapacity());
        holder.textViewCabLaugageCapacity.setText(model.getCabLaugageCapacity());
        holder.textViewCabDriver.setText(model.getCabDriver());
        holder.textViewCabStatus.setText(model.getCabStatus());
        holder.textViewCabArea.setText(model.getCabArea());
    }

    @NonNull
    @Override
    public CabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cab, parent, false);
        return new CabHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public String getId(int position) {
        String s = getSnapshots().getSnapshot(position).get(Constants.CAB_NUMBER_KEY).toString();
        return s;
    }

    class CabHolder extends RecyclerView.ViewHolder {
        public TextView textViewCabName;
        public TextView textViewCabNumber;
        public TextView textViewCabPerCapacity;
        public TextView textViewCabLaugageCapacity;
        public TextView textViewCabDriver;
        public TextView textViewCabStatus;
        public TextView textViewCabArea;


        public CabHolder(@NonNull View itemView) {
            super(itemView);
            textViewCabName = itemView.findViewById(R.id.card_cab_txt_cab_name);
            textViewCabNumber = itemView.findViewById(R.id.card_cab_txt_cab_number);
            textViewCabPerCapacity = itemView.findViewById(R.id.card_cab_txt_cab_per_capacity);
            textViewCabLaugageCapacity = itemView.findViewById(R.id.card_cab_txt_cab_laugage_capacity);
            textViewCabDriver = itemView.findViewById(R.id.card_cab_txt_cab_driver);
            textViewCabStatus = itemView.findViewById(R.id.card_cab_txt_cab_status);
            textViewCabArea = itemView.findViewById(R.id.card_cab_txt_cab_area);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                    final String cabNumber, cabName, cabPerCapacity, cabLaugageCapacity, cabDriver, cabStatus, cabArea;
                    cabName = textViewCabName.getText().toString();
                    cabNumber = textViewCabNumber.getText().toString();
                    cabLaugageCapacity = textViewCabLaugageCapacity.getText().toString();
                    cabPerCapacity = textViewCabPerCapacity.getText().toString();
                    cabDriver = textViewCabDriver.getText().toString();
                    cabStatus = textViewCabStatus.getText().toString();
                    cabArea = textViewCabArea.getText().toString();

                    sp = v.getContext().getSharedPreferences(Constants.CAB_DETAILS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putString(Constants.CAB_NAME_KEY, cabName);
                    Ed.putString(Constants.CAB_NUMBER_KEY, cabNumber);
                    Ed.putString(Constants.CAB_PERSON_CAPACITY_KEY, cabPerCapacity);
                    Ed.putString(Constants.CAB_LAUGAGE_CAPACITY_KEY, cabLaugageCapacity);
                    Ed.putString(Constants.CAB_DRIVER_KEY, cabDriver);
                    Ed.putString(Constants.CAB_STATUS_KEY, cabStatus);
                    Ed.putString(Constants.CAB_AREA_KEY, cabArea);
                    Ed.apply();
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
