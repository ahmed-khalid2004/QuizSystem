// StudentGUI.java (minor update for "0 points" display)
package com.mycompany.quizsystem;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentGUI {
    private Stage stage;
    private StudentAgent agent;
    private Label questionLabel;
    private Label answerLabel;
    private Label resultLabel;

    public StudentGUI(StudentAgent a) {
        agent = a;
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("Student Quiz System - Automatic");

        questionLabel = new Label("No question received yet.");
        answerLabel = new Label("Answer sent: None");
        resultLabel = new Label("Result: None");

        VBox root = new VBox(10, questionLabel, answerLabel, resultLabel);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    public void setQuestion(String q) {
        Platform.runLater(() -> {
            questionLabel.setText("Question: " + q);
            answerLabel.setText("Answer sent: None");
            resultLabel.setText("Result: None");
        });
    }

    public void setSentAnswer(String a) {
        answerLabel.setText("Answer sent: " + a);
    }

    public void setResult(String r) {
        resultLabel.setText("Result: " + r);
    }
}