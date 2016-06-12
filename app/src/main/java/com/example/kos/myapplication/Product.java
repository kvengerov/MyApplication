package com.example.kos.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable{

    @SerializedName("pid")
    public int pid;

    @SerializedName("name")
    public String name;

    @SerializedName("qty")
    public int qty;

    @SerializedName("price")
    public double price;

    @SerializedName("image_url")
    public String image_url;


}
