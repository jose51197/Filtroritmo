package com.example.jose.filtroritmo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button camara=(Button) findViewById(R.id.camara);
        Button galeria=(Button) findViewById(R.id.galeria);


    }
    public void botonCamara(View view){
        //llamo a la camra
    }

    public void botonGaleria(View view){
        //llamo a la galeria
    }
}
