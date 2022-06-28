# BankomSlikeArtikli
Za pravilno funkcionisanje aplikacije je neophodno u folderu Server Side/SQL_upiti pronaci upite i izvrstiti ih redom, od kreiranja baze, tabela i pregleda.

U folderu Server Side/BankomSlikeArtikli imamo file koji se zove appsettings.json i appsettings.Development.json koji sadrze konekcione stringove za bazu i lokacija skladista.

U DbConnectionString treba da definisemo IP adresu racunara na kom je podignut sql server, kao i ime korisnika i password.

U StorageLocation treba da definisemo putanju gde ce se skladistiti slike.

Na serverskoj strani u Properties > launchSettings.json neophodno je da izmenimo liniju:

"applicationUrl": "https://localhost:5001;http://localhost:5000;https://192.168.0.23:6000;http://192.168.0.23:6001;",

Treba da definisemo nasu IP adresu racunara na kom pokrecemo serversku stranu.

Na klijentskoj strani u RetrofitClientInstance imamo liniju:

private static final String BASE_URL_API = "https://192.168.0.23:6000/";

Treba da definisemo IP adresu racunara na kom pokrecemo serversku stranu.

Kada registrujete prvog korisnika neophodno je da u bazi promenite njegovu rolu na Admin kako bi mogli da otkljucate sve funkcionalnosti aplikacije.
