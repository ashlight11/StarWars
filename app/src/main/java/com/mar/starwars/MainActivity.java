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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Liste des objets
    ImageView asteroid1;
    ImageView asteroid2;
    ImageView asteroid3;
    ImageView asteroid4;
    ImageView ship; // -> Le vaisseau spatial
    ImageView explosion; // -> L'animation d'explosion
    TextView score; // -> Le score du joueur
    int count = 0; // -> Pour le calcul du score

    // Capteur
    private SensorManager mSensorManager;
    private Sensor accelerometer;

    int width; // -> largeur de l'écran
    int height; // -> hauteur de l'écran
    float X, Y; // Coordonées du vaisseau
    float gammaX, gammaY, gammaZ; // -> valeurs de retour de l'acceleromètre
    boolean inPlay; // connaître l'état de jeu

    // Permet de détecter les collisions entre objets, a été modifiée par rapport à celle initialement donnée
    boolean sontEnCollision(ImageView obstacle, ImageView vaisseau) {
        int[] positionObstacle = new int[2];
        int[] positionVaisseau = new int[2];
        int largeurObstacle = obstacle.getMeasuredWidth(), hauteurObstacle = obstacle.getMeasuredHeight();
        int largeurVaisseau = vaisseau.getMeasuredWidth(), hauteurVaisseau = vaisseau.getMeasuredHeight();
        obstacle.getLocationOnScreen(positionObstacle);
        vaisseau.getLocationOnScreen(positionVaisseau);
        // On retourne un rectangle de largeur "largeurObstacle" et de hauteur "hauteurObstacle"
        Rect rectObstacle = new Rect(positionObstacle[0],
                positionObstacle[1],
                positionObstacle[0] + largeurObstacle,
                positionObstacle[1] + hauteurObstacle);
        // On retourne un rectangle de largeur "largeurVaisseau /2" et de hauteur "hauteurVaisseau /2"
        Rect rectVaisseau = new Rect(positionVaisseau[0] + largeurVaisseau / 4,
                positionVaisseau[1] + hauteurVaisseau / 4,
                positionVaisseau[0] + 3 * largeurVaisseau / 4,
                positionVaisseau[1] + 3 * hauteurVaisseau / 4);
        return (rectObstacle.intersect(rectVaisseau));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // identification des élements de la vue
        setContentView(R.layout.activity_main);
        asteroid1 = findViewById(R.id.asteroid1);
        asteroid2 = findViewById(R.id.asteroid2);
        asteroid3 = findViewById(R.id.asteroid3);
        asteroid4 = findViewById(R.id.asteroid4);
        ship = findViewById(R.id.ship);
        explosion = findViewById(R.id.explosion);
        score = findViewById(R.id.score);

        // Calcul de la taille de l'écran en pixels afin de s'adapter à tous les téléphones
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // identification de l'accélèromètre
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        // Timer (donc nouveau thread) pour calculer le score
        // Ici, un point toutes les demi-secondes survécues
        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // on s'assure qu'on joue avant d'incrémenter le score
                        if (inPlay) {
                            score.setText("Score = " + count);
                            count++;
                        }

                    }
                });
            }
        }, 1000, 500);

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

    // Gestion du changement des valeurs de l'acceleromètre
    @Override
    public void onSensorChanged(SensorEvent event) {
        // on joue
        inPlay = true;
        // on prend les coordonées à l'instant
        X = ship.getX();
        Y = ship.getY();

        // on affecte les valeurs de l'acceleromètres à des variables
        gammaX = event.values[0];
        gammaY = event.values[1];
        gammaZ = event.values[2];

        // On déplace le vaisseau en fonction de ces valeurs
        ship.setX(X - gammaX * 10); // on multiplie pour que le déplacement soit plus rapide
        ship.setY(Y + (gammaY - 5) * 10 - (gammaZ - 2) * 10); // gammaZ permet une meilleure jouabilité (plus de sensibilité)

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

        // Détection des collisions
        if (sontEnCollision(asteroid1, ship) || sontEnCollision(asteroid2, ship)
                || sontEnCollision(asteroid3, ship) || sontEnCollision(asteroid4, ship)) {
            ship.setAnimation(null); // on arrête le mouvement
            onPause(); // on met en pause l'accelerometre (évite d'ouvrir des pop-ups sans fin)
            explosion.setVisibility(View.VISIBLE); // on affiche l'explosion
            explosion.animate().x(X).y(Y).setDuration(10).start(); // on l'anime
            showAlertDialog(); // on affiche un message de fin de jeu
        }
    }

    // non implémenté
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // vide
    }

    // lorsque l'on arrête le capteur
    @Override
    protected void onPause() {
        super.onPause();
        inPlay = false; // on ne joue plus
        mSensorManager.unregisterListener(this); // on supprime le capteur du SensorManager
    }

    // Reprise du jeu et des mesures
    @Override
    protected void onResume() {
        super.onResume();
        inPlay = true;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Affichage de la pop-up
    public void showAlertDialog() {
        inPlay = false; // on ne joue plus
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vous avez perdu! Votre score : " + count);
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