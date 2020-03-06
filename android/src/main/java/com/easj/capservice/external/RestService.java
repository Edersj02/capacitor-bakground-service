package com.easj.capservice.external;

import com.easj.capservice.entities.SendLocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RestService {

    // String URL = "https://mshuttleapidev.trip2.com/REST/";

    // @POST("Trip2Driver/Location")
    @POST("./")
    Call<ResponseMessage> sendLocationTracker(
            @Header("Authorization") String authorization,
            @Body SendLocation sendLocation
            );

}
