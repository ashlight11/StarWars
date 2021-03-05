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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class PadActivity extends AppCompatActivity {

    ImageView asteroid1;
    ImageView asteroid2;
    ImageView asteroid3;
    ImageView asteroid4;
    ImageView padCenter;
    ImageView tie;
    ImageView collision;
    boolean isPressed = false;
    private float xCoOrdinate, yCoOrdinate;


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
        setContentView(R.layout.activity_pad);
        asteroid1 = findViewById(R.id.asteroid_1);
        asteroid2 = findViewById(R.id.asteroid_2);
        asteroid3 = findViewById(R.id.asteroid_3);
        asteroid4 = findViewById(R.id.asteroid_4);
        padCenter = findViewById(R.id.pad_center);
        tie = findViewById(R.id.tie);
        collision = findViewById(R.id.collision);


        Display display = getWindowManager().getDefaultDisplay();

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


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

        // Quatrième
        Path path4 = new Path();
        path4.moveTo(800f, 30f);
        path4.quadTo(30f, 200f, 600f, 3000f);
        ObjectAnimator animatorsOfAsteroid4;
        animatorsOfAsteroid4 = ObjectAnimator.ofFloat(asteroid4, View.X, View.Y, path4);
        animatorsOfAsteroid4.setDuration(5000);
        animatorsOfAsteroid4.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid4.start();

        final float[] init_positionX = new float[1];
        final float[] init_positionY = new float[1];

        padCenter.post(() -> {
            init_positionX[0] = padCenter.getX();
            init_positionY[0] = padCenter.getY();
        });

        Handler handlerForShip = new Handler();
        Runnable moveShip = new Runnable() {

            @Override
            public void run() {

                // déplacement vers la gauche et vers le haut
                if (padCenter.getX() < init_positionX[0] && padCenter.getY() < init_positionY[0]) {
                    tie.setX(tie.getX() - (init_positionX[0] - padCenter.getX()) / width);
                    tie.setY(tie.getY() + (padCenter.getY() - init_positionY[0]) / height);


                    // déplacement vers la gauche et vers le bas
                } else if (padCenter.getX() < init_positionX[0] && padCenter.getY() > init_positionY[0]) {
                    tie.setX(tie.getX() - (init_positionX[0] - padCenter.getX()) / width);
                    tie.setY(tie.getY() - (init_positionY[0] - padCenter.getY()) / height);

                    // vers la droite et vers le bas
                } else if (padCenter.getX() > init_positionX[0] && padCenter.getY() < init_positionY[0]) {
                    tie.setX(tie.getX() + (padCenter.getX() - init_positionX[0]) / width);
                    tie.setY(tie.getY() + (padCenter.getY() - init_positionY[0]) / height);

                    // vers la gauche et vers le bas
                } else if (padCenter.getX() > init_positionX[0] && padCenter.getY() > init_positionY[0]) {
                    tie.setX(tie.getX() + (padCenter.getX() - init_positionX[0]) / width);
                    tie.setY(tie.getY() - (init_positionY[0] - padCenter.getY()) / height);
                }

                // si l'on touche un bord, on passe au côté opposé de l'écran
                if (tie.getX() > width - 10) {
                    tie.setX(20);
                }
                if (tie.getX() < 5) {
                    tie.setX(width - 30);
                }
                if (tie.getY() > height - 10) {
                    tie.setY(20);
                }
                if (tie.getY() < 5) {
                    tie.setY(height - 30);
                }

                if (isPressed) {
                    handlerForShip.postDelayed(this, 10);
                }


            }
        };


        padCenter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                        moveShip.run();
                        break;

                    case MotionEvent.ACTION_UP:
                        isPressed = false;
                        v.animate().x(init_positionX[0]).y(init_positionY[0]).start();
                        break;
                    default:
                        return false;
                }

                if (sontEnCollision(asteroid1, tie) || sontEnCollision(asteroid2, tie)
                        || sontEnCollision(asteroid3, tie) || sontEnCollision(asteroid4, tie)) {
                    padCenter.setAnimation(null);
                    tie.setAnimation(null);
                    collision.animate().x(tie.getX()).y(tie.getY()).setDuration(10).start();
                    collision.setVisibility(View.VISIBLE);
                    showAlertDialog();
                }

                return true;
            }

        });

    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vous avez perdu");
        builder.setMessage("Que voulez-vous faire?");
        builder.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(PadActivity.this, PadActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Retour au menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(PadActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}