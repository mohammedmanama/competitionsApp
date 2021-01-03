package com.example.hp15_bs.competitionsapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuestionReader {
    CompActivity activity;
    public QuestionReader(CompActivity activity){
        this.activity = activity;
    }
    public List<Question>getQuestions(String fileName) throws IOException {
        InputStream is = activity.getAssets().open(fileName);
        Scanner s = new Scanner(is);
        List<Question> questions = new ArrayList<>();
        while (s.hasNext()){
            String qText = s.nextLine();
            List<String> choices = new ArrayList<>();
            for (int i=0 ;i<3 ;i++){
                choices.add(s.nextLine());
            }
            String correctAnswer = s.nextLine();
            String photoFile = s.nextLine();
            Question q = new Question();
            q.setQuestionText(qText);
            q.setChoices(choices);
            q.setCorrectAnswer(correctAnswer);
            q.setPhotoFile(photoFile);
            questions.add(q);
        }
            return questions;
    }
}
