package com.jb.dev.cabapp_admin.fragment.Cab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.activites.AddCabActivity;
import com.jb.dev.cabapp_admin.activites.UpdateCabActivity;
import com.jb.dev.cabapp_admin.adapter.CabFirebaseAdapter;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;
import com.jb.dev.cabapp_admin.model.CabModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CabFragment extends Fragment {

    private CabViewModel cabViewModel;
    private RecyclerView recyclerViewCab;
    private ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mCabRef = db.collection("Cab_Data");
    CabFirebaseAdapter mCabFirebaseAdapter;
    SharedPreferences sp;
    String s;
    private String TAG = getTag();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cabViewModel = ViewModelProviders.of(this).get(CabViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cab, container, false);

        sp = getContext().getSharedPreferences("Is_Set_C", Context.MODE_PRIVATE);
        s = sp.getString("Is_set", "");
        Log.e("s", s);
        recyclerViewCab = root.findViewById(R.id.recycler_view_cab);
        progressDialog = new ProgressDialog(getContext());
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
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        Query query = mCabRef;
        FirestoreRecyclerOptions<CabModel> options = new FirestoreRecyclerOptions.Builder<CabModel>()
                .setQuery(query, CabModel.class)
                .build();

        mCabFirebaseAdapter = new CabFirebaseAdapter(options);
        recyclerViewCab.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewCab.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCab.setAdapter(mCabFirebaseAdapter);
        progressDialog.dismiss();
        mCabFirebaseAdapter.notifyDataSetChanged();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //alert for confirm to delete
                if (direction == ItemTouchHelper.RIGHT) {    //if swipe right
                    s = mCabFirebaseAdapter.getId(viewHolder.getAdapterPosition());
                    Log.e("s", s);
                    builder.setMessage("Are you sure to delete?");    //set message
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            Query q = mCabRef.whereEqualTo(Constants.CAB_NUMBER_KEY, s).whereEqualTo(Constants.CAB_STATUS_KEY, "Available");
                            q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        mCabFirebaseAdapter.deleteItem(viewHolder.getAdapterPosition());
                                    } else {
                                        Snackbar.make(getView(), R.string.booked, BaseTransientBottomBar.LENGTH_SHORT).show();
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
                intent.putExtra("id", id);
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