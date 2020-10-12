package com.pradhyups.rentcharging;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {

    private static final int IMAGE_PICKER = 1; //constant for image pick request

    private Uri mUri;
    private EditText mUserName;
    private EditText mPassword;
    private Button mImagePicker;
    private Button mNewRegister;
    private Button mLogin;
    private String mImageName = "Id proof";
    private String mUserNameString;
    private String mPasswordString;
    private ImageView mImageViewId;
    private String userId;

    protected static final String USER_NAME = "com.pradhyups.rentcharging.user_name";

    private StorageReference mStorageReferenceForImage;
    private DatabaseReference mDataBaseReference;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFireBaseFireStore;
    private FirebaseFunctions mFireBaseFunctions;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.registration_page);

            mImagePicker =findViewById(R.id.Image_Picker);
            mNewRegister = findViewById(R.id.New_Register);
            mLogin = findViewById(R.id.Login_Page);
            mUserName = findViewById(R.id.UserName);
            mPassword = findViewById(R.id.New_Password);
            mImageViewId = findViewById(R.id.Show_Id);

            mStorageReferenceForImage = FirebaseStorage.getInstance().getReference("images");
            mDataBaseReference = FirebaseDatabase.getInstance().getReference("Users");
            mFireBaseFireStore = FirebaseFirestore.getInstance();
            mFireBaseFunctions = FirebaseFunctions.getInstance();

            mFireBaseAuth = FirebaseAuth.getInstance();

            //Choosing the id proof image
            mImagePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePicker();
                }
            });

            //after registration is successful open the next intent
            mNewRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUserNameString = mUserName.getText().toString();
                    mPasswordString = mPassword.getText().toString();
                    if (!(mUserNameString.trim().isEmpty()) && mUri != null) {
                        uploadUserInfo(); //user info will be uploaded here
                        Intent mIntent = new Intent(RegistrationPage.this, MainActivity.class);
                        mIntent.putExtra(USER_NAME, mUserNameString);
                        startActivity(mIntent);
                    }
                    else if (mUserNameString.trim().isEmpty()) {
                        Toast.makeText(RegistrationPage.this, "Please Enter the User Name", Toast.LENGTH_LONG).show();
                    }
                    else if (mUri == null) {
                        Toast.makeText(RegistrationPage.this, "Please Select the image", Toast.LENGTH_LONG).show();
                    }
                }
            });

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(RegistrationPage.this, LoginPage.class);
                    startActivity(mIntent);
                }
            });
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
                Glide.with(this).load(mUri).into(mImageViewId); //Picasso is used to load the image.

            }
        }

        //to get file extension
        private String getFileExtension(Uri uri) {
            ContentResolver mContentResolver = getContentResolver();
            MimeTypeMap mMimeTypeMap = MimeTypeMap.getSingleton();
            return mMimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(uri));
        }

        //uploading user info and image
        private void uploadUserInfo() {

            mFireBaseAuth.createUserWithEmailAndPassword(mUserNameString, mPasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationPage.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegistrationPage.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (mUri != null) {

                StorageReference storageReference = mStorageReferenceForImage.child(System.currentTimeMillis() //Time in millisecond is used for the name of
                        + "." + getFileExtension(mUri));                                                       //image file, so that no name will be repeated

                storageReference.putFile(mUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                UploadData mUpload = new UploadData(mUserNameString,
                                        taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                userId = mFireBaseAuth.getCurrentUser().getUid();
                                mDataBaseReference.child(userId + "/User Info").setValue(mUpload);
                                Toast.makeText(RegistrationPage.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegistrationPage.this, "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Toast.makeText(this,"No image selected", Toast.LENGTH_SHORT).show();
            }
        }
}
