package com.jb.dev.cabapp_admin.fragment.booking;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.adapter.DriverBookingAdapter;
import com.jb.dev.cabapp_admin.adapter.DriverBookingHistoryAdapter;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.model.BookingModel;
import com.jb.dev.cabapp_admin.model.DriverModel;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class BookingFragment extends Fragment {

    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerViewBooking, recyclerViewDriver;
    FirebaseFirestore db = getInstance();
    TextView textViewNoData, textViewNoBooking;
    ProgressBar progressBar, progressBarBooking;
    private ProgressDialog progressDialog;
    private String driver;
    private DriverBookingAdapter mDriverFirebaseAdapter;
    DriverBookingHistoryAdapter mDriverBookingHistoryAdapter;
    private CollectionReference mDriverRef = db.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY);
    private CollectionReference mBookingRef = db.collection(Constants.BOOKING_COLLECTION_REFERENCE_KEY);

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookingViewModel =
                ViewModelProviders.of(this).get(BookingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        recyclerViewBooking = root.findViewById(R.id.booking_recycler_view);
        recyclerViewDriver = root.findViewById(R.id.driver_booking_recycler_view);
        textViewNoData = root.findViewById(R.id.txt_no_data);
        textViewNoBooking = root.findViewById(R.id.txt_no_data_booking);
        progressBar = root.findViewById(R.id.progress_circular);
        progressBarBooking = root.findViewById(R.id.progress_circular_rl_root);
//        isSetDriver();
        setRecyclerViewDriver();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setRecyclerViewDriver() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = mDriverRef;
        FirestoreRecyclerOptions<DriverModel> options = new FirestoreRecyclerOptions.Builder<DriverModel>()
                .setQuery(query, DriverModel.class)
                .build();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                } else {
                    textViewNoData.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        mDriverFirebaseAdapter = new DriverBookingAdapter(options);
        recyclerViewDriver.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDriver.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDriver.setAdapter(mDriverFirebaseAdapter);
        mDriverFirebaseAdapter.notifyDataSetChanged();
        mDriverFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.e("onItemRangeInserted::", positionStart + "");
                if (positionStart == 0) {
                    textViewNoData.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (positionStart == 0) {
                    textViewNoData.setVisibility(View.VISIBLE);
                }
            }
        });
        mDriverFirebaseAdapter.setOnItemClickListener(new DriverBookingAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String name) {
                driver = name;
                progressBarBooking.setVisibility(View.VISIBLE);
                Query query = mBookingRef.whereEqualTo(Constants.BOOKING_CAB_DRIVER_KEY, driver);
                FirestoreRecyclerOptions<BookingModel> options = new FirestoreRecyclerOptions.Builder<BookingModel>()
                        .setQuery(query, BookingModel.class)
                        .build();
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            textViewNoBooking.setVisibility(View.VISIBLE);
                        } else {
                            textViewNoBooking.setVisibility(View.INVISIBLE);
                        }
                        progressBarBooking.setVisibility(View.GONE);
                    }
                });
                mDriverBookingHistoryAdapter = new DriverBookingHistoryAdapter(options);
                recyclerViewBooking.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewBooking.setItemAnimator(new DefaultItemAnimator());
                recyclerViewBooking.setAdapter(mDriverBookingHistoryAdapter);
                mDriverBookingHistoryAdapter.notifyDataSetChanged();
                mDriverBookingHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {

                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        Log.e("onItemRangeInserted::", positionStart + "");
                        if (positionStart == 0) {
                            textViewNoBooking.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        if (positionStart == 0) {
                            textViewNoBooking.setVisibility(View.VISIBLE);
                        }
                    }
                });
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