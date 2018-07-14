package me.dats.com.datsme.Models;

//ListObject.java (to determind the type of message)
public abstract class ListObject {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_GENERAL_RIGHT = 1;
    public static final int TYPE_GENERAL_LEFT = 2;

    abstract public int getType(String userId);
}