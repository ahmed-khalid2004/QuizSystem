// TeacherGUI.java (updated - remove manual button, add reset method)
package com.mycompany.quizsystem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jade.core.AID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherGUI {
    private Stage stage;
    private TeacherAgent agent;
    private ObservableList<StudentRow> students = FXCollections.observableArrayList();
    private Map<String, StudentRow> rowMap = new HashMap<>();

    public TeacherGUI(TeacherAgent a) {
        agent = a;
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("Teacher Quiz System - Automatic");

        TableColumn<StudentRow, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StudentRow, String> ansCol = new TableColumn<>("Answer");
        ansCol.setCellValueFactory(new PropertyValueFactory<>("answer"));

        TableColumn<StudentRow, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableView<StudentRow> table = new TableView<>();
        table.setItems(students);
        table.getColumns().addAll(nameCol, ansCol, scoreCol);

        // No manual send button - automatic

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.show();

        // Initial fetch after a short delay
        Platform.runLater(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            initializeStudents();
        });
    }

    private void initializeStudents() {
        List<AID> studs = agent.getStudents();
        for (AID s : studs) {
            String name = s.getLocalName();
            if (!rowMap.containsKey(name)) {
                int existingScore = agent.getScores().getOrDefault(name, 0);
                StudentRow row = new StudentRow(name, "", existingScore);
                Platform.runLater(() -> {
                    students.add(row);
                    rowMap.put(name, row);
                });
            }
        }
    }

    public void resetAnswers() {
        Platform.runLater(() -> {
            for (StudentRow row : students) {
                row.setAnswer("");
            }
        });
    }

    public void updateStudent(String name, String ans, int score) {
        if (!rowMap.containsKey(name)) {
            StudentRow newRow = new StudentRow(name, ans, score);
            Platform.runLater(() -> {
                students.add(newRow);
                rowMap.put(name, newRow);
            });
            return;
        }

        StudentRow row = rowMap.get(name);
        if (row != null) {
            row.setAnswer(ans);
            row.setScore(score);
        }
    }
}