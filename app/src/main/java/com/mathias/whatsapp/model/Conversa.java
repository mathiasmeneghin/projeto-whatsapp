package com.mathias.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.mathias.whatsapp.config.configuracaoFirebase;

public class Conversa {
    private String idRemetente;
    private String idDestinatario;
    private  String ultimaMensagem;
    private Usuario usuarioExibicao;

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public Conversa() {
    }

    public void salvar() {
        DatabaseReference database = configuracaoFirebase.getFireBaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child(this.getIdRemetente())
                .child(this.getIdDestinatario())
                .setValue(this);
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }


    }

