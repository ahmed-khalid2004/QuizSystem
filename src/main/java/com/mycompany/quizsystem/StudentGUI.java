// ============= 3. StudentGUI.java =============
package com.mycompany.quizsystem;
// StudentGUI.java
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentGUI {
    private Stage stage;
    private StudentAgent agent;
    private Label questionLabel;
    private TextField answerField;
    private Button sendBtn;
    private Label answerLabel;
    private Label resultLabel;

    public StudentGUI(StudentAgent a) {
        agent = a;
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("Student Quiz System");

        questionLabel = new Label("No question received yet.");
        answerField = new TextField();
        answerField.setPromptText("Enter your answer");
        answerField.setDisable(true);
        sendBtn = new Button("Send Answer");
        sendBtn.setDisable(true);
        sendBtn.setOnAction(e -> {
            String ans = answerField.getText().trim();
            if (!ans.isEmpty()) {
                agent.sendAnswer(ans);
            }
        });
        answerLabel = new Label("Answer sent: None");
        resultLabel = new Label("Result: None");

        VBox root = new VBox(10, questionLabel, answerField, sendBtn, answerLabel, resultLabel);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene);
        stage.show();
    }

    public void setQuestion(String q) {
        questionLabel.setText("Question: " + q);
        answerField.setText("");
        answerField.setDisable(false);
        sendBtn.setDisable(false);
        answerLabel.setText("Answer sent: None");
        resultLabel.setText("Result: None");
    }

    public void setSentAnswer(String a) {
        answerLabel.setText("Answer sent: " + a);
        answerField.setDisable(true);
        sendBtn.setDisable(true);
    }

    public void setResult(String r) {
        resultLabel.setText("Result: " + r);
    }
}