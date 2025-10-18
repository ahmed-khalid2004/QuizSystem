// TeacherAgent.java (minor update: fix RESULT for 0 points to say "point" consistently)
package com.mycompany.quizsystem;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.DFService;
import javafx.application.Platform;
import java.util.*;

public class TeacherAgent extends Agent {
    private TeacherGUI gui;
    private Map<String, String> answers = new HashMap<>();
    private Map<String, Integer> scores = new HashMap<>();
    private int questionCounter = 0;

    protected void setup() {
        System.out.println("TeacherAgent starting up...");

        // Register in DF
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("teacher");
            sd.setName(getLocalName() + "-teacher-service");
            dfd.addServices(sd);
            DFService.register(this, dfd);
            System.out.println("Teacher registered in DF as 'teacher' service.");
        } catch (FIPAException fe) {
            System.err.println("Teacher DF registration failed: " + fe.getMessage());
            fe.printStackTrace();
        }

        // Launch GUI
        Platform.runLater(() -> {
            gui = new TeacherGUI(this);
            gui.show();
            System.out.println("Teacher GUI launched.");
        });

        // Add behaviour for receiving answers
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Teacher received message from " + msg.getSender().getLocalName() + ": " + msg.getContent());
                    if (msg.getContent().startsWith("ANSWER:")) {
                        String content = msg.getContent();
                        String ans = content.substring(7).trim();
                        String studentName = msg.getSender().getLocalName();
                        int points = "4".equals(ans) ? 1 : 0;
                        int newScore = scores.getOrDefault(studentName, 0) + points;
                        scores.put(studentName, newScore);
                        answers.put(studentName, ans);

                        // Send result with consistent "point" text
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        String res = points == 1 ? "Correct" : "Wrong";
                        String pointText = points == 1 ? "1 point" : "0 points";
                        reply.setContent("RESULT: " + res + " (" + pointText + ")");
                        send(reply);
                        System.out.println("Teacher sent RESULT to " + studentName + ": " + reply.getContent());

                        // Update GUI
                        System.out.println("Updating score for " + studentName + ": " + newScore);
                        updateStudent(studentName, ans, newScore);
                    }
                } else {
                    block();
                }
            }
        });

        // Automatic question sending: Every 10 seconds
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                questionCounter++;
                System.out.println("Teacher sending question #" + questionCounter + ": 2+2=?");
                sendQuestion();
            }
        });

        System.out.println("TeacherAgent setup complete. Automatic questions will start soon.");
    }

    public List<AID> getStudents() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("student");
        template.addServices(sd);
        try {
            DFAgentDescription[] res = DFService.search(this, template);
            System.out.println("Teacher found " + res.length + " students in DF.");
            List<AID> list = new ArrayList<>();
            for (DFAgentDescription d : res) {
                list.add(d.getName());
                System.out.println(" - Student: " + d.getName().getLocalName());
            }
            return list;
        } catch (FIPAException fe) {
            System.err.println("Teacher DF search failed: " + fe.getMessage());
            fe.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void sendQuestion() {
        List<AID> studs = getStudents();
        if (studs.isEmpty()) {
            System.out.println("No students found yet—skipping question.");
            return;
        }
        for (AID s : studs) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(s);
            msg.setContent("QUESTION: 2+2=?");
            send(msg);
            System.out.println("Question sent to " + s.getLocalName());
        }

        // Reset only answers for new question, keep scores
        answers.clear();
        for (String name : scores.keySet()) {
            answers.put(name, "");
        }
        Platform.runLater(() -> gui.resetAnswers());
    }

    private void updateStudent(String name, String ans, int score) {
        Platform.runLater(() -> gui.updateStudent(name, ans, score));
    }

    // Getter for GUI
    public Map<String, String> getAnswers() { return answers; }
    public Map<String, Integer> getScores() { return scores; }
}