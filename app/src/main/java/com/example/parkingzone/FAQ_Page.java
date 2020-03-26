package com.example.parkingzone;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FAQ_Page extends AppCompatActivity {

    ImageView ques_1;
    ImageView ques_2;
    ImageView ques_3;
    ImageView ques_4;
    ImageView ans_1;
    ImageView ans_2;
    ImageView ans_3;
    ImageView ans_4;
    ImageView back;

    TextView ans1;
    TextView ans2;
    TextView ans3;
    TextView ans4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q__page);
        initialise();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FAQ_Page.this, SettingPage.class));
                finish();
            }
        });

        ques_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand(ques_1, ans1, ans_1);
            }
        });

        ques_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand(ques_2, ans2, ans_2);
            }
        });

        ques_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand(ques_3, ans3, ans_3);
            }
        });

        ques_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand(ques_4, ans4, ans_4);
            }
        });


        ans_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(ans_1, ans1, ques_1);
            }
        });
        ans_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(ans_2, ans2, ques_2);
            }
        });
        ans_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(ans_3, ans3, ques_3);
            }
        });
        ans_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(ans_4, ans4, ques_4);
            }
        });
    }

    private void initialise() {
        ques_1 = findViewById(R.id.expand_ans1);
        ques_2 = findViewById(R.id.expand_ans2);
        ques_3 = findViewById(R.id.expand_ans3);
        ques_4 = findViewById(R.id.expand_ans4);

        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);

        ans_1 = findViewById(R.id.collapse_ans1);
        ans_2 = findViewById(R.id.collapse_ans2);
        ans_3 = findViewById(R.id.collapse_ans3);
        ans_4 = findViewById(R.id.collapse_ans4);

        back = findViewById(R.id.back);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));

    }

    private void expand(ImageView expand, TextView ans, ImageView collapse) {
        expand.setVisibility(View.GONE);//image
        collapse.setVisibility(View.VISIBLE);//image
        ans.setVisibility(View.VISIBLE);
    }

    private void collapse(ImageView collapse, TextView ans, ImageView expand) {
        expand.setVisibility(View.VISIBLE);
        collapse.setVisibility(View.GONE);
        ans.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FAQ_Page.this, SettingPage.class));
        finish();
    }
}
