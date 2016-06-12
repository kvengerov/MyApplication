package com.example.kos.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.*;
import com.kosalgeek.genasync12.MainActivity;

import java.util.HashMap;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etQty, etPrice, etImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = (EditText)findViewById(R.id.etName);
        etQty = (EditText)findViewById(R.id.etQty);
        etPrice = (EditText)findViewById(R.id.etPrice);
        etImageUrl = (EditText)findViewById(R.id.etImageUrl);
        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        HashMap postData = new HashMap();
        postData.put("txtName", etName.getText().toString());
        postData.put("txtQty", etQty.getText().toString());
        postData.put("txtPrice", etPrice.getText().toString());
        postData.put("txtImageUrl", etImageUrl.getText().toString());
        postData.put("mobile", "android");

        PostResponseAsyncTask task = new PostResponseAsyncTask(
                Main2Activity.this,
                postData,
                new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                Toast.makeText(Main2Activity.this, s, Toast.LENGTH_LONG).show();
//                if(s.contains("success")){
//                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
//                }
            }
        });
        task.execute("http://androidev.16mb.com/insert.php");
    }
}
