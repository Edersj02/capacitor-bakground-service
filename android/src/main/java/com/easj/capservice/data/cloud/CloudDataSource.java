package com.easj.capservice.data.cloud;

import android.util.Log;

import com.easj.capservice.entities.SendLocation;
import com.easj.capservice.external.ErrorResponse;
import com.easj.capservice.external.ResponseMessage;
import com.easj.capservice.external.RestService;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudDataSource implements ICloudDataSource {

    private static final String CLASS_NAME = CloudDataSource.class.getName();

    private static CloudDataSource INSTANCE;
    private Retrofit restAdapter;
    private RestService restClient;

    public CloudDataSource(String url) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        restAdapter = new Retrofit.Builder()
                // .baseUrl(RestService.URL)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        restClient = restAdapter.create(RestService.class);
    }

    public static CloudDataSource getInstance(String url) {
        if (INSTANCE == null) {
            INSTANCE = new CloudDataSource(url);
        }
        return INSTANCE;
    }

    @Override
    public void sendLocationTracker(String url, String token, SendLocation sendLocation) {
        Call<ResponseMessage> messageCall = restClient.sendLocationTracker(token, sendLocation);
        messageCall.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    Log.d(CLASS_NAME, "Response: " + response.message());
                    return;
                }
                ResponseBody errorBody = response.errorBody();
                if (errorBody.contentType().subtype().equals("json")) {
                    ErrorResponse errorResponse = ErrorResponse.fromErrorBody(errorBody);
                    Log.d(CLASS_NAME, "Error body: " + errorResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.d(CLASS_NAME, "Error failure: " + t.getMessage());
            }
        });
    }

}
