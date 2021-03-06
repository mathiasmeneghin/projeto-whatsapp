package com.mathias.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.activity.ChatActivity;
import com.mathias.whatsapp.adapter.ConversasAdapter;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.RecyclerItemClickListener;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Conversa;
import com.mathias.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

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

        //Configurar Evento de clique
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Conversa> listaConversaAtualizada = adapter.getConversas();
                        Conversa conversaSelecionada = listaConversaAtualizada.get(position);

                        if(conversaSelecionada.getIsGroup().equals("true")) {

                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatGrupo",conversaSelecionada.getGrupo());
                            startActivity(i);

                        }else {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatContato",conversaSelecionada.getUsuarioExibicao());
                            startActivity(i);

                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

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
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarCoversas(String texto) {
        //Log.d("pesquisa",texto);
        List<Conversa> listaConversasBusca = new ArrayList<>();


        for (Conversa conversa : listaConversas) {

            if(conversa.getUsuarioExibicao() != null ) {
                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();
                if (nome.contains(texto) || ultimaMsg.contains(texto)) {
                    listaConversasBusca.add(conversa);
                }

            }else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();
                if (nome.contains(texto) || ultimaMsg.contains(texto)) {
                    listaConversasBusca.add(conversa);
                }

            }



        }
       adapter = new ConversasAdapter(listaConversasBusca,getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarConversas() {
        adapter = new ConversasAdapter(listaConversas,getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarConversas() {

        listaConversas.clear();

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
