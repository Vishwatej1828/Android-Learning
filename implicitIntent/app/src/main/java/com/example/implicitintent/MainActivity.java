package com.example.implicitintent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText url, emailAdd;
    Button openURLBtn, openEmailBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        url = findViewById(R.id.editTextURL);
        emailAdd = findViewById(R.id.editTextEmailAddress);

        openURLBtn = findViewById(R.id.buttonOpenURL);
        openEmailBtn = findViewById(R.id.buttonOpenEmail);

        openURLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implicit Intent to open a web page
                String urlText = url.getText().toString();

                // Ensure URL is not empty
                if (urlText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ensure URL has a valid scheme
                if (!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
                    urlText = "http://" + urlText; // Add default scheme
                }

                Uri urlEntered = Uri.parse(urlText);
                Log.d("url", "url: " + urlText);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(urlEntered);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
                startActivity(intent);
/*
                if (isBrowserAvailable()) {
                    Intent chooser = Intent.createChooser(intent, "Open URL with");
                    startActivity(chooser);
                } else {
                    Toast.makeText(MainActivity.this, "No browser available to open the URL", Toast.LENGTH_SHORT).show();
                }
*/
            }
/*
            private boolean isBrowserAvailable() {
                Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(testIntent, 0);

                Log.d("BrowserCheck", "Number of browsers found: " + resolveInfos.size());
                for (ResolveInfo resolveInfo : resolveInfos) {
                    Log.d("BrowserCheck", "Browser package: " + resolveInfo.activityInfo.packageName);
                }

                return !resolveInfos.isEmpty();
            }
*/

        });


        openEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implicit Intent to send mail
                String mailText = emailAdd.getText().toString();
                String subject = "Empty Subject";
                Uri attachment = null;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, mailText);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_STREAM, attachment);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "No apps are found to handle the email", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}