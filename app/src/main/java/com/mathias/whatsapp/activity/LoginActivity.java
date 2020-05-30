package com.mathias.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail,campoSenha;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = configuracaoFirebase.getFireBaseAutenticacao();

        campoEmail = findViewById(R.id.editLoginSenha);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void logarUsuario(Usuario usuario) {

       autenticacao.signInWithEmailAndPassword(usuario.getEmail(),
               usuario.getSenha()
       ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()) {

                  abrirTelaPrincipal();
              } else {
                  String excessao = "";
                  try {
                      throw task.getException();

                  }catch (FirebaseAuthWeakPasswordException e) {
                      excessao = "Digite uma senha mais forte!";
                  } catch (FirebaseAuthInvalidCredentialsException e) {
                      excessao = "Por Favor, digite um e-mail valido";
                  } catch (FirebaseAuthUserCollisionException e ) {
                      excessao = "Esta conta já foi cadastrada";
                  } catch (Exception e) {
                      excessao = "Erro ao Logar usuário: " + e.getMessage();
                      e.printStackTrace();
                  }

                  Toast.makeText(LoginActivity.this,excessao,Toast.LENGTH_SHORT).show();

              }
           }
       });
    }

     public void validarAutenticacaoUsuario(View view) {

        //Recuperar texto dos campos

         String textoEmail = campoEmail.getText().toString();
         String textoSenha = campoSenha.getText().toString();

        // validar se o email e senha foram digitados

         if (!textoEmail.isEmpty() ) { // VerificaEmail
             if(!textoSenha.isEmpty()) { // Verifica Senha

                 Usuario usuario = new Usuario();
                 usuario.setEmail(textoEmail);
                 usuario.setSenha(textoSenha);

                 logarUsuario(usuario);


             } else {
                 Toast.makeText(LoginActivity.this,
                         "Preencha a senha!",Toast.LENGTH_SHORT).show();
             }

         } else {
             Toast.makeText(LoginActivity.this,
                     "Preencha o e-mail",Toast.LENGTH_SHORT).show();
         }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null ) {
           abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view) {
        Intent intent = new Intent(LoginActivity.this,CadastroActivity.class);
        startActivity(intent);

    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);

    }
}
