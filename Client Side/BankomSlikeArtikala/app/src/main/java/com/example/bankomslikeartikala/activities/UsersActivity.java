package com.example.bankomslikeartikala.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.bankomslikeartikala.R;
import com.example.bankomslikeartikala.customSpinner.CustomSpinnerAdapterUsers;
import com.example.bankomslikeartikala.interfaces.UploadReceiptService;
import com.example.bankomslikeartikala.models.User;
import com.example.bankomslikeartikala.retrofit.RetrofitClientInstance;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// Activity nasledjuje AppCompatActivity da bi mogla aplikacija da radi na starijim uredjajima
public class UsersActivity extends AppCompatActivity {

    private UploadReceiptService service;

    private SearchableSpinner dropdown;
    private SpinnerAdapter adapter;
    private EditText edt_username, edt_password;
    private Button btn_save, btn_delete;
    private CheckBox cbx_admin;
    private List<User> users;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Iniciramo klijent Retrofita
        service = RetrofitClientInstance.getRetrofitInstance().create(UploadReceiptService.class);

        // Uspostavljamo kontrolu nad elementima prikaza
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        cbx_admin = (CheckBox) findViewById(R.id.cbx_admin);
        dropdown = findViewById(R.id.users_spinner);

        // Dogadjaji
        getStratupKorisnici();
        btn_delete.setOnClickListener(view -> {
            if(!edt_username.getText().toString().equals("")) {
                brisanjeKorisnika();
                resetForm();
            } else {
                Toast.makeText(getBaseContext(), "Username nije upisan!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_save.setOnClickListener(view -> {
            if(!edt_username.getText().toString().equals("")){
                menjanjeKorisinika();
                resetForm();
            } else {
                Toast.makeText(getBaseContext(), "Username nije upisan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Popunjavamo dropdown listu korisnika i definisemo sta se desava kad izaberemo element
    private void updateDropdown() {
        User[] arrayUsers = new User[users.size()];
        users.toArray(arrayUsers);

        // Ukljucujemo spinner adapter koji smo sami pravili
        adapter = new CustomSpinnerAdapterUsers(UsersActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayUsers);

        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    user = (User) adapter.getItem(position);
                    edt_username.setText(user.getUsername());
                    if(user.getRola().toUpperCase().equals("ADMIN")){
                        cbx_admin.setChecked(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                user = null;
                return;
            }
        });
    }

    // Dobijanje liste korisnika sa servera koje koristimo da bi popunili dropdown listu za spinner
    private void getStratupKorisnici() {
        Call<List<User>> call = service.getAllUsers(LoginActivity.Constants.user.getUsername());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NotNull Call<List<User>> call, @NotNull Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Problem sa nabavkom korisnika", Toast.LENGTH_LONG).show();
                    return;
                }
                users = response.body();
                updateDropdown();
            }

            @Override
            public void onFailure(@NotNull Call<List<User>> call, @NotNull Throwable t) {
                Toast.makeText(getBaseContext(), "Problem sa nabavkom korisnika", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Metoda koja poziva HTTP DELETE za brisanje korisnika
    private void brisanjeKorisnika(){
        Call<ResponseBody> call = service.deleteUser(user.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Uspešno obrisan korisnik!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getBaseContext(), "Neuspelo brisanje korisnika!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Neuspelo brisanje korisnika!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metoda koja poziva HTTP PUT za menjanje korisnika
    private void menjanjeKorisinika(){
        User tempUser;
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();
        if(user.getId()!=0)
             tempUser = user;
        else
            tempUser = new User(username);
        if(!username.equals(user.getUsername()))
            tempUser.setUsername(username);
        if(!password.equals(""))
            tempUser.setPassword(password);
        else
            tempUser.setPassword(null);
        if(cbx_admin.isChecked())
            tempUser.setRola("Admin");
        else
            tempUser.setRola("Korisnik");
        Call<ResponseBody> call = service.changeUser(tempUser);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Uspešno sačuvan korisnik!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getBaseContext(), "Nije uspešno sačuvan korisnik!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getBaseContext(), "Nije uspešno sačuvan korisnik!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metodu koji koristimo da resetujemo formu za unos
    private void resetForm(){
        edt_username.setText("");
        edt_password.setText("");
        users.clear();
        getStratupKorisnici();
    }
}