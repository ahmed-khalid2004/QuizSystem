package com.mycompany.quizsystem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentRow {
    private final StringProperty name;
    private final StringProperty answer;
    private final IntegerProperty score;

    public StudentRow(String n, String a, int s) {
        this.name = new SimpleStringProperty(n);
        this.answer = new SimpleStringProperty(a);
        this.score = new SimpleIntegerProperty(s);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty answerProperty() {
        return answer;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public String getName() {
        return name.get();
    }

    public String getAnswer() {
        return answer.get();
    }

    public int getScore() {
        return score.get();
    }

    public void setAnswer(String a) {
        answer.set(a);
    }

    public void setScore(int s) {
        score.set(s);
    }
}