package com.example.bankomslikeartikala.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bankomslikeartikala.R;
import com.example.bankomslikeartikala.interfaces.UploadReceiptService;
import com.example.bankomslikeartikala.models.User;
import com.example.bankomslikeartikala.retrofit.RetrofitClientInstance;

import org.jetbrains.annotations.NotNull;

import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// Activity nasledjuje AppCompatActivity da bi mogla aplikacija da radi na starijim uredjajima
public class RegisterActivity extends AppCompatActivity {

    private UploadReceiptService service;

    private EditText edt_username, edt_password;
    private TextView txt_account;
    private Button btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Iniciramo klijent Retrofita
        service = RetrofitClientInstance.getRetrofitInstance().create(UploadReceiptService.class);

        // Uspostavljamo kontrolu nad elementima prikaza
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_create = (Button) findViewById(R.id.btn_create);
        txt_account = (TextView) findViewById(R.id.txt_account);

        // Ako kliknemo na tekst idemo na Register Activity
        txt_account.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
        btn_create.setOnClickListener(view ->{
            // Pravimo novi dialog upozorenja
            AlertDialog dialog = new SpotsDialog.Builder()
                    .setContext(RegisterActivity.this)
                    .build();
            dialog.show();

            // Pravimo objekat Korisnika, sa unetim korisnickim imenom i lozinkom
            User user = new User(edt_username.getText().toString(),
                    edt_password.getText().toString());

            // Pozivamo HTTP POST registerUser(user) iz interfejsa
            Call<ResponseBody> call = service.registerUser(user);
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override // Ukoliko je uspesan odgovor, ispisujemo poruku, vracamo se u login deo programa
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Uspešno registrovanje!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        return;
                    }
                    // Ukoliko nije uspesan odgovor, ispisujemo samo poruku
                    Toast.makeText(getBaseContext(), "Nije uspešno registrovanje!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                @Override // Ukoliko nismo uspeli da uspostavimo kontakt sa serverom, ispisujemo poruku
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Toast.makeText(getBaseContext(), "Nije uspešno registrovanje!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}