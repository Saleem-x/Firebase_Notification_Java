package com.example.firebase_notification;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    // Reference to your Firebase Database
    private DocumentReference tokenDocument;


    // Constructor
    public FirebaseHelper() {
        // Initialize the Firebase Database
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Reference to the root of your Firebase Database
        tokenDocument = firestore.collection("tokens").document("fcm");
    }

    // Function to upload a string to Firebase Database
    public void uploadString(String data) {
        // Generate a unique key for the data
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", data);

        // Update the document with the token data
        tokenDocument.set(tokenData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Token saved successfully
                        System.out.println("Token saved");
                    }
                })
                .addOnFailureListener(e -> {
                    // Error handling
                    System.err.println("Error adding token: " + e.getMessage());
                });
    }
}
