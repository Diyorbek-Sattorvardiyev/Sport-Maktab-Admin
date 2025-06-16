#  Sport-Maktab-Admin Ilovasi

**Sport-Maktab-Admin** — bu sport maktabi ma'murlari uchun ishlab chiqilgan Android ilova bo‘lib, talabalar, murabbiylar, sport turlari, yangiliklar, natijalar, slayderlar va mashg‘ulotlar jadvalini boshqarish imkoniyatini beradi.

---



##  Asosiy funksiyalar

-  Talabalarni qo‘shish, tahrirlash, o‘chirish
-  Murabbiylarni boshqarish
-  Sport turlari bilan ishlash (rasm yuklash bilan)
-  Yangiliklar joylash va tahrirlash
- Slayderlar boshqaruvi
-  Mashg‘ulotlar jadvalini tuzish
-  Musobaqa natijalarini qo‘shish va tahrirlash
-  Parolni yangilash, profilni ko‘rish

---

##  Texnologiyalar

- **Android (Java)** – ilovani yaratish
- **Retrofit2** – REST API bilan ishlash
- **Multipart file upload** – rasm va fayllarni serverga yuborish
- **JWT token autentifikatsiyasi** – barcha so‘rovlarda xavfsizlik

---

##  Muhim API endpointlar (`ApiService`)

###  Autentifikatsiya:
```java
@POST("login")
Call<LoginResponse> login(...);

@GET("profile")
Call<Map<String, Object>> getProfile(...);
```

###  Talabalar:
```java
@GET("students")
@POST("students")
@PUT("students/{id}")
@DELETE("students/{id}")
```

###  Murabbiylar:
```java
@GET("coaches")
@POST("coaches")
@PUT("coaches/{id}")
@DELETE("coaches/{id}")
```

###  Sport turlari (rasm bilan):
```java
@Multipart @POST("sport-types")
@Multipart @PUT("sport-types/{id}")
@DELETE("sport-types/{id}")
```

###  Yangiliklar:
```java
@GET("news")
@Multipart @POST("news")
@Multipart @PUT("news/{id}")
@DELETE("news/{id}")
```

###  Slayderlar:
```java
@GET("sliders")
@Multipart @POST("sliders")
@Multipart @PUT("sliders/{id}")
@DELETE("sliders/{id}")
```

###  Natijalar:
```java
@GET("results")
@Multipart @POST("results")
@Multipart @PUT("results/{id}")
@DELETE("results/{id}")
```

###  Mashg‘ulot jadvali:
```java
@GET("training-schedule")
@POST("training-schedule")
@PUT("training-schedule/{id}")
@DELETE("training-schedule/{id}")
```

###  Parolni yangilash:
```java
@PUT("profile/update-password")
Call<Map<String, String>> updatePassword(...);
```

---


