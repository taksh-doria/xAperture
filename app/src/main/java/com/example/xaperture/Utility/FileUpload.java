package com.example.xaperture.Utility;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class FileUpload
{
    Intent data;
    String category;
    String description;
    String mime;
    Timestamp timestamp= com.google.firebase.Timestamp.now();

    public FileUpload(Intent data, String category, String description,String mime) {
        this.data = data;
        this.category = category;
        this.description = description;
        this.mime=mime;
    }
    public boolean registerImage()
    {
        final boolean[] status = {false};
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String img_name=generateRandom();
        final String orignal=img_name+"."+mime;
        String thumbnail=img_name+"_2560x1440"+"."+mime;
        //creating hashmap to add data collection to it
        System.out.println("generated imagenames: "+orignal+" thumbnail: "+thumbnail);
        final HashMap<String,Object> dataentry=new HashMap<>();
        dataentry.put("category",category);
        dataentry.put("date",timestamp);
        dataentry.put("orignal_img",orignal);
        dataentry.put("thumbnail_img",thumbnail);
        dataentry.put("email",user.getEmail());
        dataentry.put("description",description);
        //creating storage refrence to add img data to database
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("UserImg").document().set(dataentry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Data Entry SuccesFull");
                status[0] =uploadImage(orignal,data.getData(),category);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error Occured: "+e);
            }
        });
        System.out.println("return value from register Image:"+status[0]);
        return status[0];
    }
    public String generateRandom()
    {
        String alphaNum="ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"1234567890"+"abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb=new StringBuilder(20);
        for (int i=0;i<20;i++)
        {
            int index=(int)(alphaNum.length()*Math.random());
            sb.append(alphaNum.charAt(index));
        }
        return sb.toString();
    }
    public boolean uploadImage(String  imagename, Uri uri,String category)
    {
        final boolean[] status = {false};
        FirebaseStorage storage=FirebaseStorage.getInstance();
        UploadTask uploadTask = storage.getReference("Images/" + category).child(imagename).putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Image uploaded");
                status[0] =true;
            }
        });
        System.out.println("return value from uploadImage: "+status[0]);
        return status[0];
    }
}