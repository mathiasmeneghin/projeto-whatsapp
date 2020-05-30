package com.mathias.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.activity.ChatActivity;
import com.mathias.whatsapp.adapter.ContatosAdapter;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.RecyclerItemClickListener;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Usuario;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Configuracoes iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = configuracaoFirebase.getFireBaseDatabase().child("usuarios");


        //configurar o adpter
        adapter = new ContatosAdapter(listaContatos,getActivity());

        //configurar o recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);

        //configurar o evento de clique no recyclerView
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario usuarioSelecionado = listaContatos.get(position);
                                Intent i = new Intent(getActivity(), ChatActivity.class);
                                i.putExtra("chatContato",usuarioSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos() {
      valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {

          FirebaseUser usuarioAtual =  UsuarioFirebase.getUsuarioAtual();
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for (DataSnapshot dados: dataSnapshot.getChildren())  {

                   Usuario usuario = dados.getValue(Usuario.class);

                   String  emailUsuarioAtual = usuarioAtual.getEmail();

                   if(!emailUsuarioAtual.contains(usuario.getEmail())) {
                       listaContatos.add(usuario);
                   }



               }

               adapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }
}
