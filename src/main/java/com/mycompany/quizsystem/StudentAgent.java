// StudentAgent.java (with debug prints)
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

public class StudentAgent extends Agent {
    private StudentGUI gui;
    private AID teacher;

    protected void setup() {
        System.out.println("StudentAgent " + getLocalName() + " starting up...");

        // Register in DF
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("student");
            sd.setName(getLocalName() + "-student-service");
            dfd.addServices(sd);
            DFService.register(this, dfd);
            System.out.println("Student " + getLocalName() + " registered in DF as 'student' service.");
        } catch (FIPAException fe) {
            System.err.println("Student " + getLocalName() + " DF registration failed: " + fe.getMessage());
            fe.printStackTrace();
        }

        // Search for teacher
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("teacher");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                teacher = result[0].getName();
                System.out.println("Student " + getLocalName() + " found teacher: " + teacher.getLocalName());
            } else {
                System.err.println("Student " + getLocalName() + " could not find teacher in DF!");
            }
        } catch (FIPAException fe) {
            System.err.println("Student " + getLocalName() + " DF search failed: " + fe.getMessage());
            fe.printStackTrace();
        }

        // Launch GUI
        Platform.runLater(() -> {
            gui = new StudentGUI(this);
            gui.show();
            System.out.println("Student " + getLocalName() + " GUI launched.");
        });

        // Add behaviour for receiving messages
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Student " + getLocalName() + " received message from " + msg.getSender().getLocalName() + ": " + msg.getContent());
                    String cont = msg.getContent();
                    if (cont.startsWith("QUESTION:")) {
                        setQuestion(cont.substring(9));
                    } else if (cont.startsWith("RESULT:")) {
                        setResult(cont.substring(7));
                    }
                } else {
                    block();
                }
            }
        });

        System.out.println("StudentAgent " + getLocalName() + " setup complete. Waiting for questions.");
    }

    public void sendAnswer(String ans) {
        if (teacher != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(teacher);
            msg.setContent("ANSWER:" + ans);
            send(msg);
            System.out.println("Student " + getLocalName() + " sent ANSWER: " + ans + " to teacher.");
            Platform.runLater(() -> gui.setSentAnswer(ans));
        } else {
            System.err.println("Student " + getLocalName() + " cannot send answer: no teacher found!");
        }
    }

    public void setQuestion(String q) {
        Platform.runLater(() -> gui.setQuestion(q));
    }

    public void setResult(String r) {
        Platform.runLater(() -> gui.setResult(r));
    }
}