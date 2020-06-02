package com.mathias.whatsapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.adapter.ConversasAdapter;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Conversa;
import com.mathias.whatsapp.model.Usuario;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ArrayList<Conversa> listaConversas = new ArrayList<>();
    private RecyclerView recyclerViewConversas;
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;
    private ChildEventListener childEventListenerConversas;

    public ConversasFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);

        // configurar adapter
        adapter = new ConversasAdapter(listaConversas, getActivity());

        // configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //Configura conversasref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = configuracaoFirebase.getFireBaseDatabase();
        conversasRef = database.child("conversas")
                .child(identificadorUsuario);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        RecuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void RecuperarConversas() {


      childEventListenerConversas =  conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Recuperar Conversas
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
