package com.example.michelle.driverbuddy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    //String BASE_URL= "http://10.0.2.2:3000/";
    String BASE_URL= "https://driverbuddy.herokuapp.com/";


    @POST("driverRegister")
    Call<Driver> createAccount(@Body Driver driver);

    @POST("userRegister")
    Call<User> createProfile(@Body User user);

    @POST("userAccountType")
    Call<User> getProfileType(@Body User user);

    @POST("userLogin")
    Call<Driver> loginDriver(@Body User user);

    @POST("userLogin")
    Call<Police> loginPolice(@Body User user);

    @POST("userLogin")
    Call<Insurance> loginInsurance(@Body User user);

    @POST("/secure-api/driverEdit")
    Call<Driver> editDriverProfile(@Header("authorization")String token,@Body Driver driver);

    @POST("/secure-api/policeEdit")
    Call<Police> editPoliceProfile(@Header("authorization") String token,@Body Police police);

    @POST("/secure-api/insuranceEdit")
    Call<Insurance> editInsuranceProfile(@Header("authorization") String token,@Body Insurance insurance);

    @POST("/secure-api/createFineTicket")
    Call<FineTicket> createFineTicket(@Header("authorization")String token,@Body FineTicket fineTicket);

    @GET("/secure-api/checkLicense")
    Call<Driver>checkLicense(@Header("authorization")String token,@Query("license") String licenseNumber);

    @GET("/secure-api/getTicketDriver")
    Call<ArrayList<FineTicket>> getFineTicketDriver(@Header("authorization") String token,@Query("nic") String nic);

    @GET("/secure-api/getTicketPolice")
    Call<ArrayList<FineTicket>> getFineTicketPolice(@Header("authorization") String token,@Query("policeId") String policeId);

    @GET("/secure-api/getDriver")
    Call<Driver> getDriver (@Header("authorization")String token,@Query("nic") String nic);

    @POST("/secure-api/enterAccidentReport")
    Call<AccidentReport> enterAccidentReport(@Header("authorization") String token,@Body AccidentReport accidentReport);

    @GET("/secure-api/viewAccidentReport")
    Call<AccidentReport> viewAccidentReport(@Header("authorization") String token,@Query("nic") String nic,@Query("agentId") String agentId);

    @GET("checkUser")
    Call<User> checkExisitingUser (@Query("userId") String userI);

    @GET("viewRecentFineTicket")
    Call<FineTicket> viewRecentUnpaidFineTicket(@Query("nic") String nic);

    @GET("updatePaidStatus")
    Call<FineTicket> updatepaidstatus(@Query("id") String id);

    @GET("sendEmailToDriverPaymentStatus")
    Call<Email> sendmail(@Query("email") String email,@Query("dname") String dname,@Query("offence") String offence, @Query("amount") double amount);

    @GET("getCurrentMonthTickets")
    Call<ArrayList<FineTicket>> getCurrentMonthlyTickets(@Query("nic") String nic);

    @GET("getCurrentlyIssuedTickets")
    Call<ArrayList<FineTicket>> getCurrentlyIssuedTickets(@Query("policeId")String policeId);

    @GET("getCurrentlyIssuedReports")
    Call<ArrayList<AccidentReport>> getCurrentlyIssuedReports(@Query("agentId") String agentId);

}