package com.mathias.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.mathias.whatsapp.R;
import com.mathias.whatsapp.config.configuracaoFirebase;
import com.mathias.whatsapp.helper.Base64Custom;
import com.mathias.whatsapp.helper.UsuarioFirebase;
import com.mathias.whatsapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome,campoEmail,campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editPerfilNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void cadastrarUsuario(final Usuario usuario) {
        autenticacao = configuracaoFirebase.getFireBaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()

        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this,"Sucesso ao cadastrar usuário",Toast.LENGTH_SHORT).show();
                    UsuarioFirebase.atualizaNomeUsuario(usuario.getNome());
                    finish();

                    try {

                        String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(identificadorUsuario);
                        usuario.salvar();

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
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
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,excessao,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void validarCadastroUsuario(View view) {

        //Recuperar textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

         if (!textoNome.isEmpty() ) { // Verificanome
             if(!textoEmail.isEmpty()) { // Verifica Email
                if(!textoSenha.isEmpty()) { // Verifica Senha
                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario(usuario);

                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha!",Toast.LENGTH_SHORT).show();
                }

             } else {
                 Toast.makeText(CadastroActivity.this,
                         "Preencha o e-mail!",Toast.LENGTH_SHORT).show();
             }

         } else {
             Toast.makeText(CadastroActivity.this,
                     "Preencha o nome!",Toast.LENGTH_SHORT).show();
         }


    }
}
