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
        setImagen(this.imagen);
    }
    //setea imagenes filtradas en el widget
    public void setImagen(Bitmap imagen){
        ImageView imagensita = (ImageView)findViewById(R.id.imageView);
        imagensita.setImageBitmap(imagen);
    }
    public void dM(View view){
        Bitmap filtrada=this.imagen.copy(this.imagen.getConfig(),true);
        int ancho = filtrada.getWidth();
        int altura  = filtrada.getHeight();
        int actual;
        byte [] bytes;
        for (int i=0;i<ancho;i++){
            for (int j=0;j<altura;j++){
                actual=filtrada.getPixel(i,j);
                bytes= ByteBuffer.allocate(4).putInt(actual).array();
                actual=max(max((bytes[3]& 0xFF ),(bytes[1]& 0xFF)),(bytes[2]& 0xFF));
                actual= Color.rgb(actual,actual,actual);
                filtrada.setPixel(i,j,actual);
            }
        }
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }
    public void dm(View view){
        Bitmap filtrada=this.imagen.copy(this.imagen.getConfig(),true);
        int ancho = filtrada.getWidth();
        int altura  = filtrada.getHeight();
        int actual;
        byte [] bytes;
        for (int i=0;i<ancho;i++){
            for (int j=0;j<altura;j++){
                actual=filtrada.getPixel(i,j);
                bytes= ByteBuffer.allocate(4).putInt(actual).array();
                actual=min(min((bytes[3]& 0xFF ),(bytes[1]& 0xFF)),(bytes[2]& 0xFF));
                actual= Color.rgb(actual,actual,actual);
                filtrada.setPixel(i,j,actual);
            }
        }
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }
    //el mas facil y con el cual empezar
    public void average(View view){
        Bitmap filtrada=this.imagen.copy(this.imagen.getConfig(),true);
        int ancho = filtrada.getWidth();
        int altura  = filtrada.getHeight();
        int actual;
        byte [] bytes;
        for (int i=0;i<ancho;i++){
            for (int j=0;j<altura;j++){
                actual=filtrada.getPixel(i,j);
                bytes= ByteBuffer.allocate(4).putInt(actual).array();
                actual=((bytes[3]& 0xFF )+(bytes[1]& 0xFF) +(bytes[2]& 0xFF))/3;
                actual= Color.rgb(actual,actual,actual);
                filtrada.setPixel(i,j,actual);
            }
        }
        System.out.println("Aplicado average");
        setImagen(filtrada);
    }
    public void desaturation(View view){
        Bitmap filtrada=this.imagen.copy(this.imagen.getConfig(),true);
        int ancho = filtrada.getWidth();
        int altura  = filtrada.getHeight();
        int actual;
        byte [] bytes;
        for (int i=0;i<ancho;i++){
            for (int j=0;j<altura;j++){
                actual=filtrada.getPixel(i,j);
                bytes= ByteBuffer.allocate(4).putInt(actual).array();
                actual=(max(max((bytes[3]& 0xFF ),(bytes[1]& 0xFF)),(bytes[2]& 0xFF))+min(min((bytes[3]& 0xFF ),(bytes[1]& 0xFF)),(bytes[2]& 0xFF)) )/2;
                actual= Color.rgb(actual,actual,actual);
                filtrada.setPixel(i,j,actual);
            }
        }
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }

    private int max(int a, int b){
        if(a>b){
            return a;
        }
        return b;
    }
    private int min(int a, int b){
        if(a>b){
            return b;
        }
        return a;
    }



}
