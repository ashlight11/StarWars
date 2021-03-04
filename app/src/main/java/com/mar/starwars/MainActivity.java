package com.mar.starwars;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private static final String TAG = "MainActivity";
    ImageView asteroid1;
    ImageView ship;
    ImageView explosion;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    int width;
    int height;

    boolean sontEnCollision(ImageView firstView, ImageView secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);
        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + firstView.getMeasuredWidth(), firstPosition[1] + firstView.getMeasuredHeight());
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + secondView.getMeasuredWidth(), secondPosition[1] + secondView.getMeasuredHeight());

        return (rectFirstView.intersect(rectSecondView));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asteroid1 = (ImageView) findViewById(R.id.asteroid1);
        ship = (ImageView) findViewById(R.id.ship);
        explosion = (ImageView) findViewById(R.id.explosion);
        //ship.setX(500);
        // ship.setY(500);

        Display display = getWindowManager().getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+
        Log.i(TAG, "displayName  = " + displayName);

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.i(TAG, "width        = " + width);
        Log.i(TAG, "height       = " + height);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Path path = new Path();
        path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true);
        ObjectAnimator animatorsOfAsteroid = ObjectAnimator.ofFloat(asteroid1, View.X, View.Y, path);
        animatorsOfAsteroid.setDuration(4000);
        animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float X = ship.getX(), Y = ship.getY();
        if (!sontEnCollision(ship, asteroid1)) {
            float gammaX = event.values[0], gammaY = event.values[1], gammaZ = event.values[2];
            Log.d("Valeurs accelerometre", gammaX + "," + gammaY + "," + gammaZ);

            ship.setX(X - gammaX * 8);
            ship.setY(Y - (gammaZ - 4) * 8);

            if ((ship.getX() > width || ship.getY() > height)) {
                showAlertDialog();
            }

        } else {
            explosion.setX(X);
            explosion.setY(Y);
            explosion.setVisibility(View.VISIBLE);
            showAlertDialog();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vous avez perdu");
        builder.setMessage("Que voulez-vous faire?");
        builder.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Retour au menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}