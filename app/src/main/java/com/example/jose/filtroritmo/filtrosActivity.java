package com.example.jose.filtroritmo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class filtrosActivity extends AppCompatActivity {
    Bitmap imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtros);
        getImagen();
    }
    //seteo la imagen a la clase que pase en el intent anterior
    public void getImagen(){
        Intent intent=getIntent();
        this.imagen = (Bitmap) intent.getParcelableExtra("imagen");
        setImagen(this.imagen);
    }
    //setea imagenes filtradas en el widget
    public void setImagen(Bitmap imagen){
        ImageView imagensita = (ImageView)findViewById(R.id.imageView);
        imagensita.setImageBitmap(this.imagen);
    }
    public void dM(View view){
    }
    public void dm(View view){
    }
    public void average(View view){
    }
    public void desaturation(View view){
    }



}
