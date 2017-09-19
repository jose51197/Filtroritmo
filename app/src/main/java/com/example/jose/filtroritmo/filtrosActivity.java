package com.example.jose.filtroritmo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.lang.Math;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class filtrosActivity extends AppCompatActivity {
    Bitmap imagen;
    Bitmap imagenFiltrada=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtros);
        getImagen();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    //seteo la imagen a la clase que pase en el intent anterior
    public void getImagen() {
        Intent intent = getIntent();
        String path=intent.getExtras().getString("path");
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //if (myBitmap.getHeight()>1920 || myBitmap.getWidth()>1920){
                this.imagen =myBitmap;
            //}
            setImagen(this.imagen);
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    //setea imagenes filtradas en el widget
    public void setImagen(Bitmap imagen) {
        ImageView imagensita = (ImageView) findViewById(R.id.imageView);
        imagensita.setImageBitmap(imagen);
        this.imagenFiltrada=imagen;
    }

    public void dM(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        ByteBuffer buf = ByteBuffer.allocate(filtrada.getHeight()*filtrada.getWidth()*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte color;long startTime = System.currentTimeMillis();
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) max(max((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF));
            //System.out.println(String.valueOf(color));
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
            //bytes[i+3]=color;
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado dM");
        setImagen(filtrada);
    }

    public void dm(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        ByteBuffer buf = ByteBuffer.allocate(filtrada.getHeight()*filtrada.getWidth()*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte color; long startTime = System.currentTimeMillis();
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) min(min((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF));
            //System.out.println(String.valueOf(color));
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado dm");
        setImagen(filtrada);
    }
    public void emergencia(View view){
        this.imagen=getResizedBitmap(this.imagen,1080);
    }
    //el mas facil y con el cual empezar
    public void average(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        ByteBuffer buf = ByteBuffer.allocate(filtrada.getHeight()*filtrada.getWidth()*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte color;long startTime = System.currentTimeMillis();
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) (((bytes[i+1] & 0xFF) + (bytes[i+2] & 0xFF) + (bytes[i] & 0xFF))/3);
            bytes[i]= color;//rojo
            bytes[i+1]=color;//verde
            bytes[i+2]=color;//azul
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado AVG");
        setImagen(filtrada);
    }

    //
    public void desaturation(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        ByteBuffer buf = ByteBuffer.allocate(filtrada.getHeight()*filtrada.getWidth()*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte color;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < bytes.length; i+=4) {
            color =(byte) ((max(max((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF)) + min(min((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF))) >> 1);//un shift right es dividir por 2 #Asembler xD
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }
    //maximo de los de alrededor
    public void hotline(View view) {
        Bitmap testSubject = this.imagen.copy(this.imagen.getConfig(), true);
        int altura = testSubject.getHeight();
        int ancho = testSubject.getWidth();
        System.out.println("ancho= "+ancho+"  altura= "+altura);
        System.out.println("Pixeles: "+(ancho*altura) );
        long startTime = System.currentTimeMillis();
        int i;
        int j;
        int isuma;
        int jsuma;
        int color;
        double []kernel={-.1,-.1,-.1,-.1,1,-.1,-.1,-.1,-.1};
        int[] Fragmento = new int[9];
        for (i = 0; i < altura; i=i+3) {
            for (j = 0; j < ancho; j=j+3) {
                jsuma = -2;
                isuma = -1;
                for (int q = 0; q < 9; q++) {
                    jsuma++;
                    if (jsuma == 2) {
                        jsuma = -1;
                        isuma++;
                    }
                    try {
                        Fragmento[q] = this.imagen.getPixel(j + jsuma, i + isuma);
                    } catch (Exception e) {
                        Fragmento[q] = 0;
                    }
                }
                color= ejecutarKernel(Fragmento,kernel);
                jsuma = -2;
                isuma = -1;
                for (int q = 0; q < 9; q++) {
                    jsuma++;
                    if (jsuma == 2) {
                        jsuma = -1;
                        isuma++;
                    }
                    try {
                        testSubject.setPixel(j+jsuma, i+isuma, color);
                    } catch (Exception e) {

                    }
                }

            }
        }
        System.out.println("Aplicado gauss");
        setImagen(testSubject);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Duracion"+String.valueOf(totalTime));
}

    public void original(View view){
        setImagen(this.imagen);
    }

    private int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    private int min(int a, int b) {
        if (a > b) {
            return b;
        }
        return a;
    }

    public void gauss(View view) {
        Bitmap testSubject = this.imagen.copy(this.imagen.getConfig(), true);
        int altura = testSubject.getHeight();
        int ancho = testSubject.getWidth();
        System.out.println("ancho= "+ancho+"  altura= "+altura);
        System.out.println("Pixeles: "+(ancho*altura) );
        long startTime = System.currentTimeMillis();
        int i;
        int j;
        int isuma;
        int jsuma;
        int color;
        double []kernel=crearKernel(40);
        int[] Fragmento = new int[25];
        for (i = 0; i < altura; i=i+5) {
            for (j = 0; j < ancho; j=j+5) {
                jsuma = -3;
                isuma = -2;
                for (int q = 0; q < 25; q++) {
                    jsuma++;
                    if (jsuma == 3) {
                        jsuma = -2;
                        isuma++;
                    }
                    try {
                        Fragmento[q] = this.imagen.getPixel(j + jsuma, i + isuma);
                    } catch (Exception e) {
                        Fragmento[q] = 0;
                    }
                }
                color= ejecutarKernel(Fragmento,kernel);
                jsuma = -3;
                isuma = -2;
                for (int q = 0; q < 25; q++) {
                    jsuma++;
                    if (jsuma == 3) {
                        jsuma = -2;
                        isuma++;
                    }
                    try {
                        testSubject.setPixel(j+jsuma, i+isuma, color);
                    } catch (Exception e) {

                    }
                }

            }
        }
        System.out.println("Aplicado gauss");
        setImagen(testSubject);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Duracion"+String.valueOf(totalTime));
    }


    private double[] crearKernel(double sigma){
        double [] kernelResul  = new double[25];
        int i=-3;
        int j=-2;
        double sum=0;
        for(int q=0;q<25;q++){
            i++;
            if(i==3){
                j++;
                i=-2;
            }
            kernelResul[q]=((1/(2*Math.PI*Math.pow(sigma,2))) * Math.pow(Math.E,-((Math.pow(i,2)+Math.pow(j,2))/(2*Math.pow(sigma,2)))));
        }
        for(int q=0;q<25;q++){
            sum+=kernelResul[q];
        }
        for(int q=0;q<25;q++){
            kernelResul[q]=kernelResul[q]*(1/sum);
        }
        return kernelResul;
    }

    private int ejecutarKernel(int[] kernelE,double[] kernel) {
        int tamanho =kernel.length;
        double rojo = 0;
        double azul = 0;
        double verde = 0;
        int k;
        for(k=0;k<tamanho;k++){
            rojo += Color.red(kernelE[k])*kernel[k];
        }
        for(k=0;k<tamanho;k++){
            verde += Color.green(kernelE[k])*kernel[k];
        }
        for(k=0;k<tamanho;k++){
            azul += Color.blue(kernelE[k])*kernel[k];
        }

        int red=(int)rojo;
        int green= (int)verde;
        int blue=(int)azul;
        return Color.rgb(red, green, blue);
    }

    private int ejecutarKernel(int[] kernelE,int[] kernel) {
        int tamanho =kernel.length;
        int color = 0;
        int k;
        for(k=0;k<tamanho;k++){
            color += kernelE[k]*kernel[k];
        }
        return color;
    }
    public void guardarAmbas(View view) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        Date date = new Date();
        saveImage(this.imagen,  dateFormat.format(date)+ "Original");
        if (this.imagenFiltrada != null) {
            saveImage(this.imagenFiltrada, dateFormat.format(date) + "Filtrada");
        }
    }
    private void saveImage(Bitmap finalBitmap,String filename) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/filtro");
        myDir.mkdirs();
        String fname = filename+".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("error al crear");
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
        new MediaScannerConnection.OnScanCompletedListener() {
        public void onScanCompleted(String path, Uri uri) {
        Log.i("ExternalStorage", "Scanned " + path + ":");
        Log.i("ExternalStorage", "-> uri=" + uri);
        }
        });
    }
}