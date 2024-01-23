package com.nik.Activites;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nik.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button add;
    TextView tvRe;
    EditText et;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add=findViewById(R.id.btn_add);
        tvRe=findViewById(R.id.tv_result);
        et=findViewById(R.id.et_input);


        /*

        String apiKey="AIzaSyAahbytvDmUTmxfH7xnbdn68C5IpgZTRhY";

        GenerativeModel gm = new GenerativeModel("gemini-pro",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey);
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
        /*
        try {
            classifier = new Classifier(getAssets(), "mobilebert.tflite");
            Log.d("MainActivity", "Classifier initialized successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error initializing classifier: " + e.getMessage());
        }

         */

        getPermission();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to add a  widget
                /*
                if (!Settings.canDrawOverlays(MainActivity.this))
                {
                    getPermission();
                }else {
                    Intent intent = new Intent(MainActivity.this, WidgetService.class);
                    startService(intent);
                }

                 */
                if(et.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Enter Text", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    //here run the model
                }
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