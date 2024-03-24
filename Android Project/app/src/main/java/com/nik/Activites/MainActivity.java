package com.nik.Activites;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nik.Adapters.ChatAdapter;
import com.nik.R;
import com.nik.Services.WidgetService;

import org.tensorflow.lite.Tensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button add;
    TextView tvRe;
    GenerativeModelFutures model;
    GenerativeModel gm;
    EditText editText;
    ProgressBar progressBar;
    ImageView im;
    private RecyclerView recyclerView;
    private List<String> messages;
    private ChatAdapter adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add=findViewById(R.id.btn_add);
        tvRe=findViewById(R.id.tv_result);
        editText = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);
        im = findViewById(R.id.iv_done);

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        tvRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called whenever the text is changed.
                // You can access the updated text using the parameter 's'.
                String newText = s.toString();
                // Do something with the new text...

            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has changed.
                progressBar.setVisibility(View.GONE);
                im.setVisibility(View.VISIBLE);

            }
        });


        String apiKey="AIzaSyAahbytvDmUTmxfH7xnbdn68C5IpgZTRhY";

        /*

        GenerativeModel gm = new GenerativeModel("gemini-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("hi")
                .build();

        // Create an Executor (you might want to customize this based on your needs)
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
                text(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                add.setVisibility(View.GONE);
                t.printStackTrace();
            }
        }, executor);

         */

        gm = new GenerativeModel("gemini-pro", apiKey);
        model = GenerativeModelFutures.from(gm);


        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    editText.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    im.setVisibility(View.GONE);

                    messages.add("INPUT : "+userInput);
                    adapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.smoothScrollToPosition(messages.size() - 1);

                    generateContent(userInput);
                } else {
                    showToast("Please enter a message.");
                }
            }
        });



        getPermission();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Settings.canDrawOverlays(MainActivity.this))
                {
                    getPermission();
                }else {
                    Intent intent = new Intent(MainActivity.this, WidgetService.class);
                    startService(intent);
                }
            }
        });
    }

    private void generateContent(String userInput) {
        Content content = new Content.Builder()
                .addText(userInput)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
                text(resultText);
                Log.d("TAG", "onSuccess: "+resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                showToast("Failed to generate content.");
                t.printStackTrace();
            }
        }, executor);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void text(String result)
    {
        final String resultText = result;

        // Update the TextView on the main (UI) thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                messages.add(resultText.toString());
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.smoothScrollToPosition(messages.size() - 1);

                tvRe.setText(resultText.toString());
                //tvRe.setText(resultText);
            }
        });
    }

    public void getPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(!Settings.canDrawOverlays(MainActivity.this))
            {
                Toast.makeText(this, "user permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

