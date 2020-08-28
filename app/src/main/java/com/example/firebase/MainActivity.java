package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_VIDEO = 1;
    VideoView  mvideoView;
    Button mbutton;
    ProgressBar mprogressBar;
    EditText meditText;
    private Uri videouri;
    MediaController mmediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Member member;
    UploadTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        member = new Member();
        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("Video");

        
        mvideoView = (VideoView) findViewById(R.id.etVideoView);
        mbutton = (Button) findViewById(R.id.btnUpload);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        meditText = (EditText) findViewById(R.id.etVideoname);
        mmediaController = new MediaController(this);
        mvideoView.setMediaController(mmediaController);
        mvideoView.start();
        
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadView();
            }
        });
    }

    private void UploadView() {
        String videoName = meditText.getText().toString();
        String search = meditText.getText().toString().toLowerCase();
        if (videouri != null || !TextUtils.isEmpty(videoName)){
            mprogressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." +getExtension(videouri));
            uploadTask = reference.putFile(videouri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                mprogressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "data saved", Toast.LENGTH_SHORT).show();
                                member.setName(videoName);
                                member.setVideoUrl(downloadUrl.toString());
                                member.setSearch(search);
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(member);
                            }else {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(this, "ALl fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK || data != null || data.getData() != null){
            videouri = data.getData();
            mvideoView.setVideoURI(videouri);
        }
    }

    public void chooseVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);
    }

    private String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    
    public void showVideo(View view) {

        Intent intent = new Intent(MainActivity.this, Showvideo.class);
        startActivity(intent);

    }
}