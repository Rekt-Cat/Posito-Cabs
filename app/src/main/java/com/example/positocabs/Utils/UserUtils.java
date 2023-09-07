package com.example.positocabs.Utils;

import android.content.Context;
import android.util.Log;

import com.example.positocabs.Models.TokenModel;
import com.example.positocabs.Services.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserUtils {

    public static void updateToken(Context context, String token) {
        TokenModel tokenModel= new TokenModel(token);
        FirebaseDatabase.getInstance().getReference().child(Common.TOKEN_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(e ->
                        Log.d("hase", ""+e.getMessage())).addOnSuccessListener(unused -> {
                });


    }
}
