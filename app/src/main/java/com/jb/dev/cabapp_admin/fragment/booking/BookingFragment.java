package com.jb.dev.cabapp_admin.fragment.booking;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.adapter.DriverBookingAdapter;
import com.jb.dev.cabapp_admin.adapter.DriverBookingHistoryAdapter;
import com.jb.dev.cabapp_admin.model.BookingModel;
import com.jb.dev.cabapp_admin.model.DriverModel;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class BookingFragment extends Fragment{

    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerViewBooking, recyclerViewDriver;
    FirebaseFirestore db = getInstance();
    private CollectionReference mDriverRef = db.collection("Driver_Data");
    private CollectionReference mBookingRef = db.collection("Booking_Data");
    private ProgressDialog progressDialog;
    private String driver;
    private DriverBookingAdapter mDriverFirebaseAdapter;
    DriverBookingHistoryAdapter mDriverBookingHistoryAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookingViewModel =
                ViewModelProviders.of(this).get(BookingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        recyclerViewBooking = root.findViewById(R.id.booking_recycler_view);
        recyclerViewDriver = root.findViewById(R.id.driver_booking_recycler_view);
        progressDialog = new ProgressDialog(getContext());
//        isSetDriver();
        setRecyclerViewDriver();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setRecyclerViewDriver() {
        Query query = mDriverRef;
        FirestoreRecyclerOptions<DriverModel> options = new FirestoreRecyclerOptions.Builder<DriverModel>()
                .setQuery(query, DriverModel.class)
                .build();

        mDriverFirebaseAdapter = new DriverBookingAdapter(options);
        recyclerViewDriver.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDriver.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDriver.setAdapter(mDriverFirebaseAdapter);
        mDriverFirebaseAdapter.notifyDataSetChanged();
        mDriverFirebaseAdapter.setOnItemClickListener(new DriverBookingAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String name) {
                driver = name;
                Query query = mBookingRef.whereEqualTo("cab_driver", driver);
                FirestoreRecyclerOptions<BookingModel> options = new FirestoreRecyclerOptions.Builder<BookingModel>()
                        .setQuery(query, BookingModel.class)
                        .build();

                mDriverBookingHistoryAdapter = new DriverBookingHistoryAdapter(options);
                recyclerViewBooking.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewBooking.setItemAnimator(new DefaultItemAnimator());
                recyclerViewBooking.setAdapter(mDriverBookingHistoryAdapter);
                mDriverBookingHistoryAdapter.notifyDataSetChanged();
                mDriverBookingHistoryAdapter.startListening();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mDriverFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDriverFirebaseAdapter.stopListening();
    }
}