package com.mathias.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.adapter.MensagensAdapter;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.Base64Custom;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Mensagem;
import com.mathias.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;
    private DatabaseReference database;
    private DatabaseReference mensagensRef = null;
    private ChildEventListener childEventListenerMensagens ;

    //Identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       //Configuracoes iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        //Recupera dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //Recuperar dados usuario destinatário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
            if (usuarioDestinatario != null) {
                textViewNome.setText(usuarioDestinatario.getNome());
            }
            String foto = usuarioDestinatario.getFoto();
           if (foto != null) {
               Uri url = Uri.parse(usuarioDestinatario.getFoto());
               Glide.with(ChatActivity.this).load(url).into(circleImageViewFoto);
           }else {
             circleImageViewFoto.setImageResource(R.drawable.padrao);
           }



           //recuperar dados do usuario destinatario
            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
        }







        // Configuracao adapter
        adapter = new MensagensAdapter( mensagens,getApplicationContext());


        //Configuracao recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);


        database = configuracaoFirebase.getFireBaseDatabase();
         mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);


    }

    public void enviarMensagem(View view) {
        String textomensagem = editMensagem.getText().toString();
        if (!textomensagem.isEmpty()) {
            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(textomensagem);




            //Salvar mensagem para o remetente
             SalvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);
            //Salvar mensagem para o destinatario
            SalvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);


        } else {
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enviar",
                    Toast.LENGTH_LONG).show();
        }

    }

    private  void SalvarMensagem(String idRemetente, String idDestinatario,Mensagem msg) {
        DatabaseReference database = configuracaoFirebase.getFireBaseDatabase();
        DatabaseReference mensagens = database.child("mensagens");
        mensagens.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
        // Limpar texto
        editMensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }




    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }



    private void recuperarMensagens() {

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
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
