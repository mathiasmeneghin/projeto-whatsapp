package com.mathias.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.adapter.ContatosAdapter;
import com.mathias.whatsapp.adapter.GrupoSelecionadoAdapter;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.RecyclerItemClickListener;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembrosSelecionados,recyclerMembros;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuariosRef;
    private Toolbar toolbar;
    private FloatingActionButton fabAvancarCadastro;

    public void AtualizarMembrosToolbar() {
        int totalselecionados = listaMembrosSelecionados.size();
        int total = listaMembros.size() + totalselecionados;
        toolbar.setSubtitle(totalselecionados + " de " + total + " selecionados" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Configuracoes iniciais
        recyclerMembros = findViewById(R.id.recyclerMembros);
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        fabAvancarCadastro = findViewById(R.id.fabAvancarCadastro);

        usuariosRef = configuracaoFirebase.getFireBaseDatabase().child("usuarios");

        //Configurar Adapter
        contatosAdapter = new ContatosAdapter(listaMembros,getApplicationContext());

        //Configurar RecyclerView para os contatos
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);

        recyclerMembros.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerMembros, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembros.get(position);
                //Remover usuario selecionado
                listaMembros.remove(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();
                //Adiciona na nova lista selecionados
                listaMembrosSelecionados.add(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();
                AtualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //Configurar Recyclerview para os membros selecionados
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados,getApplicationContext());
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        recyclerMembrosSelecionados.addOnItemTouchListener(new
                RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembrosSelecionados,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                 Usuario usuarioSelecionado = listaMembrosSelecionados.get(position);
                 // Remover da lista de membros selecionados
                listaMembrosSelecionados.remove(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();

                // adicionar a lista de membros
                listaMembros.add(usuarioSelecionado);
                contatosAdapter.notifyDataSetChanged();
                AtualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //Configurar floatinActionButton
        fabAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GrupoActivity.this,CadastroGrupoActivity.class);
                i.putExtra("membros",(Serializable) listaMembrosSelecionados);
                startActivity(i);

            }
        });

    }

    public void recuperarContatos() {
        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {

            FirebaseUser usuarioAtual =  UsuarioFirebase.getUsuarioAtual();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dados: dataSnapshot.getChildren())  {

                    Usuario usuario = dados.getValue(Usuario.class);

                    String  emailUsuarioAtual = usuarioAtual.getEmail();

                    if(!emailUsuarioAtual.contains(usuario.getEmail())) {
                        listaMembros.add(usuario);
                    }



                }

                contatosAdapter.notifyDataSetChanged();
                AtualizarMembrosToolbar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerMembros);
    }
}