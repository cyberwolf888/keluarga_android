package com.android.skripsi.keluarga;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.skripsi.keluarga.Utility.RequestServer;
import com.android.skripsi.keluarga.Utility.Session;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class TambahGalleryActivity extends AppCompatActivity {
    private final static int WRITE_EXTERNAL_RESULT = 105;
    private final static int SELECT_PHOTO = 12345;
    private EditText etCaption;
    private ImageView imgUpload;
    private Button btnSimpan;
    private String imagePath;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_tambah_gallery);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mayRequestPermission();

        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        etCaption = (EditText) findViewById(R.id.etCaption);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanData();
            }
        });

    }

    private void simpanData(){
        etCaption.setError(null);
        String caption = etCaption.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(caption)) {
            etCaption.setError("Caption tidak boleh kosong");
            focusView = etCaption;
            cancel = true;
        }
        if (TextUtils.isEmpty(imagePath)) {
            Snackbar.make(findViewById(R.id.imgUpload), "Gambar tidak boleh kosong.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Tutup", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
            focusView = etCaption;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(TambahGalleryActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            String url = new RequestServer().getServer_url()+"uploadGallery";
            Log.d("Url",">"+url);

            Ion.with(this)
                    .load(url)
                    .setMultipartParameter("anggota_id", session.getAnggotaId())
                    .setMultipartParameter("keluarga_id", session.getKeluargaId())
                    .setMultipartParameter("caption", caption)
                    .setMultipartFile("img", "application/images", new File(imagePath))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d("result",">"+result);
                            pDialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            //Cek file size
            File file = new File(imagePath);
            int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
            Log.d("File Size",">"+file_size);
            if(file_size>(3*1024)){
                imagePath = "";
                Snackbar.make(findViewById(R.id.btnSimpan), "Ukuran gambar terlalu besar. Ukuran file maksimal 3 MB.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Tutup", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
            }else{
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);*/
                imgUpload.setImageBitmap(decodeSampledBitmapFromResource(imagePath, 300, 200));
            }

            cursor.close();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res, options);
    }

    private boolean mayRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_RESULT);
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(R.id.btnSimpan), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_RESULT);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_RESULT);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_RESULT) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission diterima
            }else{
                //permission ditolak
                //mayRequestPermission();
            }
        }
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
