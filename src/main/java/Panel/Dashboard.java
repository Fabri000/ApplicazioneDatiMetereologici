package Panel;
import Buttons.DashboardButton;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JPanel {
    private JButton d1= new DashboardButton("Rapporti giornalieri");
    private JButton d2 = new DashboardButton("Rapporti orari");
    private JButton d3 = new DashboardButton("Rapporti mensili");
    private JButton d4 = new DashboardButton("Informazioni meteorologiche");
    private JButton d5 = new DashboardButton("Rapporti sull'affidabilità delle stazioni");
    private JButton d6 = new DashboardButton("Rapporto sulle precipitazioni");

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
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new HourlyMeasurePanel());
            }
            else if(e.getSource() == d3){
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new MonthlyMeasurePanel());
            }
            else if(e.getSource() == d4){
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new WeatherInfoPanel());
            }
            else if(e.getSource() == d5){
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new ReliabilityPanel());
            }
            else{
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new PrecipitationPanel());
            }
        }
    }

}

