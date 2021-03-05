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

    ImageView asteroid1;
    ImageView asteroid2;
    ImageView asteroid3;
    ImageView asteroid4;
    ImageView ship;
    ImageView explosion;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    int width;
    int height;
    float X, Y;
    float gammaX, gammaY, gammaZ;
    boolean inPlay;

    boolean sontEnCollision(ImageView obstacle, ImageView vaisseau) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        int firstWidth = obstacle.getMeasuredWidth(), firstHeight = obstacle.getMeasuredHeight();
        int secondWidth = obstacle.getMeasuredWidth(), secondHeight = obstacle.getMeasuredHeight();
        obstacle.getLocationOnScreen(firstPosition);
        vaisseau.getLocationOnScreen(secondPosition);
        Rect rectObstacle = new Rect(firstPosition[0],
                firstPosition[1],
                firstPosition[0] + firstWidth,
                firstPosition[1] + firstHeight);
        Rect rectVaisseau = new Rect(secondPosition[0] + secondWidth / 4,
                secondPosition[1] + firstHeight / 4,
                secondPosition[0] + 3 * secondWidth / 4,
                secondPosition[1] + 3 * secondHeight / 4);
        return (rectObstacle.intersect(rectVaisseau));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asteroid1 = (ImageView) findViewById(R.id.asteroid1);
        asteroid2 = (ImageView) findViewById(R.id.asteroid2);
        asteroid3 = (ImageView) findViewById(R.id.asteroid3);
        asteroid4 = (ImageView) findViewById(R.id.asteroid4);
        ship = (ImageView) findViewById(R.id.ship);
        explosion = (ImageView) findViewById(R.id.explosion);

        Display display = getWindowManager().getDefaultDisplay();
// display size in pixels
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // identification de l'accélèromètre
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        /* ---------------------------------------------------------------

        Création des trajectoires des astéroïdes

        ------------------------------------------------------------------ */

        // Premier astéroide
        Path path = new Path();
        path.arcTo(0f, 0f, 1000f, height, 270f, -180f, true);
        ObjectAnimator animatorsOfAsteroid = ObjectAnimator.ofFloat(asteroid1, View.X, View.Y, path);
        animatorsOfAsteroid.setDuration(4000);
        animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();

        // Deuxième
        Path path2 = new Path();
        path2.moveTo(600f, height - 70);
        path2.quadTo(width, 3000f, 0f, 20f);
        ObjectAnimator animatorOfAsteroid2 = ObjectAnimator.ofFloat(asteroid2, View.X, View.Y, path2);
        animatorOfAsteroid2.setDuration(8000);
        animatorOfAsteroid2.setRepeatCount(Animation.INFINITE);
        animatorOfAsteroid2.start();

        // Troisième
        Path path3 = new Path();
        path3.cubicTo(1500f, 3000f, 200f, 3000f, 1240f, 1540f);
        ObjectAnimator animatorsOfAsteroid3;
        animatorsOfAsteroid3 = ObjectAnimator.ofFloat(asteroid3, View.X, View.Y, path3);
        animatorsOfAsteroid3.setDuration(7000);
        animatorsOfAsteroid3.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid3.start();

        //Quatrième
        Path path4 = new Path();
        path4.moveTo(800f, 30f);
        path4.quadTo(30f, 200f, 600f, 3000f);
        ObjectAnimator animatorsOfAsteroid4;
        animatorsOfAsteroid4 = ObjectAnimator.ofFloat(asteroid4, View.X, View.Y, path4);
        animatorsOfAsteroid4.setDuration(5000);
        animatorsOfAsteroid4.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid4.start();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        X = ship.getX();
        Y = ship.getY();

        gammaX = event.values[0];
        gammaY = event.values[1];
        gammaZ = event.values[2];
        Log.d("Valeurs accelerometre", gammaX + "," + gammaY + "," + gammaZ);

        ship.animate().x(X -gammaX * 10).y(Y + (gammaY - 5) * 10 - (gammaZ - 5) * 10).setDuration(0).start();

        // si l'on touche un bord, on passe au côté opposé de l'écran
        if (ship.getX() > width - 10) {
            ship.setX(20);
        }
        if (ship.getX() < 5) {
            ship.setX(width - 30);
        }
        if (ship.getY() > height - 10) {
            ship.setY(20);
        }
        if (ship.getY() < 5) {
            ship.setY(height - 30);
        }


        if (sontEnCollision(asteroid1, ship) || sontEnCollision(asteroid2, ship)
        || sontEnCollision(asteroid3, ship) || sontEnCollision(asteroid4, ship)) {
            ship.setAnimation(null);
            onPause();
            explosion.setVisibility(View.VISIBLE);
            explosion.animate().x(X).y(Y).setDuration(10).start();
            showAlertDialog();
        }


    }

    // pas implémenté
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // vide
    }

    @Override
    protected void onPause() {
        super.onPause();
        inPlay = false;
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inPlay = true;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void showAlertDialog() {

        inPlay = false;
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