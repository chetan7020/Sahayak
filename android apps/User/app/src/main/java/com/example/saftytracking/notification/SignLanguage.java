package com.example.saftytracking.notification;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.saftytracking.Dashboard;
import com.example.saftytracking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignLanguage extends AppCompatActivity {
    private static final int VOICE_REQUEST_CODE = 1;

    private EditText textInput;
    private ImageView voiceAssistant;
    private ProgressBar progressBar;
    private boolean isRecording = false; // Track the recording state
    private ImageView sendButton;




    private SpeechRecognizer speechRecognizer;
    //    private EditText textInput;
//    private ImageView voiceAssistant;
//    private Button sendButton;
    private WebView outputWebView;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_language);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = textInput.getText().toString();
                textInput.setText("");
                sendPostRequest(message);
            }
        });


//        textInput = findViewById(R.id.text_input);
//        voiceAssistant = findViewById(R.id.voice_assistant);
//        sendButton = findViewById(R.id.send_button);
//        outputWebView = findViewById(R.id.output_webview);
//
//        // Configure WebView
//        outputWebView.setWebViewClient(new WebViewClient());
//        outputWebView.getSettings().setJavaScriptEnabled(true);
//
//        // Voice Assistant click listener
//        voiceAssistant.setOnClickListener(v -> startVoiceRecognition());
//
//        // Send Button click listener
//        sendButton.setOnClickListener(v -> sendTextToApi());


        WebView webView = findViewById(R.id.output_webview);
        WebSettings settings = webView.getSettings();

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        settings.setJavaScriptEnabled(true); // Enable JavaScript if required
        settings.setAllowFileAccess(true);   // Allow file access
//        settings.setAppCacheEnabled(false); // Disable AppCache


        // Enable JavaScript for the WebView
        WebView myWebView = findViewById(R.id.output_webview);
        myWebView.setWebViewClient(new WebViewClient()); // Ensures the link opens in the WebView
        myWebView.loadUrl("http://ethixlucifer.eastus2.cloudapp.azure.com:5000");
        myWebView.clearCache(true);
        myWebView.clearHistory();


        textInput = findViewById(R.id.text_input);
        voiceAssistant = findViewById(R.id.voice_assistant);
        progressBar = findViewById(R.id.progress_bar);

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Set click listener for the mic icon
        voiceAssistant.setOnClickListener(v -> {
            if (isRecording) {
                // Stop recording
                speechRecognizer.stopListening();
                voiceAssistant.setImageResource(R.drawable.mic_off);  // Change icon to mic off
                progressBar.setVisibility(View.GONE);  // Hide progress bar
            } else {
                // Start recording
                progressBar.setVisibility(View.VISIBLE);  // Show progress bar
                startListening();
                voiceAssistant.setImageResource(R.drawable.mic);  // Change icon to mic on
            }
            isRecording = !isRecording;  // Toggle the recording state
        });

        // Set up the recognition listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    textInput.setText(matches.get(0));  // Set the recognized speech text in the input field
                }
                progressBar.setVisibility(View.GONE); // Hide progress bar once results are received
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                progressBar.setVisibility(View.GONE); // Hide progress bar on error
                Toast.makeText(SignLanguage.this, "Speech recognition failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });




        ImageView notification = findViewById(R.id.notification_icon);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(SignLanguage.this, Notifications.class);
                startActivity(intent);
            }
        });

        LinearLayout signl = findViewById(R.id.signuplanguage);
        signl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(SignLanguage.this, SignLanguage.class);
                startActivity(intent);
            }
        });






    }


    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        speechRecognizer.startListening(intent);
    }

    private void sendMessageToAPI(String message) {


//        sendPostRequest();
//        new SendMessageTask().execute(message)
        ;
    }



//    private class SendMessageTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            progressBar.setVisibility(View.VISIBLE); // Show progress bar
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String message = params[0];
//            try {
//                URL url = new URL("http://ethixlucifer.eastus2.cloudapp.azure.com:5000/trigger");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setDoOutput(true);
//                connection.setRequestProperty("Content-Type", "application/json");
//
//                // Create JSON body
//                JSONObject jsonBody = new JSONObject();
//                jsonBody.put("message", message);
//
//                // Send request body
//                try (OutputStream os = connection.getOutputStream()) {
//                    byte[] input = jsonBody.toString().getBytes("utf-8");
//                    os.write(input, 0, input.length);
//                }
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    return "Message sent successfully!";
//                } else {
//                    return "Failed to send message. Response Code: " + responseCode;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "Error: " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
////            progressBar.setVisibility(View.GONE); // Hide progress bar
//            Toast.makeText(SignLanguage.this, result, Toast.LENGTH_SHORT).show();
//        }
//    }
//


    public void sendPostRequest(String message) {
        // URL of the server endpoint
        String url = "http://ethixlucifer.eastus2.cloudapp.azure.com:5000/trigger";

        // Create the JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        Log.d("VolleyResponse", "Response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("VolleyError", "Error: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Set headers if needed
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}