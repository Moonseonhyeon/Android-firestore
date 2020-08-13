package com.linda.firestoreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_Activity";

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();//양방향

        User user = User.builder()
                .id(1)
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                // .createDate(Timestamp.now())
                .build();

        db.collection("user").document("1")
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: insert 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: 인서트 실패"+e.getMessage());
                    }
                });

       // save();
       // findAll();
        findById();
        //mStream();
    }

    //push!!!!!!!!!!! 데이터베이스에 빨대 꼽은 거에요!
    private void mStream() {
        final DocumentReference docRef = db.collection("user").document("1");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d(TAG, "onEvent: 데이터 변경 됨");
                User user = documentSnapshot.toObject(User.class);
                Log.d(TAG, "onEvent: user : " + user.getPassword());

                //이것을 라이브데이터한테 주면 변경감지해서 UI그려준다.
                //화면마다 ViewModel이 있고 그것마다 LiveData
            }
        });
    }
        
        
        private void findById(){
            db.document("user/1")
           // db.collection("user").document("1")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                User user = document.toObject(User.class);
                                Log.d(TAG, "onComplete: "+user);
                            }else{
                                Log.d(TAG, "onComplete: 실패"+ task.getException());
                            }
                        }
                    });
        }



    private  void save(){
        User user = User.builder()
                .id(2)
                .username("love")
                .password("1234")
                .email("love@nate.com")
                // .createDate(Timestamp.now())
                .build();
    }


    private void findAll(){
        db.collection("user").orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //QuerySnapshot : select 할 때 형상을 가져옴
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: task"+ task.getResult().getDocuments());
                       for(QueryDocumentSnapshot document : task.getResult()){
                           Log.d(TAG, "onComplete: document : "+ document.getId()+"=>"+document.getData());
                           Log.d(TAG, "onComplete: email : "+document.getData().get("email"));
                           //뷰모델 레퍼런스 접근.data.setValue(컬렉션);
                       }


                        }else{
                            Log.d(TAG, "onComplete: task실패 :"+task.getException());
                        }
                    }
                });




    }
}