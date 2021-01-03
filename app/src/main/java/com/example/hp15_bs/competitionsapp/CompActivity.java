package com.example.hp15_bs.competitionsapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CompActivity extends AppCompatActivity {

    TextView qTv;
    Button choice1Btn , choice2Btn , choice3Btn;
    ImageView qPhotoIm;
    TextView scoreValueTv;
    int score = 0;
    MediaPlayer successPlayer;
    MediaPlayer failPlayer;
    List<Question> questions;
    Button[] choiceBtns = new Button[3];
    ProgressBar progressBar;
    MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp);

        timer = new MyTimer(10000,1000);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("name")){
            String name = intent.getStringExtra("name");
            TextView nameTv = findViewById(R.id.nameTv);
            nameTv.setText(name);
        }
        successPlayer = MediaPlayer.create(getApplicationContext(),R.raw.clap_sound);
        failPlayer = MediaPlayer.create(getApplicationContext(),R.raw.fail_sound);
        successPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                biuldQuestion();
            }
        });
        failPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                biuldQuestion();
            }
        });


        qTv = findViewById(R.id.guestionTv);
        choice1Btn = findViewById(R.id.choice1Btn);
        choice2Btn = findViewById(R.id.choice2Btn);
        choice3Btn = findViewById(R.id.choice3Btn);
        choiceBtns[0] = choice1Btn;
        choiceBtns[1] = choice2Btn;
        choiceBtns[2] = choice3Btn;
        qPhotoIm = findViewById(R.id.qPhotoIm);
        scoreValueTv = findViewById(R.id.scoreValueTv);
        progressBar = findViewById(R.id.progressBar);

        try {
            QuestionReader qReader = new QuestionReader(this);
            questions = qReader.getQuestions("questions.txt");
            Collections.shuffle(questions);
            biuldQuestion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void biuldQuestion(){
        if(questions.isEmpty()) {
            Intent score = new Intent(getApplicationContext(), ScoreActivity.class);
            score.putExtra("scoreValue", scoreValueTv.getText().toString());
            return;
        }

        for(Button btn:choiceBtns){
            btn.setEnabled(true);
        }

        final Question q = questions.remove(0);
        qTv.setText(q.getQuestionText());
        for(int x=0 ; x<choiceBtns.length ; x++){
            final int y = x;
            choiceBtns[x].setText(q.getChoices().get(x));
            choiceBtns[x].setBackgroundResource(R.drawable.choice_default_shape);
            choiceBtns[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(choiceBtns[y].getText().toString().equalsIgnoreCase(q.getCorrectAnswer())){
                        choiceBtns[y].setBackgroundResource(R.drawable.choice_correct_shape);
                        score+=10;
                        scoreValueTv.setText(score+"");
                        disableButtons();
                        timer.cancel();
                        successPlayer.start();
                    }else{
                        choiceBtns[y].setBackgroundResource(R.drawable.choice_wrong_shape);
                        disableButtons();
                        timer.cancel();
                        failPlayer.start();
                    }
                }
                public void disableButtons() {
                    for (Button btn : choiceBtns) {
                        btn.setEnabled(false);
                    }
                }
            });

        }
        if(q.getPhotoFile().equalsIgnoreCase("no image")){
            qPhotoIm.setImageResource(0);
        }else {
            int dotLoc = q.getPhotoFile().lastIndexOf(".");
            String photoName = q.getPhotoFile().substring(0,dotLoc);
            int id = getResources().getIdentifier(photoName,"drawable",getPackageName());
            qPhotoIm.setImageResource(id);
        }
        timer.start();
    }

    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            //Log.d("ttt",millisUntilFinished+"");
            progressBar.setProgress(Math.round(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            //Log.d("ttt","finished...");
            for (Button btn : choiceBtns)
                btn.setEnabled(false);
                failPlayer.start();
            }
        }
}
