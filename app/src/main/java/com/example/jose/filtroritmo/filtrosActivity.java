package com.example.jose.filtroritmo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.nio.ByteBuffer;

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
        setImagen();
    }
    //setea imagenes filtradas en el widget
    public void setImagen(){
        ImageView imagensita = (ImageView)findViewById(R.id.imageView);
        imagensita.setImageBitmap(this.imagen);
    }
    public void dM(View view){
    }
    public void dm(View view){
    }
    //el mas facil y con el cual empezar
    public void average(View view){
        Bitmap filtrada=this.imagen.copy(this.imagen.getConfig(),true);
        int altura = filtrada.getHeight();
        int ancho  = filtrada.getWidth();
        int actual;
        byte [] bytes;
        for (int i=0;i<altura;i++){
            for (int j=0;j<ancho;j++){
                try{
                    actual=filtrada.getPixel(i,j);
                    bytes= ByteBuffer.allocate(4).putInt(actual).array();
                    for(byte b : bytes){
                        actual+=b;
                    }
                    actual=actual/3;
                    actual= (actual & 0xff) << 24 | (actual & 0xff) << 16 | (actual & 0xff) << 16 | (actual & 0xff);

                    filtrada.setPixel(i,j,actual);
                }
                catch(Exception e){
                    System.out.println("crash en"+String.valueOf(i)+","+String.valueOf(j));
                }

            }
        }
        System.out.println("Logrado");
        this.imagen=filtrada;
        setImagen();
    }
    public void desaturation(View view){
    }



}
