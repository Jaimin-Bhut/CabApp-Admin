package com.jb.dev.cabapp_admin.fragment.Driver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.activites.AddDriverActivity;
import com.jb.dev.cabapp_admin.activites.UpdateDriverActivity;
import com.jb.dev.cabapp_admin.adapter.DriverFirebaseAdapter;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.model.DriverModel;

import java.util.ArrayList;


public class DriverFragment extends Fragment {

    private RecyclerView recyclerViewDriver;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mDriverRef = db.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY);
    private CollectionReference mCabRef = db.collection(Constants.CAB_COLLECTION_REFERENCE_KEY);
    private DriverFirebaseAdapter mDriverFirebaseAdapter;
    private DriverViewModel driverViewModel;
    private String TAG = getTag();
    int count = 0;
    String cabStatus, cabDriver;
    TextView textViewNoData;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        driverViewModel = ViewModelProviders.of(this).get(DriverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_driver, container, false);
        recyclerViewDriver = root.findViewById(R.id.recycler_view_driver);
        textViewNoData = root.findViewById(R.id.txt_no_data);
        progressBar = root.findViewById(R.id.progress_circular);
        setRecyclerViewDriver();

        FloatingActionButton floatingActionButton = root.findViewById(R.id.driver_floating_button_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddDriverActivity.class);
                startActivityForResult(intent, Constants.DRIVER_REFRESH_RESULT_CODE);

            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + "," + resultCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setRecyclerViewDriver() {
        progressBar.setVisibility(View.VISIBLE);
        final Query query = mDriverRef;
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

        FirestoreRecyclerOptions<DriverModel> options = new FirestoreRecyclerOptions.Builder<DriverModel>()
                .setQuery(query, DriverModel.class)
                .build();
        mDriverFirebaseAdapter = new DriverFirebaseAdapter(options);
        recyclerViewDriver.setLayoutManager(new LinearLayoutManager(getContext()));
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //alert for confirm to delete
//                viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    cabDriver = mDriverFirebaseAdapter.getId(viewHolder.getAdapterPosition());
                    builder.setMessage("Are you sure to delete?");//set message
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            Query q = mCabRef.whereEqualTo(Constants.CAB_DRIVER_KEY, cabDriver);
                            final ArrayList list = new ArrayList();
                            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            String id = snapshot.getId();
                                            cabStatus = snapshot.get(Constants.CAB_STATUS_KEY).toString();
                                            count++;
                                            if (cabStatus.equals(Constants.BOOKED)) {
                                                Snackbar.make(getView(), R.string.this_driver_is_currently_booked, BaseTransientBottomBar.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                return;
                                            } else {
                                                list.add(id);
                                            }
                                        }
                                        if (count == list.size()) {
                                            for (int i = 0; i < list.size(); i++) {
                                                mCabRef.document(String.valueOf(list.get(i))).delete();
                                            }
                                            mDriverFirebaseAdapter.deleteItem(viewHolder.getAdapterPosition());
                                            Snackbar.make(getView(), getString(R.string.driver_deleted), Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            mDriverFirebaseAdapter.deleteItem(viewHolder.getAdapterPosition());
                                            Snackbar.make(getView(), getString(R.string.driver_deleted), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDriverFirebaseAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).show();  //show alert dialog
                }
            }
        }).attachToRecyclerView(recyclerViewDriver);
        mDriverFirebaseAdapter.setOnItemClickListener(new DriverFirebaseAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                DriverModel driverModel = documentSnapshot.toObject(DriverModel.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(getContext(), UpdateDriverActivity.class);
                intent.putExtra(Constants.DRIVER_ID, id);
                startActivity(intent);
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