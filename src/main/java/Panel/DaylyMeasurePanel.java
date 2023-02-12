package Panel;

import DataApi.DataAPI;
import DataApi.GraphCreator;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Panel.SubPanel.PeriodQueryTableVisualization;
import Panel.SubPanel.QueryTableVisualization;
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
import java.util.Map;

public class DaylyMeasurePanel extends JPanel {
    private String measure;
    private QueryType type;
    private String state, stationName, timezone,zone, datainit, dataf;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JCheckBox specificdata,periodindays;
    private JComboBox datain, datafin;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public DaylyMeasurePanel(){
        stateselectionbox =new JComboBox<String>(QueryInfo.getInstance().getStates());
        timezoneselectionbox=new JComboBox<String>(QueryInfo.getInstance().getTimezone());
        stationselectionbox=new JComboBox<String>(QueryInfo.getInstance().getStations());
        zoneselectionbox= new JComboBox<String>(QueryInfo.getInstance().getZone());
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new TypeAndMeasureSelectionPanel());
        this.add(new TimePeriodSelectionPanel());
        this.add( new SelectionQueryParamsPanel());
        JPanel submitPanel = new JPanel(); submitPanel.setSize( new Dimension(100,40));
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
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
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

    class TypeAndMeasureSelectionPanel extends  JPanel{
        private JComboBox<String> measureSelector,typeOfQuerySelector;
        private CustomJComboBoxListener comboBoxListener;

        public TypeAndMeasureSelectionPanel(){
            comboBoxListener = new CustomJComboBoxListener();
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setBorder(new EmptyBorder(10,0,10,0));
            this.setMaximumSize(new Dimension(920,40));
            JPanel p1 = new JPanel();
            p1.setSize(0,20);
            p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
            JLabel l1  = new JLabel("Seleziona la misura di interesse:");
            l1.setFont(new Font("Calibri",Font.BOLD,20));
            p1.add(l1);
            measureSelector = new JComboBox<>(QueryInfo.getInstance().getDaylymeasures().keySet().toArray(new String[0]));
            measureSelector.setSelectedItem(null);
            measureSelector.setAlignmentX(LEFT_ALIGNMENT);
            measureSelector.setSize(new Dimension(300,40));
            measureSelector.addItemListener(comboBoxListener);
            p1.add(measureSelector);
            this.add(p1);
            this.add(Box.createRigidArea(new Dimension(100,0)));
            JPanel p2 = new JPanel();
            p2.setSize(600,20);
            p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
            JLabel l2 = new JLabel("Seleziona i parametri di interesse:");
            l2.setFont(new Font("Calibri",Font.BOLD,20));
            p2.add(l2);
            typeOfQuerySelector = new JComboBox<>(QueryInfo.getInstance().getTypeOfQuery().keySet().toArray(new String[0]));
            typeOfQuerySelector.setSelectedItem(null);
            typeOfQuerySelector.setAlignmentX(LEFT_ALIGNMENT);
            typeOfQuerySelector.setSize(new Dimension(300,40));
            typeOfQuerySelector.addItemListener(comboBoxListener);
            p2.add(typeOfQuerySelector);
            this.add(p2);
        }
        public class CustomJComboBoxListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()== ItemEvent.SELECTED){
                    JComboBox source = (JComboBox) e.getSource();
                    if(source.equals(measureSelector)){
                        measure = QueryInfo.getInstance().getDaylymeasures().get(source.getSelectedItem());
                        measureSelector.setEnabled(false);
                        ApplicazioneDatiMetereologiciGUI.getInstance().getContentPane().revalidate();
                    }
                    else if (source.equals(typeOfQuerySelector)){
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
    class TimePeriodSelectionPanel extends JPanel{
        ButtonGroup group = new ButtonGroup();
        public TimePeriodSelectionPanel(){
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setMaximumSize(new Dimension(920,40));
            this.setBorder(new EmptyBorder(0,0,10,0));
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            JLabel l1 = new JLabel("Seleziona l'arco temporale:");
            p1.add(l1);
            ActionListener listener = new PeriodSelectionListener();
            specificdata = new JCheckBox("Giorno");
            specificdata.setSize(200,40);
            specificdata.addActionListener(listener);
            periodindays = new JCheckBox("Giorni");
            periodindays.addActionListener(listener);
            periodindays.setSize(200,40);
            p1.add(specificdata);p1.add(periodindays);
            group.add(specificdata);group.add(periodindays);
            this.add(p1);
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
            TimeSelectionComboBoxListener listener1 = new TimeSelectionComboBoxListener();
            datain=new JComboBox<>(QueryInfo.getInstance().getDate());
            datain.setSelectedItem(null);
            datain.addItemListener(listener1);
            datain.setEnabled(false);
            datain.setSize(200,40);
            datafin = new JComboBox(QueryInfo.getInstance().getDate());
            datafin.setSelectedItem(null);
            datafin.setSize(200,40);
            datafin.addItemListener(listener1);
            datafin.setEnabled(false);
            JLabel l2  = new JLabel("Seleziona una data iniziale:");
            p2.add(l2);p2.add(datain);
            JLabel l3  = new JLabel("Seleziona una data finale:");
            p2.add(l3);p2.add(datafin);
            this.add(p2);
        }
        class PeriodSelectionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(source.equals(specificdata)){
                    datain.setEnabled(true);
                    datafin.setSelectedItem(null);
                    dataf=null;
                    datafin.setEnabled(false);
                }
                else if (source.equals(periodindays)){
                    datain.setEnabled(true);
                    datafin.setEnabled(true);
                }
            }
        }
        class TimeSelectionComboBoxListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(source.equals(datain)) datainit=val;
                    else if (source.equals(datafin)) dataf =val;
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
            timezoneselectionbox.setMaximumSize(new Dimension(200,40));
            timezoneselectionbox.setEnabled(false);
            this.add(timezoneselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            JLabel l4 = new JLabel("Seleziona uno stato:");
            this.add(l4);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stateselectionbox.setSelectedItem(null);
            stateselectionbox.addItemListener(listener);
            stateselectionbox.setMaximumSize(new Dimension(200,40));
            stateselectionbox.setEnabled(false);
            this.add(stateselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            JLabel l5 = new JLabel("Seleziona una zona:");
            this.add(l5);
            this.add(Box.createRigidArea(new Dimension(10,0)));
            zoneselectionbox.setSelectedItem(null);
            zoneselectionbox.addItemListener(listener);
            zoneselectionbox.setMaximumSize(new Dimension(200,40));
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
            if(e.getSource().equals(submitButton)){
                if(measure == null){
                    JOptionPane.showMessageDialog(null, "Non hai selezionato la misura");
                }
                if(specificdata.isSelected()) {
                    if (datainit == null) {
                        JOptionPane.showMessageDialog(null, "Non hai selezionato la data per cui vuoi ottenere le misurazioni");
                    }
                    try {
                        switch (type) {
                            case STATE -> {
                                queryResult.add(new QueryTableVisualization(measure + " per il giorno " + datainit, DataAPI.getMeasureByDay("dayly", datainit, measure, type, state)));
                            }
                            case STATION -> {
                                queryResult.add(new QueryTableVisualization(measure + " per il giorno " + datainit, DataAPI.getMeasureByDay("dayly", datainit, measure, type, stationName)));
                            }
                            case TZONE -> {
                                queryResult.add(new QueryTableVisualization(measure + " per il giorno " + datainit, DataAPI.getMeasureByDay("dayly", datainit, measure, type, timezone)));
                            }
                            case ZONE -> {
                                queryResult.add(new QueryTableVisualization(measure + " per il giorno " + datainit, DataAPI.getMeasureByDay("dayly", datainit, measure, type, zone)));
                            }
                        }
                    } catch (NoValuesForParamsException ex) {
                        JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
                    }
                    queryResult.setVisible(true);
                }
                else if ( periodindays.isSelected() ){
                    if((datainit==null || dataf == null) || (Integer.parseInt(datainit) > Integer.parseInt(dataf))){
                        JOptionPane.showMessageDialog(null, "Il periodo per cui vuoi ottenere le misurazioni non è valido");
                        datainit = null; dataf = null; datafin.setSelectedItem(null); datain.setSelectedItem(null);
                    }
                    try{
                        System.out.println(measure);
                        switch (type){
                            case STATE -> {
                                if(measure.equals("Sunset")|| measure.equals("Sunrise")) {
                                    Object[][] ris = GraphCreator.getDaylyMeasureGraphForDateVal("dayly", datainit, dataf, measure, type, state);
                                    queryResult.add(new PeriodQueryTableVisualization(ris, measure));
                                }
                                else queryResult.add(new XChartPanel<>(GraphCreator.getDaylyMeasureGraphForDoubleVal("dayly",datainit,dataf,measure,type,state)));
                            }
                            case STATION -> {
                                if(measure.equals("Sunset")|| measure.equals("Sunrise")){
                                    Object[][] ris = GraphCreator.getDaylyMeasureGraphForDateVal("dayly",datainit,dataf,measure,type,stationName);
                                    queryResult.add(new PeriodQueryTableVisualization(ris,measure));
                                }
                                else queryResult.add(new XChartPanel<>(GraphCreator.getDaylyMeasureGraphForDoubleVal("dayly",datainit,dataf,measure,type,stationName)));
                            }
                            case TZONE -> {
                                if(measure.equals("Sunset")|| measure.equals("Sunrise")){
                                    Object[][] ris = GraphCreator.getDaylyMeasureGraphForDateVal("dayly",datainit,dataf,measure,type,timezone);
                                    queryResult.add(new PeriodQueryTableVisualization(ris,measure));
                                }
                                else queryResult.add(new XChartPanel<>(GraphCreator.getDaylyMeasureGraphForDoubleVal("dayly",datainit,dataf,measure,type,timezone)));
                            }
                            case ZONE -> {
                                if(measure.equals("Sunset")|| measure.equals("Sunrise")){
                                    Object[][] ris = GraphCreator.getDaylyMeasureGraphForDateVal("dayly",datainit,dataf,measure,type,zone);
                                    queryResult.add(new PeriodQueryTableVisualization(ris,measure));
                                }
                                else queryResult.add(new XChartPanel<>(GraphCreator.getDaylyMeasureGraphForDoubleVal("dayly",datainit,dataf,measure,type,zone)));
                            }
                        }
                    }
                    catch (NoValuesForParamsException ex ){
                        JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
                    }
                    catch (IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
                    }
                    queryResult.setVisible(true);
                }
            }
        }
    }
}