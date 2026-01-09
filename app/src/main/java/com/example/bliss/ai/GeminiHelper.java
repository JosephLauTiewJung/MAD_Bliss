package com.example.bliss.ai;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ServerException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.bliss.BuildConfig;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiHelper {
    // API Key is now loaded from BuildConfig.GEMINI_API_KEY (which gets it from local.properties)
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final int MAX_RETRIES = 3;

    public GeminiHelper() {
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("null")) {
            android.util.Log.e("GeminiHelper", "API Key is MISSING! Check local.properties and build.");
        } else {
            android.util.Log.d("GeminiHelper", "API Key loaded. Length: " + API_KEY.length());
        }

        // Using the requested model
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        this.model = GenerativeModelFutures.from(gm);
    }

    public void analyzeJournalEntry(String title, String content, AnalysisCallback callback) {
        String prompt = "Analyze the following journal entry. " +
                "Identify the mood (e.g., Happy, Sad, Anxious, Neutral) and suggest a helpful activity or advice. " +
                "Format the response as: Mood: [Mood]\nSuggestion: [Suggestion]\n\n" +
                "Title: " + title + "\n" +
                "Content: " + content;

        android.util.Log.d("GeminiHelper", "Sending prompt to Gemini. Prompt length=" + prompt.length());

        Content userContent = new Content.Builder()
                .addText(prompt)
                .build();

        generateWithRetry(userContent, MAX_RETRIES, callback);
    }

    private void generateWithRetry(Content content, int retriesLeft, AnalysisCallback callback) {
        final ListenableFuture<GenerateContentResponse> response;
        try {
            response = model.generateContent(content);
        } catch (Throwable t) {
            android.util.Log.e("GeminiHelper", "Failed to start generateContent", t);
            callback.onAnalysisFailure(t);
            return;
        }

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result == null) {
                    android.util.Log.e("GeminiHelper", "GenerateContentResponse is null");
                    callback.onAnalysisFailure(new NullPointerException("GenerateContentResponse is null"));
                    return;
                }

                String text = result.getText();
                if (text == null || text.trim().isEmpty()) {
                    android.util.Log.w("GeminiHelper", "Empty text returned from Gemini. Full response: " + result);
                    callback.onAnalysisFailure(new IllegalStateException("Empty response from Gemini"));
                    return;
                }

                callback.onAnalysisSuccess(text);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof ServerException && retriesLeft > 0) {
                    android.util.Log.w("GeminiHelper", "Server error, retrying... (" + retriesLeft + " retries left)", t);
                    // Exponential backoff
                    long delay = (long) (Math.pow(2, MAX_RETRIES - retriesLeft) * 1000);
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        generateWithRetry(content, retriesLeft - 1, callback);
                    }, delay);
                } else {
                    android.util.Log.e("GeminiHelper", "Generation failed", t);
                    callback.onAnalysisFailure(t);
                }
            }
        }, executor);
    }


    public interface AnalysisCallback {
        void onAnalysisSuccess(String result);
        void onAnalysisFailure(Throwable t);
    }
}
