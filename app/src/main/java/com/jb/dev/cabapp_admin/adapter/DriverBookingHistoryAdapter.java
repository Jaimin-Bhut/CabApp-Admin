package com.jb.dev.cabapp_admin.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.model.BookingModel;

public class DriverBookingHistoryAdapter extends FirestoreRecyclerAdapter<BookingModel, DriverBookingHistoryAdapter.DriverBookingHistoryHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     *
     * @param options
     */
    public DriverBookingHistoryAdapter(@NonNull FirestoreRecyclerOptions<BookingModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DriverBookingHistoryHolder holder, int position, @NonNull BookingModel model) {
        holder.textViewFare.setText(String.valueOf(model.getPrice()));
        holder.textViewFrom.setText(model.getCurrent_location());
        holder.textViewUserPhone.setText(model.getUser_phone_number());
        holder.textViewUserName.setText(model.getuser_name());
        holder.textViewDate.setText(model.getdate());
        holder.textViewTo.setText(model.getDestination_location());
    }

    @NonNull
    @Override
    public DriverBookingHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_driver_booking, parent, false);
        return new DriverBookingHistoryHolder(view);
    }

    public class DriverBookingHistoryHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewUserName, textViewUserPhone, textViewFare, textViewFrom, textViewTo;

        public DriverBookingHistoryHolder(@NonNull View v) {
            super(v);
            textViewDate = v.findViewById(R.id.card_driver_booking_date);
            textViewUserName = v.findViewById(R.id.card_driver_booking_user_name);
            textViewUserPhone = v.findViewById(R.id.card_driver_booking_user_phone);
            textViewFrom = v.findViewById(R.id.card_driver_booking_from);
            textViewTo = v.findViewById(R.id.card_driver_booking_to);
            textViewFare = v.findViewById(R.id.card_driver_booking_fare);
        }
    }
}
