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

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etPrice, etQty, etImageUrl;
    Button btnUpdate;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        etName = (EditText)findViewById(R.id.etName);
        etQty = (EditText)findViewById(R.id.etQty);
        etPrice = (EditText)findViewById(R.id.etPrice);
        etImageUrl = (EditText)findViewById(R.id.etImageUrl);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        product = (Product) getIntent().getSerializableExtra("product");
        if(product == null) {
            return;
        }

        etName.setText(product.name);
        etQty.setText("" + product.qty);
        etPrice.setText("" + product.price);
        etImageUrl.setText(product.image_url);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnUpdate){

            if(product == null){
                return;
            }

            HashMap postData = new HashMap();
            postData.put("mobile", "android");
            postData.put("pid", "" + product.pid);
            postData.put("txtName", etName.getText().toString());
            postData.put("txtQty", etQty.getText().toString());
            postData.put("txtPrice", etPrice.getText().toString());
            postData.put("ImageUrl", etImageUrl.getText().toString());

            PostResponseAsyncTask taskUpdate =
                    new PostResponseAsyncTask(this, postData, new AsyncResponse() {
                @Override
                public void processFinish(String s) {
                    if(s.contains("success")){
                        Toast.makeText(DetailActivity.this, "Update", Toast.LENGTH_LONG).show();
                        Intent in = new Intent(DetailActivity.this, SubActivity.class);
                        startActivity(in);
                    }
                    else{
                        Toast.makeText(DetailActivity.this, "Something wrong. Cannot Update it.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            taskUpdate.execute("http://androidev.16mb.com/update.php");
        }
    }
}
