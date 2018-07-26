package me.dats.com.datsme.Models;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Date;

import me.dats.com.datsme.Utils.DateParser;

public class Messages implements Serializable,Comparable {
    private String message, type, from;
    private boolean seen;
    private long time;


    public Messages() {
    }

    public Messages(String message, boolean seen, long time, String type, String from) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }



    @Override
    public int compareTo(@NonNull Object o) {

        long comparetime=((Messages)o).getTime();
        if(comparetime>this.time)
        return 1;
        else return 0;
    }
}
