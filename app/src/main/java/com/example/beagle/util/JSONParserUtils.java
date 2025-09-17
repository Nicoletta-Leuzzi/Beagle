package com.example.beagle.util;

import android.content.Context;

import com.example.beagle.model.ChatCompletionResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONParserUtils {

    public Context context;

    public JSONParserUtils(Context context) {
        this.context = context;
    }

    /**
     * Parses a JSON file from the assets folder into a {@link ChatCompletionResponse} object
     * using Gson.
     *
     * @param filename The name of the JSON file located in the assets folder.
     * @return A {@link ChatCompletionResponse} object populated with data from the JSON file.
     * @throws IOException if an error occurs while opening or reading the file from the assets.
     */
    public ChatCompletionResponse parseJSONResponseWithGson(String filename) throws IOException {
        InputStream inputStream = context.getAssets().open(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new Gson().fromJson(bufferedReader, ChatCompletionResponse.class);
    }

}
