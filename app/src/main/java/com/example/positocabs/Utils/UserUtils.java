package com.example.positocabs.Utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.positocabs.Models.TokenModel;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Services.MessagingService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserUtils {

    public static void updateToken(Context context, String token) {
        TokenModel tokenModel= new TokenModel();
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(unused -> {
                });

    }
}
