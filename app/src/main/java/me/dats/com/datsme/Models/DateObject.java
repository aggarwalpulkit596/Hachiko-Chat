package me.dats.com.datsme.Models;

public class DateObject extends ListObject {
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType(String userId) {
        return TYPE_DATE;
    }
}
