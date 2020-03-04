package com.easj.capservice.external;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ErrorResponse {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public static ErrorResponse fromErrorBody(ResponseBody errorBody) {
        try {
            ErrorResponse error = new Gson()
                    .fromJson(errorBody.string(), ErrorResponse.class);
            return error;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ErrorResponse();
    }
}
