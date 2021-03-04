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
import android.widget.RelativeLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class PadActivity extends AppCompatActivity {


    private static final String TAG = "PadActivity";
    ImageView asteroid1;
    ImageView asteroid2;
    ImageView padCenter;
    ImageView tie;
    ImageView collision;
    boolean isPressed;
    //private SensorManager mSensorManager;
    //private Sensor accelerometer;

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
        setContentView(R.layout.activity_pad);

        asteroid1 = findViewById(R.id.asteroid_1);
        asteroid2 = findViewById(R.id.asteroid_2);
        padCenter = findViewById(R.id.pad_center);
        tie = findViewById(R.id.tie);
        collision = findViewById(R.id.collision);
        //tie.setX(500);
        // tie.setY(500);

        Display display = getWindowManager().getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+
        Log.i(TAG, "displayName  = " + displayName);

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i(TAG, "width        = " + width);
        Log.i(TAG, "height       = " + height);

        int[] initPositionPad = new int[2];
        padCenter.getLocationInWindow(initPositionPad);
        System.out.println("pad init x  " + initPositionPad[0]);

        float init_positionX = padCenter.getX();
        float init_positionY = padCenter.getY();

        Handler handlerForShip = new Handler();
        Runnable moveShip = new Runnable() {

            @Override
            public void run() {

                if (padCenter.getX() < init_positionX && padCenter.getY() < init_positionY) {
                    tie.setX(tie.getX() - (init_positionX - padCenter.getX()));
                    tie.setY(tie.getY() + (padCenter.getY() - init_positionY));
                } else if (padCenter.getX() < init_positionX && padCenter.getY() > init_positionY) {
                    tie.setX(tie.getX() - (init_positionX - padCenter.getX()));
                    tie.setY(tie.getY() - (init_positionY - padCenter.getY()));
                } else if (padCenter.getX() > init_positionX && padCenter.getY() < init_positionY) {
                    tie.setX(tie.getX() + (padCenter.getX() - init_positionX));
                    tie.setY(tie.getY() + (padCenter.getY() - init_positionY));
                } else if (padCenter.getX() > init_positionX && padCenter.getY() > init_positionY) {
                    tie.setX(tie.getX() + (padCenter.getX() - init_positionX));
                    tie.setY(tie.getY() - (init_positionY - padCenter.getY()));


                    if ((tie.getX() > width || tie.getY() > height)) {
                        showAlertDialog();
                    }

                    if (isPressed) {
                        handlerForShip.postDelayed(this, 5);
                    }
                }

            }
        };


        padCenter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        padCenter.setX(event.getX());
                        padCenter.setY(event.getY());
                        moveShip.run();
                        if (sontEnCollision(asteroid1, tie)) {
                            collision.setVisibility(View.VISIBLE);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        //
                        isPressed = false;
                        padCenter.setX(init_positionX);
                        padCenter.setY(init_positionY);
                        System.out.println("pas init 2" + init_positionX);


                        break;
                    default:
                        return false;
                }
                return true;
            }

        });

        Path path = new Path();
        path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true);
        ObjectAnimator animatorsOfAsteroid = ObjectAnimator.ofFloat(asteroid1, View.X, View.Y, path);
        animatorsOfAsteroid.setDuration(4000);
        animatorsOfAsteroid.setRepeatCount(Animation.INFINITE);
        animatorsOfAsteroid.start();

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