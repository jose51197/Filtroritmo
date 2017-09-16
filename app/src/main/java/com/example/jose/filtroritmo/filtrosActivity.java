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
        byte color;
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) max(max((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF));
            //System.out.println(String.valueOf(color));
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
            //bytes[i+3]=color;
        }
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
        byte color;
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) min(min((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF));
            //System.out.println(String.valueOf(color));
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
        }
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado dm");
        setImagen(filtrada);
    }

    //el mas facil y con el cual empezar
    public void average(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        ByteBuffer buf = ByteBuffer.allocate(filtrada.getHeight()*filtrada.getWidth()*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte color;
        for (int i = 0; i < bytes.length; i+=4) {
            color = (byte) (((bytes[i+1] & 0xFF) + (bytes[i+2] & 0xFF) + (bytes[i] & 0xFF))/3);
            bytes[i]= color;//rojo
            bytes[i+1]=color;//verde
            bytes[i+2]=color;//azul
        }
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
        for (int i = 0; i < bytes.length; i+=4) {
            color =(byte) ((max(max((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF)) + min(min((bytes[i+1] & 0xFF), (bytes[i+2] & 0xFF)), (bytes[i] & 0xFF))) >> 1);//un shift right es dividir por 2 #Asembler xD
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
        }
        ByteBuffer retBuf = ByteBuffer.wrap(bytes);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }
    //maximo de los de alrededor
    public void hotline(View view) {
        Bitmap filtrada = this.imagen.copy(this.imagen.getConfig(), true);
        int altura = filtrada.getHeight();
        int ancho=filtrada.getWidth();
        int total=altura*ancho;

        ByteBuffer buf = ByteBuffer.allocate(total*4);
        filtrada.copyPixelsToBuffer(buf);
        byte[] bytes=buf.array();
        byte[] bytesFinales=buf.array().clone();
        int color;
        int[] kernel= new int[9];
        int ianch;
        int iancho;

        for (int i = 0; i < bytes.length; i+=4) {
            //bytes[i+1] & 0xFF
            //if no es primera fila
            ianch=i-ancho;
            iancho=i+ancho;
            if(ianch-4>0){//ya se que agarra pixeles incorrectos a veces, me vale xD es parte del filtro
                kernel[0]=Color.rgb(bytes[ianch-4],bytes[ianch+1-4],bytes[ianch+2-4]);//pi-anchoxel
                kernel[1]=Color.rgb(bytes[ianch],bytes[ianch+1],bytes[ianch+2]);//pi-anchoxel
                kernel[2]=Color.rgb(bytes[ianch+4],bytes[ianch+1+4],bytes[ianch+2+4]);//pi-anchoxel
            }
            else{
                kernel[0]=0;
                kernel[1]=0;
                kernel[2]=0;
            }
            if(i-4>0){
                kernel[3]=Color.rgb(bytes[i-4],bytes[i+1-4],bytes[i+2-4]);//pixel iz
            }
            else{
                kernel[3]=0;
            }

            kernel[4]=Color.rgb((bytes[i] & 0xFF),(bytes[i+1] & 0xFF),(bytes[i+2] & 0xFF));//pixel actual

            if(i+2+4<total){
                kernel[5]=Color.rgb(bytes[i+4],bytes[i+1+4],bytes[i+2+4]);//pixel der
            }
            else{
                kernel[5]=0;
            }


            if(iancho+4+2<total){
                kernel[6]=Color.rgb(bytes[iancho-4],bytes[iancho+1-4],bytes[iancho+2-4]);//pi+anchoxel
                kernel[7]=Color.rgb(bytes[iancho],bytes[iancho+1],bytes[iancho+2]);//pi+anchoxel
                kernel[8]=Color.rgb(bytes[iancho+4],bytes[iancho+1+4],bytes[iancho+2+4]);//pi+anchoxel
            }
            else{
                kernel[6]=0;
                kernel[7]=0;
                kernel[8]=0;
            }


            color=kernelH(kernel);
            bytesFinales[i]= (byte)Color.red(color);
            bytesFinales[i+1]=(byte)Color.green(color);
            bytesFinales[i+2]=(byte)Color.blue(color);
        }
        ByteBuffer retBuf = ByteBuffer.wrap(bytesFinales);
        filtrada.copyPixelsFromBuffer(retBuf);
        System.out.println("Aplicado hotline");
        setImagen(filtrada);
    }

    //kernel de 3x3
    private int kernelH(int[] colores){
        int[] filtro={0,0,0,0,1,0,0,0,0};
        int rojo=0,verde=0,azul=0;
        for(int i=0;i<9;i++){
            rojo+=Color.red(colores[i])*filtro[i];
            verde+=Color.green(colores[i])*filtro[i];
            azul+=Color.blue(colores[i])*filtro[i];
        }
        return Color.rgb(verde,rojo,azul);
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
        int i;
        int j;
        int isuma;
        int jsuma;
        int sumaCampana;
        double []kernel=crearKernel(40);
        int[] Fragmento = new int[25];
        for (i = 0; i < altura; i++) {
            System.out.println("Aqui: "+String.valueOf(altura-i));
            for (j = 0; j < ancho; j++) {
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
                testSubject.setPixel(j, i, ejecutarKernel(Fragmento,kernel));
            }
        }
        System.out.println("Aplicado gauss");
        setImagen(testSubject);
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
        double rojo = 0;
        double azul = 0;
        double verde = 0;
        int divisor=0;
        int k;
        for (k=0;k<25;k++){
            divisor+=kernel[k];
        }
        for(k=0;k<25;k++){
            rojo += Color.red(kernelE[k])*kernel[k];
        }
        //rojo = rojo / divisor;
        for(k=0;k<25;k++){
            verde += Color.green(kernelE[k])*kernel[k];
        }
        //verde = verde / divisor;
        for(k=0;k<25;k++){
            azul += Color.blue(kernelE[k])*kernel[k];
        }
        //azul= azul / divisor;

        int red=(int)rojo;
        int green= (int)verde;
        int blue=(int)azul;
        return Color.rgb(red, green, blue);
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