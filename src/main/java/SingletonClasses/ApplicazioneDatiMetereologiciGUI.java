package SingletonClasses;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import Panel.Dashboard;

public class ApplicazioneDatiMetereologiciGUI extends JFrame {
    private static ApplicazioneDatiMetereologiciGUI instance = null;
    JPanel view;
    private ApplicazioneDatiMetereologiciGUI(){
        setTitle("Applicazione Dati metereologici");
        this.view= new Dashboard();
        this.setContentPane(view);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    public static ApplicazioneDatiMetereologiciGUI  getInstance(){
        if( instance == null){
            instance= new ApplicazioneDatiMetereologiciGUI();
        }
        return  instance;
    }
    public void setView(JPanel view){
        this.remove(this.view);
        this.view=view;
        this.view.setVisible(true);
        this.setContentPane(view);
        repaint();
    }
}
