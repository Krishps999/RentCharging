package com.pradhyups.rentcharging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfPlaces extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private PlaceListAdapter mPlLiAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private List<UploadData> mUploads;

    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

        mProgressBar = findViewById(R.id.Progress_Circular);
        mRecyclerView = findViewById(R.id.Recycler_For_ListItems);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserId = mFirebaseAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users/" + mUserId );

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    UploadData mUpload = postSnapshot.getValue(UploadData.class);
                    mUploads.add(mUpload);
                }

                mPlLiAdapter = new PlaceListAdapter(ListOfPlaces.this, mUploads);
                mRecyclerView.setAdapter(mPlLiAdapter);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListOfPlaces.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}