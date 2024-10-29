package com.example.proyectogrupo4_gtics.Config;

import java.io.Serializable;

public class Image implements Serializable {
    private byte[]  content;
    private String sender;
    private  String recipient;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
