package com.mar.starwars;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class PadActivity extends AppCompatActivity {

    // Liste des objets
    ImageView asteroid1;
    ImageView asteroid2;
    ImageView asteroid3;
    ImageView asteroid4;
    ImageView padCenter;
    ImageView tie; // -> Le vaisseau spatial
    ImageView collision; // -> L'animation d'explosion
    int width; // -> largeur de l'écran
    int height; // -> hauteur de l'écran
    boolean isPressed = false; // -> le pad est appuyé
    private float xCoOrdinate, yCoOrdinate; // coordonées du pad


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
        setContentView(R.layout.activity_pad);
        asteroid1 = findViewById(R.id.asteroid_1);
        asteroid2 = findViewById(R.id.asteroid_2);
        asteroid3 = findViewById(R.id.asteroid_3);
        asteroid4 = findViewById(R.id.asteroid_4);
        padCenter = findViewById(R.id.pad_center);
        tie = findViewById(R.id.tie);
        collision = findViewById(R.id.collision);

        // Calcul de la taille de l'écran en pixels afin de s'adapter à tous les téléphones
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

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

        // Calcul de la position initiale du pad par rapport à l'écran entier
        final float[] init_positionX = new float[1];
        final float[] init_positionY = new float[1];
        padCenter.post(() -> {
            init_positionX[0] = padCenter.getX();
            init_positionY[0] = padCenter.getY();
        });

        // Création du thread de déplacement du vaisseau
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


        // Gestion des interactions avec le bouton pad
        padCenter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        // calcul des coordonées sur l'écran
                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // animation du déplacement du pad
                        v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                        // déplacement du vaisseau
                        moveShip.run();
                        break;

                    case MotionEvent.ACTION_UP:
                        isPressed = false;
                        // garantit que le pad revient à sa position initale de manière fluide
                        v.animate().x(init_positionX[0]).y(init_positionY[0]).start();
                        break;
                    default:
                        return false;
                }

                // détection des collisions
                if (sontEnCollision(asteroid1, tie) || sontEnCollision(asteroid2, tie)
                        || sontEnCollision(asteroid3, tie) || sontEnCollision(asteroid4, tie)) {
                    // on immobilise le pad et le vaisseau
                    padCenter.setAnimation(null);
                    tie.setAnimation(null);
                    // on anime l'explosion
                    collision.animate().x(tie.getX()).y(tie.getY()).setDuration(10).start();
                    collision.setVisibility(View.VISIBLE);
                    // on affiche la popup
                    showAlertDialog();
                }

                return true;
            }

        });

    }

    // Affichage de la pop-up
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