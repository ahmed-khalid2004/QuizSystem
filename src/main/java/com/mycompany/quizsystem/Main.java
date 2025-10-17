package com.mycompany.quizsystem;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.embed.swing.JFXPanel;

public class Main {
    public static void main(String[] args) {
       
        new JFXPanel();

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        try {
            AgentContainer mc = rt.createMainContainer(p);
            
           
            AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            rma.start();
            
           
            AgentController teacher = mc.createNewAgent("teacher", TeacherAgent.class.getName(), new Object[0]);
            teacher.start();
            
          
            for (int i = 1; i <= 2; i++) {
                AgentController student = mc.createNewAgent("student" + i, StudentAgent.class.getName(), new Object[0]);
                student.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}