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
            bytes[i]= color;
            bytes[i+1]=color;
            bytes[i+2]=color;
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
        for (int i = 0; i < bytes.length; i+=4) {
            //bytes[i+1] & 0xFF
            //if no es primera fila
            if(i-ancho-2>0){//ya se que agarra pixeles incorrectos a veces, me vale xD es parte del filtro
                kernel[0]=Color.rgb(bytes[i-ancho-4],bytes[i-ancho+1-4],bytes[i-ancho+2-4]);//pi-anchoxel
                kernel[1]=Color.rgb(bytes[i-ancho],bytes[i-ancho+1],bytes[i-ancho+2]);//pi-anchoxel
                kernel[2]=Color.rgb(bytes[i-ancho+4],bytes[i-ancho+1+4],bytes[i-ancho+2+4]);//pi-anchoxel
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

            kernel[4]=Color.rgb(bytes[i],bytes[i+1],bytes[i+2]);//pixel actual
            if(i+2+4<total){
                kernel[5]=Color.rgb(bytes[i+4],bytes[i+1+4],bytes[i+2+4]);//pixel der
            }
            else{
                kernel[5]=0;
            }


            if(i+ancho+4+2<total){
                kernel[6]=Color.rgb(bytes[i+ancho-4],bytes[i+ancho+1-4],bytes[i+ancho+2-4]);//pi+anchoxel
                kernel[7]=Color.rgb(bytes[i+ancho],bytes[i+ancho+1],bytes[i+ancho+2]);//pi+anchoxel
                kernel[8]=Color.rgb(bytes[i+ancho+4],bytes[i+ancho+1+4],bytes[i+ancho+2+4]);//pi+anchoxel
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
        System.out.println("Aplicado desaturation");
        setImagen(filtrada);
    }

    //kernel de 3x3
    private int kernelH(int[] colores){
        int[] filtro={1,1,1,1,-1,-1,-1,-1,-1};
        int rojo=0,verde=0,azul=0;
        for(int i=0;i<9;i++){
            rojo+=Color.red(colores[i])*filtro[i];
            verde+=Color.green(colores[i])*filtro[i];
            azul+=Color.blue(colores[i])*filtro[i];
        }
        return Color.rgb(rojo,verde,azul);
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
        Bitmap testSubjectAlter = this.imagen.copy(this.imagen.getConfig(), true);
        int altura = testSubjectAlter.getHeight();
        int ancho = testSubjectAlter.getWidth();
        int i;
        int j;
        int isuma;
        int jsuma;
        int sumaCampana;
        int[] kernel = new int[9];
        for (i = 0; i < altura; i++) {
            for (j = 0; j < ancho; j++) {
                jsuma = -2;
                isuma = -1;
                for (int q = 0; q < 9; q++) {
                    jsuma++;
                    if (jsuma == 2) {
                        jsuma = -1;
                        isuma++;
                    }
                    try {
                        kernel[q] = this.imagen.getPixel(j + jsuma, i + isuma);
                    } catch (Exception e) {
                        kernel[q] = 0;
                    }
                }
                testSubjectAlter.setPixel(j, i, ejecutarKernel(kernel));
            }
        }
        System.out.println("Aplicado gauss");
        setImagen(testSubjectAlter);
    }
    //retorna el color del pixel al aplicar gauss
    private int ejecutarKernel(int[] kernel) {
        int rojo = 0;
        int azul = 0;
        int verde = 0;
        int[] kernelProcedure = new int[9];//lista donde termina resultado de cada pixel
        kernelProcedure[0] = Color.red(kernel[0])<<2;
        kernelProcedure[1] = Color.red(kernel[1])<<3;
        kernelProcedure[2] = Color.red(kernel[2])<<2;
        kernelProcedure[3] = Color.red(kernel[3]) <<3;
        kernelProcedure[4] = Color.red(kernel[4]) <<4;
        kernelProcedure[5] = Color.red(kernel[5]) <<3;
        kernelProcedure[6] = Color.red(kernel[6])<<2;
        kernelProcedure[7] = Color.red(kernel[7]) <<3;
        kernelProcedure[8] = Color.red(kernel[8])<<2;
        for (int i = 0; i < 9; i++) {
            rojo += kernelProcedure[i];
        }
        rojo = rojo >>6;
        kernelProcedure[0] = Color.green(kernel[0])<<2;
        kernelProcedure[1] = Color.green(kernel[1]) <<3;
        kernelProcedure[2] = Color.green(kernel[2])<<2;
        kernelProcedure[3] = Color.green(kernel[3]) <<3;
        kernelProcedure[4] = Color.green(kernel[4]) <<4;
        kernelProcedure[5] = Color.green(kernel[5]) <<3;
        kernelProcedure[6] = Color.green(kernel[6])<<2;
        kernelProcedure[7] = Color.green(kernel[7]) <<3;
        kernelProcedure[8] = Color.green(kernel[8])<<2;
        for (int i = 0; i < 9; i++) {
            verde += kernelProcedure[i];
        }
        verde = verde >>6;
        kernelProcedure[0] = Color.blue(kernel[0])<<2;
        kernelProcedure[1] = Color.blue(kernel[1]) <<3;
        kernelProcedure[2] = Color.blue(kernel[2])<<2;
        kernelProcedure[3] = Color.blue(kernel[3]) <<3;
        kernelProcedure[4] = Color.blue(kernel[4]) <<4;
        kernelProcedure[5] = Color.blue(kernel[5]) <<3;
        kernelProcedure[6] = Color.blue(kernel[6])<<2;
        kernelProcedure[7] = Color.blue(kernel[7]) <<3;
        kernelProcedure[8] = Color.blue(kernel[8])<<2;
        for (int i = 0; i < 9; i++) {
            azul += kernelProcedure[i];
        }
        azul = azul >>6;
        return Color.rgb(rojo, verde, azul);
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