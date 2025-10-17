// TeacherAgent.java (with debug prints)
package com.mycompany.quizsystem;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
                        int points = "5".equals(ans) ? 1 : 0;
                        int newScore = scores.getOrDefault(studentName, 0) + points;
                        scores.put(studentName, newScore);
                        answers.put(studentName, ans);

                        // Send result
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        String res = points == 1 ? "Correct" : "Wrong";
                        reply.setContent("RESULT: " + res + " (" + points + " point)");
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

        System.out.println("TeacherAgent setup complete. Ready for students.");
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

    private void updateStudent(String name, String ans, int score) {
        Platform.runLater(() -> gui.updateStudent(name, ans, score));
    }

    // Getter for GUI
    public Map<String, String> getAnswers() { return answers; }
    public Map<String, Integer> getScores() { return scores; }
}