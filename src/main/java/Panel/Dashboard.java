package Panel;
import Buttons.DashboardButton;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JPanel {
    private JButton d1= new DashboardButton("Dayly Reports");
    private JButton d2 = new DashboardButton("Monthly Reports");
    private JButton d3 = new DashboardButton("Precipitation Reports");
    private JButton d4 = new DashboardButton("Wheater Type Reports");
    private JButton d5 = new DashboardButton("Wind Chill Reports");
    private JButton d6 = new DashboardButton("Station Relaiability Reports");

    public Dashboard(){
        this.setSize(new Dimension(1920,1080));
        this.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));
        GridLayout l =new GridLayout(2,3,60,60);
        this.setLayout(l);
        DashboardButtonListener listener=new DashboardButtonListener();
        d1.addActionListener(listener);
        this.add(d1);
        d2.addActionListener(listener);
        this.add(d2);
        d3.addActionListener(listener);
        this.add(d3);
        d4.addActionListener(listener);
        this.add(d4);
        d5.addActionListener(listener);
        this.add(d5);
        d6.addActionListener(listener);
        this.add(d6);
    }
    private class DashboardButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == d1){
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
            }
            else if(e.getSource() == d2){

            }
            else if(e.getSource() == d2){

            }
            else if(e.getSource() == d2){

            }
            else if(e.getSource() == d2){

            }
            else{

            }
        }
    }

}

