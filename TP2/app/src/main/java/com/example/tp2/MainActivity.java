package com.example.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_SECRET = "key_secret";
    private static final String KEY_TRIES = "key_tries";

    private Button btnValider;
    private EditText edtNombreUser;
    private TextView txtTitre;
    private TextView txtNbrEssai;
    private TextView txtResultat;

    private int secretNumber = -1;
    private int tries = 0;
    private final int MAX_TRIES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        btnValider = findViewById(R.id.Btn_valider);
        edtNombreUser = findViewById(R.id.Edt_nombre_user);
        txtTitre = findViewById(R.id.Txt_titre);
        txtNbrEssai = findViewById(R.id.Txt_nbr_essai);
        txtResultat = findViewById(R.id.Txt_resultat);

        // Restaurer état si rotation
        if (savedInstanceState != null) {
            secretNumber = savedInstanceState.getInt(KEY_SECRET, -1);
            tries = savedInstanceState.getInt(KEY_TRIES, 0);
        } else {
            generateNewSecret();
        }

        updateUi();

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nbr = edtNombreUser.getText().toString().trim();

                if (TextUtils.isEmpty(nbr)) {
                    // Message visible à l'utilisateur
                    Toast.makeText(MainActivity.this, "Vous devez saisir un nombre", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vérifier longueur / chiffres : on attend 3 chiffres 100..999
                if (nbr.length() != 3) {
                    Toast.makeText(MainActivity.this, "Veuillez saisir un nombre à 3 chiffres (100..999)", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nbrSaisi;
                try {
                    nbrSaisi = Integer.parseInt(nbr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Format invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nbrSaisi < 100 || nbrSaisi > 999) {
                    Toast.makeText(MainActivity.this, "Veuillez saisir un nombre entre 100 et 999", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vérification du jeu
                if (tries >= MAX_TRIES) {
                    Toast.makeText(MainActivity.this, "Nombre d'essais maximum atteint. Redémarrage du jeu.", Toast.LENGTH_SHORT).show();
                    generateNewSecret();
                    updateUi();
                    return;
                }

                tries++;
                if (nbrSaisi == secretNumber) {
                    txtResultat.setText(String.format(Locale.getDefault(), "Bravo ! Vous avez trouvé %d en %d essai(s).", secretNumber, tries));
                    btnValider.setEnabled(false);
                } else if (nbrSaisi < secretNumber) {
                    txtResultat.setText("C'est plus grand.");
                } else {
                    txtResultat.setText("C'est plus petit.");
                }

                updateUi();

                // Si on a atteint le max d'essais
                if (tries >= MAX_TRIES && nbrSaisi != secretNumber) {
                    txtResultat.setText(String.format(Locale.getDefault(),
                            "Perdu — nombre secret = %d. Cliquez sur Valider pour recommencer.", secretNumber));
                    // Préparer un nouveau jeu : on régénère pour le prochain clic
                    generateNewSecret();
                    tries = 0;
                    updateUi();
                }
            }
        });
    }

    private void generateNewSecret() {
        Random r = new Random();
        secretNumber = 100 + r.nextInt(900); // 100..999
        tries = 0;
    }

    private void updateUi() {
        txtNbrEssai.setText(String.format(Locale.getDefault(), "Tentative %d / %d", Math.max(1, tries == 0 ? 1 : tries), MAX_TRIES));
        // Si on veut remettre le champ vide après validation
        // edtNombreUser.setText("");
        btnValider.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SECRET, secretNumber);
        outState.putInt(KEY_TRIES, tries);
    }
}