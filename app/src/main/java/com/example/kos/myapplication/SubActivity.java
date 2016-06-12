package com.example.kos.myapplication;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.amigold.fundapter.interfaces.ItemClickListener;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SubActivity extends AppCompatActivity implements AsyncResponse,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private SwipeRefreshLayout swipe_refresh_layout;
    private ListView lvProduct;
    private FunDapter<Product> adapter;
    private ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubActivity.this, Main2Activity.class));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PostResponseAsyncTask task = new PostResponseAsyncTask(SubActivity.this, this);
        task.execute("http://androidev.16mb.com/product.php");

        swipe_refresh_layout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setOnRefreshListener(this);

        lvProduct = (ListView)findViewById(R.id.lvProduct);

        registerForContextMenu(lvProduct);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_activity_contex_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Product selectedProduct = adapter.getItem(info.position);

        if(item.getItemId() == R.id.menuUpdate){

            Intent in = new Intent(SubActivity.this, DetailActivity.class);
            in.putExtra("product", selectedProduct);
            startActivity(in);
        }
        else if (item.getItemId() == R.id.menuRemove){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Do you want to remove this?");
            alert.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    productList.remove(selectedProduct);

                    SubActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    HashMap postData = new HashMap();
                    postData.put("mobile", "android");
                    postData.put("pid", "" + selectedProduct.pid);

                    PostResponseAsyncTask taskRemove =
                            new PostResponseAsyncTask(SubActivity.this, postData, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    if(s.contains("success")){
                                        Toast.makeText(SubActivity.this, "Removed", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(SubActivity.this, "Something wrong.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    taskRemove.execute("http://androidev.16mb.com/remove.php");

                }
            });
            alert.setNegativeButton("Cancel", null);
            alert.show();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void processFinish(String jsonText) {

        ImageLoader.getInstance().init(UILConfig());

        productList = new JsonConverter<Product>().toArrayList(jsonText, Product.class);

        BindDictionary<Product> dict = new BindDictionary<Product>();
        dict.addStringField(R.id.tvName, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product product, int position) {
                return product.name;
            }
        }).onClick(new ItemClickListener<Product>() {
            @Override
            public void onClick(Product item, int position, View view) {
                Toast.makeText(SubActivity.this, item.name, Toast.LENGTH_LONG).show();            }
        });
        dict.addStringField(R.id.tvQty, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product product, int position) {
                return "" + product.qty;
            }
        });
        dict.addStringField(R.id.tvPrice, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product product, int position) {
                return "" + product.price;
            }
        });

        dict.addDynamicImageField(R.id.imageView, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product product, int position) {
                return product.image_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String url, ImageView view) {
                ImageLoader.getInstance().displayImage(url, view);
                view.setPadding(0, 0, 0, 0);
                view.setAdjustViewBounds(true);
            }
        }
    );

        adapter = new FunDapter<>(SubActivity.this,
                productList, R.layout.product_layout, dict);

        lvProduct.setAdapter(adapter);
        lvProduct.setOnItemClickListener(this);

    }

    private ImageLoaderConfiguration UILConfig(){
        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnLoading(android.R.drawable.stat_sys_download)
                        .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                        .showImageOnFail(android.R.drawable.stat_notify_error)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .build();

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration
                        .Builder(getApplicationContext())
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .denyCacheImageMultipleSizesInMemory()
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .defaultDisplayImageOptions(defaultOptions)
                        .build();

        return config;
    }

    @Override
    public void onRefresh() {
        swipe_refresh_layout.setRefreshing(true);

        PostResponseAsyncTask task = new PostResponseAsyncTask(SubActivity.this, this);
        task.execute("http://androidev.16mb.com/product.php");

        swipe_refresh_layout.setRefreshing(false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Product product = productList.get(position);

        Intent in = new Intent(SubActivity.this, DetailActivity.class);
        in.putExtra("product", product);
        startActivity(in);
    }
}
