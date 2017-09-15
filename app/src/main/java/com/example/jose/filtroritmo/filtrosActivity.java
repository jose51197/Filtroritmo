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
    //para quedar con todo al dia
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

    public void gauss(View view){
        Bitmap testSubject =this.imagen.copy(this.imagen.getConfig(),true);
        Bitmap testSubjectAlter =this.imagen.copy(this.imagen.getConfig(),true);
        int altura = testSubject.getHeight();
        int ancho  = testSubject.getWidth();
        int i;
        int j;
        int isuma;
        int jsuma;
        int sumaCampana;
        int [] kernel=new int[9];
        for ( i=0;i<altura;i++){
            for ( j=0;j<ancho;j++){
                jsuma=-2;
                isuma=-1;
                for(int q=0;q<9;q++){
                    jsuma++;
                    if(jsuma==2){
                        jsuma=-1;
                        isuma++;
                    }
                    try{
                        kernel[q]=testSubject.getPixel(j+jsuma,i+isuma);
                    }
                    catch(Exception e){
                        kernel[q]=0;
                    }
                }
                testSubjectAlter.setPixel(j,i,ejecutarKernel(kernel));
            }
        }
        System.out.println("Aplicado gauss");
        setImagen(testSubjectAlter);
    }

    private int ejecutarKernel(int[] kernel){
        int rojo=0;
        int azul=0;
        int verde=0;
        int [] kernelProcedure=new int[9];
        kernelProcedure[0]=Color.red(kernel[0]);
        kernelProcedure[1]=Color.red(kernel[1])*2;
        kernelProcedure[2]=Color.red(kernel[2]);
        kernelProcedure[3]=Color.red(kernel[3])*2;
        kernelProcedure[4]=Color.red(kernel[4])*4;
        kernelProcedure[5]=Color.red(kernel[5])*2;
        kernelProcedure[6]=Color.red(kernel[6]);
        kernelProcedure[7]=Color.red(kernel[7])*2;
        kernelProcedure[8]=Color.red(kernel[8]);
        for(int i=0;i<9;i++){
            rojo+=kernelProcedure[i];
        }
        rojo=rojo/16;
        kernelProcedure[0]=Color.green(kernel[0]);
        kernelProcedure[1]=Color.green(kernel[1])*2;
        kernelProcedure[2]=Color.green(kernel[2]);
        kernelProcedure[3]=Color.green(kernel[3])*2;
        kernelProcedure[4]=Color.green(kernel[4])*4;
        kernelProcedure[5]=Color.green(kernel[5])*2;
        kernelProcedure[6]=Color.green(kernel[6]);
        kernelProcedure[7]=Color.green(kernel[7])*2;
        kernelProcedure[8]=Color.green(kernel[8]);
        for(int i=0;i<9;i++){
            verde+=kernelProcedure[i];
        }
        verde=verde/16;
        kernelProcedure[0]=Color.blue(kernel[0]);
        kernelProcedure[1]=Color.blue(kernel[1])*2;
        kernelProcedure[2]=Color.blue(kernel[2]);
        kernelProcedure[3]=Color.blue(kernel[3])*2;
        kernelProcedure[4]=Color.blue(kernel[4])*4;
        kernelProcedure[5]=Color.blue(kernel[5])*2;
        kernelProcedure[6]=Color.blue(kernel[6]);
        kernelProcedure[7]=Color.blue(kernel[7])*2;
        kernelProcedure[8]=Color.blue(kernel[8]);
        for(int i=0;i<9;i++){
            azul+=kernelProcedure[i];
        }
        azul=azul/16;
        return Color.rgb(rojo,verde,azul);}



}