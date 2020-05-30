package com.mathias.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class configuracaoFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //Retorna a instancia do firebase
    public static DatabaseReference getFireBaseDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();

        }
        return database;
    }

    //Retorna a instancia do auth
    public static FirebaseAuth getFireBaseAutenticacao() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;


    }

    public static StorageReference getFirebaseStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
