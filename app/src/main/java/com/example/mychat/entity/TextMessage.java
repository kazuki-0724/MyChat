package com.example.mychat.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TextMessage {

    public TextMessage(String textMessage, Integer from){
        this.textMessage = textMessage;
        this.from = from;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "text_message")
    private String textMessage;

    @ColumnInfo(name = "from")
    private Integer from;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }
}
