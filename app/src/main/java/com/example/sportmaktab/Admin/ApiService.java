package com.example.sportmaktab.Admin;



import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {
    // Authentication
    @POST("login")
    Call<LoginResponse> login(@Body Map<String, String> loginData);

    // Students management
    @GET("students")
    Call<List<Student>> getStudents(@Header("Authorization") String token, @Query("search") String search);

    @POST("students")
    Call<ResponseBody> addStudent(@Header("Authorization") String token, @Body Map<String, String> studentData);

    @PUT("students/{id}")
    Call<Map<String, String>> updateStudent(@Header("Authorization") String token, @Path("id") int id, @Body Map<String, String> studentData);

    @DELETE("students/{id}")
    Call<ResponseBody> deleteStudent(@Header("Authorization") String token, @Path("id") int id);

    // Coaches management
    @GET("coaches")
    Call<List<Coach>> getCoaches(@Header("Authorization") String token, @Query("search") String search);

    @POST("coaches")
    Call<Map<String, Object>> addCoach(@Header("Authorization") String token, @Body Map<String, String> coachData);

    @PUT("coaches/{id}")
    Call<Map<String, String>> updateCoach(@Header("Authorization") String token, @Path("id") int id, @Body Map<String, String> coachData);

    @DELETE("coaches/{id}")
    Call<Map<String, String>> deleteCoach(@Header("Authorization") String token, @Path("id") int id);

    // Sport Types management
    @GET("sport-types")
    Call<List<SportType>> getSportTypes(@Header("Authorization") String token);

    @Multipart
    @POST("sport-types")
    Call<Map<String, Object>> addSportType(
            @Header("Authorization") String token,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("sport-types/{id}")
    Call<Map<String, String>> updateSportType(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @DELETE("sport-types/{id}")
    Call<Map<String, String>> deleteSportType(@Header("Authorization") String token, @Path("id") int id);

    // News management
    @GET("news")
    Call<List<News>> getNews(@Header("Authorization") String token);

    @Multipart
    @POST("news")
    Call<Map<String, Object>> addNews(
            @Header("Authorization") String token,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("date") RequestBody date,
            @Part List<MultipartBody.Part> images
    );

    @Multipart
    @PUT("news/{id}")
    Call<Map<String, String>> updateNews(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("date") RequestBody date,
            @Part("replace_images") RequestBody replaceImages,
            @Part List<MultipartBody.Part> images
    );

    @DELETE("news/{id}")
    Call<Map<String, String>> deleteNews(@Header("Authorization") String token, @Path("id") int id);

    // Sliders management
    @GET("sliders")
    Call<List<Slider>> getSliders(@Header("Authorization") String token);

    @Multipart
    @POST("sliders")
    Call<Map<String, Object>> addSlider(
            @Header("Authorization") String token,
            @Part("school_name") RequestBody schoolName,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("sliders/{id}")
    Call<Map<String, String>> updateSlider(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Part("school_name") RequestBody schoolName,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @DELETE("sliders/{id}")
    Call<Map<String, String>> deleteSlider(@Header("Authorization") String token, @Path("id") int id);

    // Training Schedule management
    @GET("training-schedule")
    Call<List<TrainingSchedule>> getTrainingSchedule(@Header("Authorization") String token);

    @POST("training-schedule")
    Call<Map<String, Object>> addTrainingSchedule(@Header("Authorization") String token, @Body Map<String, Object> scheduleData);

    @PUT("training-schedule/{id}")
    Call<Map<String, String>> updateTrainingSchedule(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Map<String, Object> scheduleData
    );

    @DELETE("training-schedule/{id}")
    Call<Map<String, String>> deleteTrainingSchedule(@Header("Authorization") String token, @Path("id") int id);

    // Results management
    @GET("results")
    Call<List<Result>> getResults(@Header("Authorization") String token);

    @Multipart
    @POST("results")
    Call<Map<String, Object>> addResult(
            @Header("Authorization") String token,
            @Part("competition_name") RequestBody competitionName,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("results/{id}")
    Call<Map<String, String>> updateResult(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Part("competition_name") RequestBody competitionName,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @DELETE("results/{id}")
    Call<Map<String, String>> deleteResult(@Header("Authorization") String token, @Path("id") int id);

    // Profile management
    @GET("profile")
    Call<Map<String, Object>> getProfile(@Header("Authorization") String token);

    @PUT("profile/update-password")
    Call<Map<String, String>> updatePassword(
            @Header("Authorization") String token,
            @Body Map<String, String> passwordData
    );
}