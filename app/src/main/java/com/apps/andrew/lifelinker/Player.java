package com.apps.andrew.lifelinker;

import java.util.UUID;

/**
 * Created by Andrew on 8/3/2015.
 */
public class Player {

    private UUID mId;
    private String mName;
    private int mLife;
    private int mPoison;

    public Player(){
        mId = UUID.randomUUID();
        mLife = 20;
        mPoison = 0;
        mName = null;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getLife() {
        return mLife;
    }

    public void setLife(int mLife) {
        this.mLife = mLife;
    }

    public int getPoison() {
        return mPoison;
    }

    public void setPoison(int mPoison) {
        this.mPoison = mPoison;
    }
}
