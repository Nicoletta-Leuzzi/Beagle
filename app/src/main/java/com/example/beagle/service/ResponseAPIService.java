package com.example.beagle.service;

import com.example.beagle.source.chat.ChatCompletionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ResponseAPIService {
    @POST("chat/completions")
    Call<ChatCompletionResponse> getChatCompletionResponse(
        @Body com.example.beagle.source.chat.ChatCompletionRequest request,
        @Header("Authorization") String apiKey
    );
}
