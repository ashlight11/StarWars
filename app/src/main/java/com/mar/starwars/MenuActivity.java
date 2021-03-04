package com.mar.starwars;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends AppCompatActivity {

    Button boutonPad;
    Button boutonAccel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        boutonPad = (Button)findViewById(R.id.jeuPad);
        boutonPad.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        Intent i = new Intent (MenuActivity.this, PadActivity.class);
                        startActivity(i);
                    }
                }
        );
        boutonAccel = (Button)findViewById(R.id.jeuAccel);
        boutonAccel.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        Intent i = new Intent (MenuActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
        );
    }

}