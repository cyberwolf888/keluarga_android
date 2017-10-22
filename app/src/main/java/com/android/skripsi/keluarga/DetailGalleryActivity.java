package com.android.skripsi.keluarga;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.skripsi.keluarga.Utility.RequestServer;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class DetailGalleryActivity extends AppCompatActivity {
    private ImageView iv,imgGallery;
    private  TextView tvName,tvEmail,tvUpload,tvViews,tvCaption;
    private String id_gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_gallery = getIntent().getStringExtra("id_gallery");
        setContentView(R.layout.activity_detail_gallery);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv = (ImageView) findViewById(R.id.imageView);
        imgGallery = (ImageView) findViewById(R.id.imgGallery);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvUpload = (TextView) findViewById(R.id.tvUpload);
        tvViews = (TextView) findViewById(R.id.tvViews);
        tvCaption = (TextView) findViewById(R.id.tvCaption);
    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }

    private void loadData()
    {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        String url = new RequestServer().getServer_url() + "detailGallery";
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("id_gallery", id_gallery);

        Log.d("url",">"+url);
        Log.d("jsonReq",">"+jsonReq);
        Ion.with(this)
                .load(url)
                .setJsonObjectBody(jsonReq)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("result",">"+result);

                        if(result.get("status").getAsString().equals("1")){
                            JsonObject data = result.get("data").getAsJsonObject();
                            if(!data.get("foto").getAsString().equals("")){
                                Ion.with(DetailGalleryActivity.this)
                                        .load(data.get("foto").getAsString())
                                        .withBitmap()
                                        .placeholder(R.drawable.guest)
                                        .error(R.drawable.guest)
                                        .intoImageView(iv);
                            }
                            Ion.with(DetailGalleryActivity.this)
                                    .load(data.get("img").getAsString())
                                    .withBitmap()
                                    .placeholder(R.drawable.noimage)
                                    .error(R.drawable.noimage)
                                    .intoImageView(imgGallery);
                            tvName.setText(data.get("name").getAsString());
                            tvEmail.setText(data.get("email").getAsString());
                            tvUpload.setText(data.get("diupload").getAsString());
                            tvViews.setText(data.get("dilihat").getAsString());
                            tvCaption.setText(data.get("caption").getAsString());

                        }else{
                            Snackbar.make(findViewById(R.id.imageView), "Terjadi kesalahan saaat menyambung ke server.", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Tutup", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                        }
                        pDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
