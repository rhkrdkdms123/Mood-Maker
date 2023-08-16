package com.example.iothome;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FrameFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef; // 이미지 레퍼런스
    private DatabaseReference frameDataRef;
    private FirebaseDatabase database;
    private ImageView uploadImage;
    Button uploadButton;
    Button turnOffFrameBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frame, container, false);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // MKupload 경로에 레퍼런스 생성
        imageRef = storageRef.child("MKupload");

        uploadButton = view.findViewById(R.id.uploadButton);
        uploadImage = view.findViewById(R.id.uploadImage);

        turnOffFrameBtn=view.findViewById(R.id.frameTurnOffBtn);

        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Firebase 데이터베이스 참조
        database = FirebaseDatabase.getInstance();
        frameDataRef = database.getReference("frameData");

        if (frameDataRef != null) {
            Log.d("FrameFragment", "Firebase Connected: Yes");
        } else {
            Log.d("FrameFragment", "Firebase Connected: No");
        }

        turnOffFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스의 isMusicPlaying 값 가져오기
                frameDataRef.child("isFrameTurnedOn").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            Boolean isFrameTurnedOn = snapshot.getValue(Boolean.class);
                            if (isFrameTurnedOn) {
                                // isFrameTurnedOn가 true인 경우
                                // turnOffFrame 값을 true로 업데이트
                                frameDataRef.child("turnOffFrame").setValue(true)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                frameDataRef.child("isFrameTurnedOn").setValue(false);
                                                frameDataRef.child("imageUrl").setValue("");
                                                uploadImage.setImageResource(R.drawable.icon_art);
                                            }
                                        });
                            } else {
                                // isFrameTurnedOn가 false인 경우
                                // 이미 액자가 꺼져있음을 알리는 토스트 띄우기
                                Toast.makeText(requireContext(), "이미 액자가 꺼져 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // 기존에 저장되어있던 이미지 삭제
            deleteExistingImage();

            // 새로운 이미지 업로드
            uploadImage(imageUri);
        }
    }

    private void deleteExistingImage() {
        imageRef.delete() // 기존 이미지 삭제
                .addOnSuccessListener(aVoid -> {
                    Log.e("FrameFragment","기존 이미지 삭제 성공");
                })
                .addOnFailureListener(exception -> {
                    Log.e("FrameFragment","기존 이미지 삭제 실패");
                });
    }

    private void uploadImage(Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("MKupload/" + imageUri.getLastPathSegment());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                        // 파일 업로드 성공
                        imageRef.getDownloadUrl().addOnSuccessListener(url -> {
                            // 업로드한 이미지의 다운로드 URL을 Firebase에 저장
                            frameDataRef.child("imageUrl").setValue(url.toString());
                            Picasso.get().load(url).into(uploadImage);
                            frameDataRef.child("isFrameTurnedOn").setValue(true);
                            frameDataRef.child("turnOffFrame").setValue(false);
                        });
                })
                .addOnFailureListener(exception -> {
                    // 파일 업로드 실패
                    showUploadFailureDialog();
                });
    }


    private void showUploadFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("오류")
                .setMessage("파일 업로드에 실패했습니다.")
                .setPositiveButton("확인", (dialog, which) -> {
                    dialog.dismiss(); // 다이얼로그 닫기
                })
                .show();
    }
}
