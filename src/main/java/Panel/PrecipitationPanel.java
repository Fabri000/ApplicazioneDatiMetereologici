package Panel;
import DataApi.GraphCreator;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Panel.SubPanel.PrecipitationTableVisualization;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PrecipitationPanel extends JPanel {
    String datai,dataf,threshold,state, stationName, timezone,zone;
    private QueryType type;
    private JComboBox datain,datafin,thresholdselector;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public PrecipitationPanel(){
        stateselectionbox =new JComboBox<String>(QueryInfo.getInstance().getStates());
        timezoneselectionbox=new JComboBox<String>(QueryInfo.getInstance().getTimezone());
        stationselectionbox=new JComboBox<String>(QueryInfo.getInstance().getStations());
        zoneselectionbox= new JComboBox<String>(QueryInfo.getInstance().getZone());
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new PeriodSelectionPanel());
        this.add(new ThresholdsSelectionPanel());
        this.add(new TypeSelectionPanel());
        this.add(new SelectionQueryParamsPanel());
        JPanel submitPanel = new JPanel(); submitPanel.setSize( new Dimension(100,20));
        submitButton = new JButton("Cerca");
        submitButton.addActionListener(new submitButtonLister());
        submitButton.setEnabled(false);
        submitPanel.add(submitButton);
        this.add(submitPanel);
        queryResult=new JPanel();
        queryResult.setVisible(false);
        this.add(queryResult);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        newResearchButton = new JButton("Nuova ricerca");
        newResearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new PrecipitationPanel());
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
    class ThresholdsSelectionPanel extends JPanel{
        public ThresholdsSelectionPanel(){
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setMaximumSize(new Dimension(800,20));
            this.add(new JLabel("Seleziona il valore minimo di precipitazioni:"));
            this.add(Box.createRigidArea(new Dimension( 20,0)));
            thresholdselector = new JComboBox(QueryInfo.getInstance().getThresholds());
            thresholdselector.setSelectedItem(null);
            thresholdselector.addItemListener(new ThresholdsSelectorListener());
            thresholdselector.setMaximumSize(new Dimension(200,20));
            this.add(thresholdselector);
        }
        class ThresholdsSelectorListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    threshold = thresholdselector.getSelectedItem().toString();
                }
            }
        }
    }
    class PeriodSelectionPanel extends JPanel{
        public PeriodSelectionPanel(){
            PeriodSelectionListener listener = new PeriodSelectionListener();
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setMaximumSize(new Dimension(800,20));
            this.add(new JLabel("Seleziona data iniziale:"));
            this.add(Box.createRigidArea(new Dimension( 20,0)));
            datain = new JComboBox(QueryInfo.getInstance().getDate());
            datain.setSelectedItem(null);
            datain.addItemListener(listener);
            datain.setMaximumSize(new Dimension(200,20));
            this.add(datain);
            this.add(Box.createRigidArea(new Dimension(100,0)));
            this.add(new JLabel("Seleziona data finale:"));
            datafin= new JComboBox(QueryInfo.getInstance().getDate());
            datafin.setSelectedItem(null);
            datafin.addItemListener(listener);
            datafin.setMaximumSize(new Dimension(200,20));
            this.add(datafin);
        }

        class PeriodSelectionListener implements ItemListener{

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()== ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    val=val.replace(":","");
                    if(source.equals(datain)){
                        datai = val;
                    } else if (source.equals(datafin)) {
                        dataf = val;
                    }
                }
            }
        }
    }
    class TypeSelectionPanel extends  JPanel{
        private JComboBox<String> typeOfQuerySelector;
        private CustomJComboBoxListener comboBoxListener;

        public TypeSelectionPanel(){
            comboBoxListener = new CustomJComboBoxListener();
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setBorder(new EmptyBorder(10,0,10,0));
            this.setMaximumSize(new Dimension(920,20));
            this.add(Box.createRigidArea(new Dimension(100,0)));
            this.add(new JLabel("Seleziona i parametri di interesse:"));
            this.add(Box.createRigidArea(new Dimension(10,0)));
            typeOfQuerySelector = new JComboBox<>(QueryInfo.getInstance().getTypeOfQuery().keySet().toArray(new String[0]));
            typeOfQuerySelector.setSelectedItem(null);
            typeOfQuerySelector.setAlignmentX(LEFT_ALIGNMENT);
            typeOfQuerySelector.setSize(new Dimension(300,25));
            typeOfQuerySelector.addItemListener(comboBoxListener);
            this.add(typeOfQuerySelector);
        }
        public class CustomJComboBoxListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()== ItemEvent.SELECTED){
                    JComboBox source = (JComboBox) e.getSource();
                    if (source.equals(typeOfQuerySelector)){
                        type= QueryInfo.getInstance().getTypeOfQuery().get(source.getSelectedItem());
                        typeOfQuerySelector.setEnabled(false);
                        switch (type){
                            case STATE -> stateselectionbox.setEnabled(true);
                            case STATION -> stationselectionbox.setEnabled(true);
                            case TZONE -> timezoneselectionbox.setEnabled(true);
                            case ZONE -> zoneselectionbox.setEnabled(true);
                        }
                        ApplicazioneDatiMetereologiciGUI.getInstance().getContentPane().revalidate();
                    }
                }
            }
        }
    }
    class SelectionQueryParamsPanel extends JPanel{
        public SelectionQueryParamsPanel(){
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setSize(1000,20);
            this.setBorder(new EmptyBorder(0,0,10,0));
            SelectionQueryParamsListener listener = new SelectionQueryParamsListener();
            JLabel l2 = new JLabel("Seleziona una stazione:");
            this.add(l2);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stationselectionbox.setSelectedItem(null);
            stationselectionbox.setMaximumSize(new Dimension(200,20));
            stationselectionbox.addItemListener(listener);
            stationselectionbox.setEnabled(false);
            this.add(stationselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            JLabel l3 = new JLabel("Seleziona un fuso orario:");
            this.add(l3);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            timezoneselectionbox.setSelectedItem(null);
            timezoneselectionbox.addItemListener(listener);
            timezoneselectionbox.setMaximumSize(new Dimension(200,20));
            timezoneselectionbox.setEnabled(false);
            this.add(timezoneselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            JLabel l4 = new JLabel("Seleziona uno stato:");
            this.add(l4);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stateselectionbox.setSelectedItem(null);
            stateselectionbox.addItemListener(listener);
            stateselectionbox.setMaximumSize(new Dimension(200,20));
            stateselectionbox.setEnabled(false);
            this.add(stateselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            JLabel l5 = new JLabel("Seleziona una zona:");
            this.add(l5);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            zoneselectionbox.setSelectedItem(null);
            zoneselectionbox.addItemListener(listener);
            zoneselectionbox.setMaximumSize(new Dimension(200,20));
            zoneselectionbox.setEnabled(false);
            this.add(zoneselectionbox);
        }
        class SelectionQueryParamsListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    submitButton.setEnabled(true);
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(source.equals(timezoneselectionbox)) timezone = val;
                    else if (source.equals(stateselectionbox)) state = val;
                    else if (source.equals(stationselectionbox)) stationName = val.split("-")[0];
                    else if (source.equals(zoneselectionbox)) zone=val;
                }
            }
        }
    }

    class  submitButtonLister implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(datai==null || dataf==null){
                JOptionPane.showMessageDialog(null, "Non hai selezionato correttamente un periodo");
            }
            if(Integer.parseInt(datai)>Integer.parseInt(dataf)){
                JOptionPane.showMessageDialog(null, "Periodo non valido");
                datai=null;
                datain.setSelectedItem(null);
                dataf=null;
                datafin.setSelectedItem(null);
            }
            if(threshold==null){
                JOptionPane.showMessageDialog(null, "Non hai selezionato un valore minimo per le precipitazioni");
            }
            Object[][] ris ;
            switch (type) {
                case STATE -> {
                    ris =GraphCreator.getPrecipitationOverVals(datai,dataf,threshold,type,state);
                }
                case STATION -> {
                    ris =GraphCreator.getPrecipitationOverVals(datai,dataf,threshold,type,stationName);
                }
                case TZONE -> {
                    ris =GraphCreator.getPrecipitationOverVals(datai,dataf,threshold,type,timezone);
                }
                case ZONE -> {
                    ris =GraphCreator.getPrecipitationOverVals(datai,dataf,threshold,type,zone);
                }
            }
            try {
                queryResult.add(new PrecipitationTableVisualization(GraphCreator.getPrecipitationOverVals(datai, dataf, threshold, type, zone)));
            }
            catch( NoValuesForParamsException ex){
                JOptionPane.showMessageDialog(null, "Non ci sono misurazioni valide");
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new PrecipitationPanel());
            }
            queryResult.setVisible(true);
        }
    }
}
