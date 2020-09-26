package com.jb.dev.cabapp_admin.fragment.Cab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.activites.AddCabActivity;
import com.jb.dev.cabapp_admin.activites.UpdateCabActivity;
import com.jb.dev.cabapp_admin.adapter.CabFirebaseAdapter;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.model.CabModel;

public class CabFragment extends Fragment {

    private CabViewModel cabViewModel;
    private RecyclerView recyclerViewCab;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mCabRef = db.collection(Constants.CAB_COLLECTION_REFERENCE_KEY);
    CabFirebaseAdapter mCabFirebaseAdapter;
    SharedPreferences sp;
    String s;
    private String TAG = getTag();
    TextView textViewNoData;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cabViewModel = ViewModelProviders.of(this).get(CabViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cab, container, false);

        textViewNoData = root.findViewById(R.id.txt_no_data);
        sp = getContext().getSharedPreferences("Is_Set_C", Context.MODE_PRIVATE);
        s = sp.getString("Is_set", "");
        recyclerViewCab = root.findViewById(R.id.recycler_view_cab);
        progressBar = root.findViewById(R.id.progress_circular);
        setRecyclerViewCab();

        FloatingActionButton floatingActionButton = root.findViewById(R.id.cab_floating_button_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddCabActivity.class);
                startActivityForResult(intent, Constants.CAB_REFRESH_RESULT_CODE);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + "," + resultCode);

    }

    public void setRecyclerViewCab() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = mCabRef;
        FirestoreRecyclerOptions<CabModel> options = new FirestoreRecyclerOptions.Builder<CabModel>()
                .setQuery(query, CabModel.class)
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
        mCabFirebaseAdapter = new CabFirebaseAdapter(options);
        recyclerViewCab.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewCab.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCab.setAdapter(mCabFirebaseAdapter);
        mCabFirebaseAdapter.notifyDataSetChanged();
        mCabFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //alert for confirm to delete
                if (direction == ItemTouchHelper.LEFT) {    //if swipe left
                    s = mCabFirebaseAdapter.getId(viewHolder.getAdapterPosition());
                    builder.setMessage("Are you sure to delete?");    //set message
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            Query q = mCabRef.whereEqualTo(Constants.CAB_NUMBER_KEY, s).whereEqualTo(Constants.CAB_STATUS_KEY, Constants.AVAILABLE);
                            q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        mCabFirebaseAdapter.deleteItem(viewHolder.getAdapterPosition());
                                        Snackbar.make(getView(), getString(R.string.cab_deleted), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(getView(), R.string.this_cab_is_currently_booked, BaseTransientBottomBar.LENGTH_SHORT).show();
                                        mCabFirebaseAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCabFirebaseAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).show();  //show alert dialog
                }
            }
        }).attachToRecyclerView(recyclerViewCab);
        mCabFirebaseAdapter.setOnItemClickListener(new CabFirebaseAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                CabModel cabModel = documentSnapshot.toObject(CabModel.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(getContext(), UpdateCabActivity.class);
                intent.putExtra(Constants.CAB_ID, id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mCabFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCabFirebaseAdapter.stopListening();
    }
}