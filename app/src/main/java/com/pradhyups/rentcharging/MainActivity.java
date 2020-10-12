package com.pradhyups.rentcharging;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;


public class MainActivity extends Activity {

    private static final int IMAGE_PICKER = 1; //constant for image pick request

    private EditText mName;
    private EditText mAddress;
    private EditText mContactInfo;
    private Spinner mSpinner;
    private Button mSave;
    private Button mShow;
    private Button mLogout;
    private Button mStationImage;
    private Uri mUri;
    private String mConnectorType = "";
    private String userId;

    private String mNameString;
    private String mAddressString;
    private String mContactInfoString;
    private int mLocationvalue;
    private String mConnectorTypeString;

    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName = findViewById(R.id.Station_name);
        mAddress = findViewById(R.id.Station_Address);
        mContactInfo = findViewById(R.id.Contact_Info);
        mSpinner = findViewById(R.id.Connector_Type);
        mSave = findViewById(R.id.Save_Button);
        mLogout = findViewById(R.id.LogOut);
        mShow = findViewById(R.id.Search_Places);
        mStationImage = findViewById(R.id.Pick_Station_Image);

        mStorageReference = FirebaseStorage.getInstance().getReference("station Images");
        mFirebaseDatabase = FirebaseDatabase.getInstance(); // instance is the root node
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users"); // reference is the sub nodes inside a root node
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Spinner operation for connector type
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.connector_Types));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Select Connector Types"))
                {
                    //do nothing here
                }
                else
                {
                    //on selecting an item from the spinner
                    mConnectorType = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing here
            }
        });

        mStationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker();
            }
        });

        //when the save button is clicked
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameString = mName.getText().toString();
                mAddressString = mAddress.getText().toString();
                mContactInfoString = mContactInfo.getText().toString();
                mConnectorTypeString = mConnectorType;

                if (mAddressString.equals("Kengeri")) {

                    mLocationvalue = 234567; //location value is hardcoded, needs to be changed
                }
                uploadStationInfo();
            }
        });

        mShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceListActivity();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent mIntent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(mIntent);
                finish();
            }
        });

    }

   private void openPlaceListActivity() {
        Intent mIntent = new Intent(MainActivity.this, ListOfPlaces.class);
        startActivity(mIntent);
   }

    //Image pick and Loading of image once picked
    private void imagePicker() {
        Intent mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(mIntent, IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mUri = data.getData();
            Glide.with(this).load(mUri); //Picasso is used to load the image.

        }
    }
    //to get file extension
    private String getFileExtension(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        MimeTypeMap mMimeTypeMap = MimeTypeMap.getSingleton();
        return mMimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(uri));
    }

    //uploading Station info and image
    private void uploadStationInfo() {

        if (mUri != null) {

            StorageReference storageReference = mStorageReference.child(System.currentTimeMillis() //Time in millisecond is used for the name of
                    + "." + getFileExtension(mUri));                                                       //image file, so that no name will be repeated

            storageReference.putFile(mUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    UploadData uploadData = new UploadData(mNameString, mAddressString, mContactInfoString
                                            , mConnectorTypeString, mLocationvalue, downloadUri);
                                    userId = mFirebaseAuth.getCurrentUser().getUid();
                                    mDatabaseReference.child(userId + "/Station Info").setValue(uploadData); //using this database will be created with user id mNameString
                                    Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(this,"No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
