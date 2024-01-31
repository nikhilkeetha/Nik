package com.nik.Activites;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nik.R;
import com.nik.Services.WidgetService;

import org.tensorflow.lite.Tensor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button add;
    TextView tvRe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add=findViewById(R.id.btn_add);
        tvRe=findViewById(R.id.tv_result);

        String apiKey="AIzaSyAahbytvDmUTmxfH7xnbdn68C5IpgZTRhY";

        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ apiKey);
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

    public void text(String result)
    {
        final String resultText = result;

        // Update the TextView on the main (UI) thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvRe.setText(resultText);
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