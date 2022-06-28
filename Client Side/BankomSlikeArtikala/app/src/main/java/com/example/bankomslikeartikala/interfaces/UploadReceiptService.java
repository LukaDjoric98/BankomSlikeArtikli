package com.example.bankomslikeartikala.interfaces;

import com.example.bankomslikeartikala.models.Artikli;
import com.example.bankomslikeartikala.models.Slike;
import com.example.bankomslikeartikala.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UploadReceiptService {

    @GET("api/artikli")     // HTTP GET metod za dobijanje liste artikala
    Call<List<Artikli>> getArtikli();

    @Multipart
    @POST("api/slike/{artikalId}") // HTTP POST metod za otpremanje slike
    Call<ResponseBody> uploadPicture(
            @Part MultipartBody.Part file,
            @Path("artikalId") int artikalId
    );

    @GET("api/slike") // HTTP GET metod za dobijanje liste slika
    Call<List<Slike>> getSlike();

    @Streaming
    @GET("api/slike/{id}") // HTTP GET metod za dobijanje slike
    Call<ResponseBody> getSlika(@Path("id") int slikaId);

    @POST("api/users/register") // HTTP POST metod za registrovanje novog korisnika
    Call<ResponseBody> registerUser(@Body User user);

    @POST("api/users/login") // HTTP POST metod za logovanje
    Call<User> loginUser(@Body User user);

    @GET("api/users/{username}") // HTTP GET metod za dobijanje liste korisnika
    Call<List<User>> getAllUsers(@Path("username") String username);

    @PUT("api/users") // HTTP PUT metod za promenu korisnika
    Call<ResponseBody> changeUser(@Body User user);

    @DELETE("api/users/{id}") // HTTP DELETE metod za brisanje korisnika
    Call<ResponseBody> deleteUser(@Path("id") int id);

}
