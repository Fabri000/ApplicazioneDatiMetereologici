package Panel;

import DataApi.DataAPI;
import Panel.SubPanel.ReliabilityTableVisualization;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

public class ReliabilityPanel extends JPanel {
    private String measure,set;
    private JCheckBox dayly,hourly,monthly;
    private JComboBox measureselector;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult=new JPanel();
    public ReliabilityPanel(){
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new DataSetSelectionPanel());
        JPanel submitPanel = new JPanel(); submitPanel.setSize( new Dimension(100,20));
        submitButton = new JButton("Cerca");
        submitButton.addActionListener(new submitButtonLister());
        submitButton.setEnabled(false);
        submitPanel.add(submitButton);
        this.add(submitPanel);
        queryResult.setMaximumSize(new Dimension(1000,600));
        queryResult.setVisible(false);
        this.add(queryResult);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        newResearchButton = new JButton("Nuova ricerca");
        newResearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new ReliabilityPanel());
                }
            }
        });
        returnHomeButton=new JButton("Ritorna alla home");
        returnHomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(returnHomeButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new Dashboard());
                }
            }
        });
        buttons.add( newResearchButton );
        buttons.add(Box.createRigidArea(new Dimension(10,0)));
        buttons.add( returnHomeButton);
        this.add(buttons);
        this.add(Box.createRigidArea(new Dimension(40,0)));
    }
    class DataSetSelectionPanel extends JPanel{
        Map<String,String> monthlymeasures= Map.of(
                "Temperatura Massima Media","AvgMaxTemp",
                "Temperatura Minima Media","AvgMinTemp",
                "Temperatura Media","AvgTemp",
                "Pressione al livello del mare massima", "MaxSeaLevelPressure",
                "Pressione al livello del mare minima","MinSeaLevelPressure",
                "Precipitazioni","TotalMonthlyPrecip",
                "Precipitazioni nevose", "TotalSnowfall",
                "Velocità del vento","AvgWindSpeed"
        );
        Map<String,String> hourlymeasures= Map.of(
                "Temperatura","DryBulbCelsius",
                "Visibilità","Visibility",
                "Precipitazioni","HourlyPrecip",
                "Pressione al livello del mare","SeaLevelPressure",
                "Velocità del vento","WindSpeed"
        );
        Map<String,String> daylymeasures= Map.of(
                "Temperatura Massima","Tmax",
                "Temperatura Minima", "Tmin",
                "Temperatura Media", "Tavg",
                "Tramonto","Sunset",
                "Alba","Sunrise",
                "Precipitazioni Nevose","SnowFall",
                "Pressione al livello del mare","SeaLevel",
                "Velocità del vento","AvgSpeed"
        );
        ButtonGroup group = new ButtonGroup();
        public DataSetSelectionPanel(){
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setMaximumSize(new Dimension(920,20));
            this.setBorder(new EmptyBorder(0,0,10,0));
            DataSetSelectionListener listener = new DataSetSelectionListener();
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            p1.add(new JLabel("Seleziona l'arco temporale:"));
            hourly = new JCheckBox("Orarie");
            hourly.addActionListener(listener);
            hourly.setSize(200,20);
            dayly = new JCheckBox("Giornaliero");
            dayly.addActionListener(listener);
            dayly.setSize(200,20);
            monthly = new JCheckBox("Mensile");
            monthly.addActionListener(listener);
            monthly.setSize(200,20);
            group.add(hourly); group.add(dayly); group.add(monthly);
            p1.add(hourly);p1.add(dayly);p1.add(monthly);
            this.add(p1);
            JPanel p2  = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
            p2.add(new JLabel("Seleziona la misura:"));
            measureselector = new JComboBox();
            measureselector.setMaximumSize(new Dimension(300,20));
            measureselector.setEnabled(false);
            measureselector.setSelectedItem( null );
            measureselector.addItemListener(new MeasureSelectionListener());
            p2.add(measureselector);
            this.add(p2);
        }
        class DataSetSelectionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(e.getSource().equals(hourly)){
                    set="hourly";
                    measure=null;
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(hourlymeasures.keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
                else if(e.getSource().equals(dayly)){
                    set="dayly";
                    measure=null;
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(daylymeasures.keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
                else if (e.getSource().equals(monthly)) {
                    set="monthly";
                    measure=null;
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(monthlymeasures.keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
            }
        }

        class MeasureSelectionListener implements  ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(set.equals("hourly")) measure = hourlymeasures.get(val);
                    else if(set.equals("dayly")) measure = daylymeasures.get(val);
                    else if (set.equals("monthly")) measure = monthlymeasures.get(val);
                    submitButton.setEnabled(true);
                }
            }
        }
    }
    class  submitButtonLister implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(measure == null){
                JOptionPane.showMessageDialog(null, "Non hai selezionato la misura");
            }
            else{
                queryResult.add(new ReliabilityTableVisualization(DataAPI.getReliabilityOfStations(set,measure)));
                queryResult.setVisible(true);
            }
        }
    }
}