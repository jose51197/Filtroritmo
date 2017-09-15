package com.example.jose.filtroritmo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;









public class MainActivity extends AppCompatActivity {
    private final int SELECT_IMAGE =2;
    private Bitmap imageSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


    }
    public void botonCamara(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap fotoTomada= (Bitmap) extras.get("data");
            //Intent
        }
        if(requestCode==SELECT_IMAGE && resultCode == RESULT_OK && data!=null){
            Uri imagenElegida = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imagenElegida , filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap yourSelectedImage  = BitmapFactory.decodeFile(filePath);
        }
    }

    public void botonGaleria(View view){
        Intent openGalleryIntent = new Intent (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryIntent.setType("image/*");
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(openGalleryIntent, "Select Picture"),SELECT_IMAGE);
    }
}
