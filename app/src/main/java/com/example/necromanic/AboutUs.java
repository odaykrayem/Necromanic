package com.example.necromanic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.sshadkany.RectButton;

public class AboutUs extends AppCompatActivity {


    public static final String FOUND_A_BUG = "found a bug !";
    public static final String GOT_AN_IMPROVEMENT = "I've got an Idea for you !";
    final String[] TO = {"digitalminds87@gmail.com"};

    RectButton mContactUsIdea;
    RectButton mContactUsBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mContactUsBug = findViewById(R.id.contact_us_bug_btn);
        mContactUsIdea = findViewById(R.id.contact_us_idea_btn);

        mContactUsBug.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EmailUs(FOUND_A_BUG);
            }
        });

        mContactUsIdea.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EmailUs(GOT_AN_IMPROVEMENT);
            }
        });

    }

    private void EmailUs(String type) {


        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, type);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email app installed.", Toast.LENGTH_SHORT).show();
        }
    }

}