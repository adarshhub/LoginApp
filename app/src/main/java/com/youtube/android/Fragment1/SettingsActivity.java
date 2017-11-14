package com.youtube.android.Fragment1;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView status,name;
    Button changeimage,changestatus;
    Uri uri;
    DatabaseReference ref;
    FirebaseUser currentUser;
    ImageView profilepicture;
    StorageReference mStorageRef;
    Toolbar toolbar;
    int extra;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        extra = getIntent().getIntExtra("Camera",0);
        if (extra== 123)
        {
            Intent imageintent,chosser;
            imageintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageintent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(getFile()));
            imageintent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            chosser=Intent.createChooser(imageintent,"Choose app");
            startActivityForResult(chosser,0);
        }
        toolbar= (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SETTINGS");
        status= (TextView) findViewById(R.id.setting_status);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name = (TextView) findViewById(R.id.setting_name);
        changeimage = (Button) findViewById(R.id.change_image);
        changestatus = (Button) findViewById(R.id.change_status);
        profilepicture = (ImageView) findViewById(R.id.profile_picture);
        //changeimage.setOnClickListener(this);
        changestatus.setOnClickListener(this);
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (currentUser!= null) {
            ref = database.getReference().child("Users").child(currentUser.getUid());
            ref.keepSynced(true);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    status.setText(dataSnapshot.child("status").getValue().toString());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    if(!dataSnapshot.child("image").getValue().toString().equals("default"))
                    {
                        setpicture();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });

        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.change_status)
        {

            Intent statusintent = new Intent(this,StatusActivity.class);
            startActivity(statusintent);
        }
    }

    private File getFile()
    {
        File folder = new File("sdcard/Camera");
        if (!folder.exists())
            folder.mkdir();
        return new File(folder,"pisLoginApp.jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode ==0 && resultCode==RESULT_OK)
        {
            uri = data.getData();
            cropimage();

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            final String uid = currentUser.getUid();
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    File compressedImageFile = new Compressor(this).compressToFile(new File(resultUri.getPath()));
                    StorageReference thumbRef = mStorageRef.child("thumbnails").child(uid+".jpg");
                    thumbRef.putFile(Uri.fromFile(compressedImageFile));
                    ref.child("thumbnail").setValue(uid+".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StorageReference riversRef = mStorageRef.child("images").child(uid+".jpg");
                riversRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.child("image").setValue(uid+".jpg");
                            Toast.makeText(SettingsActivity.this, "upload succesfull", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(SettingsActivity.this,"upload failed",Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
                Snackbar.make(findViewById(R.id.setting_parent_layout),"Internet Problem",Snackbar.LENGTH_LONG)
                        .setAction("Retry",null).show();
            }
        }
    }


    public void showpopmenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.context_image_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.context_camera)
                {
                    Intent imageintent,chosser;
                    imageintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"picLoginApp.jpg");
                    Uri tempuri = Uri.fromFile(imageFile);
                    imageintent.putExtra(MediaStore.EXTRA_OUTPUT,tempuri);
                    chosser=Intent.createChooser(imageintent,"Choose app");
                    startActivityForResult(chosser,0);

                }
                else if (item.getItemId() == R.id.context_gallery)
                {
                    Intent imageintent,chosser;
                    imageintent = new Intent(Intent.ACTION_GET_CONTENT);
                    imageintent.setType("image/*");
                    chosser=Intent.createChooser(imageintent,"Choose app");
                    startActivityForResult(chosser,0);

                }
                return true;
            }
        });
    }


    public void cropimage()
    {

        CropImage.activity(uri).setAspectRatio(1,1).setRequestedSize(200,200)
                .start(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setpicture()
    {
        final StorageReference mRef = mStorageRef.child("images").child(currentUser.getUid()+".jpg");
        ref.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.getValue().equals("default"))
                {
                    Glide.with(getBaseContext())
                            .using(new FirebaseImageLoader())
                            .load(mRef)
                            .placeholder(R.drawable.profiledefaulmale)
                            .into(profilepicture);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
