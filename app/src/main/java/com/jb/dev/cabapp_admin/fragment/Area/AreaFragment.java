package com.jb.dev.cabapp_admin.fragment.Area;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.adapter.AreaAdapter;
import com.jb.dev.cabapp_admin.fragment.Cab.CabViewModel;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;
import com.jb.dev.cabapp_admin.model.AreaModel;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AreaFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mAreaRef = db.collection(Constants.AREA_COLLECTION_REFERENCE_KEY);
    AreaAdapter mAreaAdapter;
    SharedPreferences sp;
    String s;
    TextView textViewNoData;
    ProgressBar progressBar;
    boolean isUpdate;
    String id;
    private CabViewModel cabViewModel;
    private RecyclerView recyclerViewCab;
    private String TAG = getTag();
    private String name;

    public AreaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_area, container, false);

        textViewNoData = root.findViewById(R.id.txt_no_data);
        sp = getContext().getSharedPreferences("Is_Set_C", Context.MODE_PRIVATE);
        s = sp.getString("Is_set", "");
        recyclerViewCab = root.findViewById(R.id.recycler_view_area);
        progressBar = root.findViewById(R.id.progress_circular);
        setRecyclerViewCab();

        FloatingActionButton floatingActionButton = root.findViewById(R.id.area_floating_button_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = "";
                isUpdate = false;
                isShowBottomSheet();
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
        Query query = mAreaRef;
        FirestoreRecyclerOptions<AreaModel> options = new FirestoreRecyclerOptions.Builder<AreaModel>()
                .setQuery(query, AreaModel.class)
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
        mAreaAdapter = new AreaAdapter(options);
        recyclerViewCab.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewCab.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCab.setAdapter(mAreaAdapter);
        mAreaAdapter.notifyDataSetChanged();
        mAreaAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                    s = mAreaAdapter.getId(viewHolder.getAdapterPosition());
                    builder.setMessage("Are you sure to delete?");    //set message
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            mAreaAdapter.deleteItem(viewHolder.getAdapterPosition());
                            Snackbar.make(getView(), getString(R.string.area_deleted_successfully), BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAreaAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).show();  //show alert dialog
                }
            }
        }).attachToRecyclerView(recyclerViewCab);
        mAreaAdapter.setOnItemClickListener(new AreaAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AreaModel cabModel = documentSnapshot.toObject(AreaModel.class);
                id = documentSnapshot.getId();
                name = mAreaAdapter.getItem(position).getAreaName();
                isUpdate = true;
                isShowBottomSheet();
            }
        });
    }

    void isShowBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.add_area_bottom_sheet, null);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        final TextInputEditText siteName, siteContactNumber;
        siteName = view.findViewById(R.id.add_site_name);
        Button btnAdd = view.findViewById(R.id.btn_add_site);
        if (isUpdate) {
            siteName.setText(name);
            btnAdd.setText("Update");
            toolbar.setTitle("Update Area");
        } else {
            btnAdd.setText("Add");
            toolbar.setTitle("Add Area");
        }
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        view.findViewById(R.id.action_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = siteName.getText().toString();
                if (isUpdate) {
                    if (!Helper.isValidText(name)) {
                        siteName.setError("Enter Area");
                    } else {
                        DocumentReference documentReference = db.collection(Constants.AREA_COLLECTION_REFERENCE_KEY).document(id);
                        documentReference.update(Constants.AREA_NAME, name)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mAreaAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), getString(R.string.txt_success_update), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } else {
                    if (!Helper.isValidText(name)) {
                        siteName.setError("Enter Area");
                    } else {
                        Map<String, Object> newSite = new HashMap<>();
                        newSite.put(Constants.AREA_NAME, name);
                        db.collection(Constants.AREA_COLLECTION_REFERENCE_KEY).document().set(newSite).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mAreaAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Error:", e.getMessage());
                            }
                        });
                    }
                }

            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAreaAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAreaAdapter.stopListening();
    }
}