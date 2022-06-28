package com.example.bankomslikeartikala.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.bankomslikeartikala.R;
import com.example.bankomslikeartikala.customSpinner.CustomSpinnerAdapterArtikli;
import com.example.bankomslikeartikala.interfaces.UploadReceiptService;
import com.example.bankomslikeartikala.models.Artikli;
import com.example.bankomslikeartikala.models.Slike;
import com.example.bankomslikeartikala.retrofit.RetrofitClientInstance;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// Activity nasledjuje AppCompatActivity da bi mogla aplikacija da radi na starijim uredjajima
public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Button mUploadBtn;
    private String imagePath, currentPhotoPath;
    private File file, novaSlika, cameraFileSlika;
    private Uri urifile, cameraFileUri;
    private UploadReceiptService service;
    private SearchableSpinner dropdown;
    private Artikli artikal;
    private Slike tmpSlika;
    private SpinnerAdapter adapter;
    private List<Artikli> artikli;
    private List<Slike> slike;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int RC_TAKE_PHOTO = 1337;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pravimo nas dialog za ucitavanje
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Ucitavanje");
        progressDialog.setMessage("Molim Vas sacekajte...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        new Thread(() -> {
            try {
                // Pravimo instancu Retrofit klijenta za rad sam HTTP zahtevima
                service = RetrofitClientInstance.getRetrofitInstance().create(UploadReceiptService.class);
                progressDialog.incrementProgressBy(25);
                Thread.sleep(500);
                // Dobijanje liste artikala iz baze
                getStratupArtikli();
                progressDialog.incrementProgressBy(25);
                Thread.sleep(500);
                // Dobijanje liste slika iz baze
                getStratupSlike();
                progressDialog.incrementProgressBy(25);
                Thread.sleep(500);
                // Provera da li su dozvole date
                verifyStoragePermissions(this);
                progressDialog.incrementProgressBy(25);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }).start();

        // Uspostavljamo kontrolu nad elementima prikaza
        toolbar = findViewById(R.id.toolbar);
        mImageView = findViewById(R.id.image_view);
        mUploadBtn = findViewById(R.id.upload_image_btn);
        dropdown = findViewById(R.id.artikli_spinner);

        // Proveravamo rolu prijavljenog korisnika
        if(LoginActivity.Constants.user.getRola().toUpperCase().equals("KORISNIK"))
            mUploadBtn.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bankom Artikli");

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        mUploadBtn.setOnClickListener(v -> {
            if (!artikal.equals(null)) {
                if (!imagePath.equals(null)) {
                    uploadPicture(artikal.getId());
                } else {
                    Toast.makeText(getApplicationContext(), "Izaberite Sliku!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Izaberite Artikal", Toast.LENGTH_SHORT).show();
            }

        });


    }

    // Provera da li aplikacija ima dozvolu za rad sa skladistem
    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    // Ukoliko se dobije permission nastavi sa radom
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                //permission was granted
                if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE")) {
                    pickImageFromGallery();
                } else if (permissions[0].equals("android.permission.CAMERA")) {
                    dispatchTakePictureIntent();
                }
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                //permission was denied
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metoda koja nam sluzi za kompresovanje slike
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURICompress(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 1080.0f;
        float maxWidth = 1920.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calcaulateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = imageUri;//getFilename();
        Log.d("Compress", "filename: " + filename);
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    // Racunjanje koliko slika moze da se kompresuje
    public int calcaulateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    // Dobija pravi path od URI file, da bi mogao da ga kompresuje
    private String getRealPathFromURICompress(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    // Ciscenje foldera slikanih slika
    private void deleteCache() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                new File(dir, child).delete();
            }
        }
    }

    // Dodavanje opcija u toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginActivity.Constants.user.getRola().toUpperCase().equals("ADMIN")){
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    // Funkcionalnost opcija u toolbaru
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.CAMERA};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    dispatchTakePictureIntent();
                }
                break;
            case R.id.gallery:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
                break;
            case R.id.users:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Kreiranje slikane slike kao file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Slikanje slike metoda
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraFileSlika = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cameraFileSlika != null) {
            cameraFileUri = FileProvider.getUriForFile(this, "com.example.bankomslikeartikala.fileprovider", cameraFileSlika);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            startActivityForResult(takePictureIntent, RC_TAKE_PHOTO);
        }
    }

    // Da li postoji slika za izabrani artikal
    private void findSlikaInSlike(Artikli artikal) {
        for (Slike slika : slike) {
            if (slika.getArtikalId().equals(artikal.getId())) {
                tmpSlika = slika;
                getSlika(slika.getId());
                return;
            }
        }
        mImageView.setImageResource(0);
    }

    // Updatovanje dropdown liste artikala
    private void updateDropdown() {
        Artikli[] arrayArtikli = new Artikli[artikli.size()];
        artikli.toArray(arrayArtikli);

        adapter = new CustomSpinnerAdapterArtikli(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayArtikli);

        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    artikal = (Artikli) adapter.getItem(position);
                    findSlikaInSlike(artikal);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                artikal = null;
                return;
            }
        });
    }

    // Uploadovanje slike na server
    private void uploadPicture(int artikalId) {
        try {
            compressImage(imagePath);
            file = new File(imagePath);

            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part files = MultipartBody.Part.createFormData("file", file.getName().toLowerCase().replace(' ', '.'), photoContent);

            Call<ResponseBody> call = service.uploadPicture(files, artikalId);
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Slika je uspesno uploadovana!", Toast.LENGTH_SHORT).show();
                        getStratupSlike();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Slika nije uspesno uploadovana!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Toast.makeText(getBaseContext(), "Slika nije uspesno uploadovana!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dobijanje prave putanje slike
    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    // Biranje slike iz galerije
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // Nakon sto se izvrsi biranje slike ili slikanje, sta raditi s tim slikama
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            assert data != null;
            urifile = data.getData();
            imagePath = getRealPathFromUri(urifile);
            mImageView.setImageURI(urifile);
        }
        if (requestCode == RC_TAKE_PHOTO && resultCode == RESULT_OK) {
            //set taken image to image view
            urifile = cameraFileUri;
            imagePath = currentPhotoPath;
            mImageView.setImageURI(urifile);
        }
    }

    // Dobijanje artikala sa servera
    private void getStratupArtikli() {
        Call<List<Artikli>> call = service.getArtikli();

        call.enqueue(new Callback<List<Artikli>>() {
            @Override
            public void onResponse(@NotNull Call<List<Artikli>> call, @NotNull Response<List<Artikli>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Problem sa nabavkom Artikala", Toast.LENGTH_LONG).show();
                    return;
                }
                artikli = response.body();
                updateDropdown();
            }

            @Override
            public void onFailure(@NotNull Call<List<Artikli>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Problem sa nabavkom Artikala", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Dobijanje informacije o slikama sa servera
    private void getStratupSlike() {
        Call<List<Slike>> call = service.getSlike();

        call.enqueue(new Callback<List<Slike>>() {
            @Override
            public void onResponse(@NotNull Call<List<Slike>> call, @NotNull Response<List<Slike>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Problem sa nabavkom Slika", Toast.LENGTH_LONG).show();
                    return;
                }
                slike = response.body();
            }

            @Override
            public void onFailure(@NotNull Call<List<Slike>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Problem sa nabavkom Slika", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Dobijanje slike sa servera
    private void getSlika(int slikaId) {
        Call<ResponseBody> call = service.getSlika(slikaId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                assert response.body() != null;
                boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                if (writtenToDisk) {
                    mImageView.setImageURI(Uri.fromFile(novaSlika));
                } else {
                    Toast.makeText(getApplicationContext(), "Neuspesno dobijanje Slike sa servera", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Problem sa nabavkom Slike sa servera", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Dobijenje slike sa servera
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            novaSlika = new File(getExternalFilesDir(null) + File.separator + tmpSlika.getNaziv());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(novaSlika);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1)
                        break;
                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    // Brisanje cache foldera sa slikama i zatvaranje aplikacije
    @Override
    protected void onDestroy() {
        deleteCache();
        finish();
        super.onDestroy();
    }
}