package com.example.m16142.pulseoximeter;

import java.io.Serializable;

/**
 * Created by m16142 on 6/26/2015.
 */
public class User implements Serializable {
    private String mId;
    private String mName;
    private int mAge;


    public User(String mId, String mName, int mAge){
        this.mId = mId;
        this.mName = mName;
        this.mAge = mAge;
    }

    public User(){

    }

    public User(String mId){
        this.mId = mId;
    }

    public String getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public int getAge(){
        return mAge;
    }

    public void setId(String id){
        mId = id;
    }

    public void setName(String name){
        mName = name;
    }

    public void setAge(int age){
        mAge = age;
    }
}
