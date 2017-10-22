package com.android.skripsi.keluarga;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.skripsi.keluarga.Adapter.GalleryAdapter;
import com.android.skripsi.keluarga.Models.Gallery;
import com.android.skripsi.keluarga.Utility.RequestServer;
import com.android.skripsi.keluarga.Utility.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private GridView mGridView;
    private GalleryAdapter mGridAdapter;
    private List<Gallery> mGridData;
    private JsonArray data;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GalleryActivity.this, TambahGalleryActivity.class);
                startActivity(i);
            }
        });

        mGridView = (GridView) findViewById(R.id.gridView);
    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }

    private void loadData(){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        mGridData = new ArrayList<>();
        data = new JsonArray();

        String url = new RequestServer().getServer_url() + "getGallery";
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("keluarga_id", session.getKeluargaId());

        Log.d("url",">"+url);

        Ion.with(this)
                .load(url)
                .setJsonObjectBody(jsonReq)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("result",">"+result);
                        try{
                            data = result.getAsJsonArray("data");
                            for (int i=0; i<data.size(); i++){
                                JsonObject objData = data.get(i).getAsJsonObject();

                                String photo = "";
                                if(!objData.get("img").getAsString().equals("")){
                                    photo = new RequestServer().getImg_url()+"gallery/"+objData.get("img").getAsString();
                                }
                                Log.d("photo",">"+photo);

                                mGridData.add(new Gallery(
                                        objData.get("id").getAsString(),
                                        photo
                                ));

                            }
                            mGridAdapter = new GalleryAdapter(GalleryActivity.this, R.layout.grid_gallery_layout, mGridData);
                            mGridView.setAdapter(mGridAdapter);

                            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    //Get item at position
                                    Gallery item = (Gallery) parent.getItemAtPosition(position);
                                    //Iklan item = mGridData.get(position);

                                    Intent i = new Intent(GalleryActivity.this,DetailGalleryActivity.class);
                                    i.putExtra("id_gallery",item.id_gallery);
                                    startActivity(i);
                                }
                            });

                        }catch (Exception ex){
                            Snackbar.make(findViewById(R.id.gridView), "Terjadi kesalahan saaat menyambung ke server.", Snackbar.LENGTH_INDEFINITE)
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
