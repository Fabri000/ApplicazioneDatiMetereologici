package Panel;

import DataApi.DataAPI;
import DataApi.DataAPI$;
import DataApi.GraphCreator;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Panel.SubPanel.MonthlyQueryTable;
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

public class MonthlyMeasurePanel extends JPanel {
    private String measure;
    private QueryType type;
    private String state, stationName, timezone,zone, monthinit, monthf;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JCheckBox singlemonth, monthlyperiod;
    private JComboBox monthin, monthfin;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult=new JPanel();
    public MonthlyMeasurePanel(){
        stateselectionbox =new JComboBox<String>(QueryInfo.getInstance().getStates());
        timezoneselectionbox=new JComboBox<String>(QueryInfo.getInstance().getTimezone());
        stationselectionbox=new JComboBox<String>(QueryInfo.getInstance().getStations());
        zoneselectionbox= new JComboBox<String>(QueryInfo.getInstance().getZone());
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new TypeAndMeasureSelectionPanel());
        this.add(new TimePeriodSelectionPanel());
        this.add(new SelectionQueryParamsPanel());
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
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new MonthlyMeasurePanel());
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
        Map<String,String> possibleMeasures= Map.of(
                "Temperatura Massima Media","AvgMaxTemp",
                "Temperatura Minima Media","AvgMinTemp",
                "Temperatura Media","AvgTemp",
                "Pressione al livello del mare massima", "MaxSeaLevelPressure",
                "Pressione al livello del mare minima","MinSeaLevelPressure",
                "Precipitazioni","TotalMonthlyPrecip",
                "Precipitazioni nevose", "TotalSnowfall",
                "Velocità del vento","AvgWindSpeed"
        );
        Map<String, QueryType> typeOfQuery=Map.of(
                "Zona", QueryType.ZONE,
                "Stato", QueryType.STATE,
                "Fuso orario",QueryType.TZONE,
                "Stazione",QueryType.STATION
        );
        private JComboBox<String> measureSelector,typeOfQuerySelector;
        private CustomJComboBoxListener comboBoxListener;

        public TypeAndMeasureSelectionPanel(){
            comboBoxListener = new CustomJComboBoxListener();
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setBorder(new EmptyBorder(10,0,10,0));
            this.setMaximumSize(new Dimension(920,20));
            JPanel p1 = new JPanel();
            p1.setSize(0,20);
            p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
            JLabel l1  = new JLabel("Seleziona la misura di interesse:");
            p1.add(l1);
            measureSelector = new JComboBox<>(possibleMeasures.keySet().toArray(new String[0]));
            measureSelector.setSelectedItem(null);
            measureSelector.setAlignmentX(LEFT_ALIGNMENT);
            measureSelector.setSize(new Dimension(300,25));
            measureSelector.addItemListener(comboBoxListener);
            p1.add(measureSelector);
            this.add(p1);
            this.add(Box.createRigidArea(new Dimension(100,0)));
            JPanel p2 = new JPanel();
            p2.setSize(600,20);
            p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
            JLabel l2 = new JLabel("Seleziona i parametri di interesse:");
            p2.add(l2);
            typeOfQuerySelector = new JComboBox<>(typeOfQuery.keySet().toArray(new String[0]));
            typeOfQuerySelector.setSelectedItem(null);
            typeOfQuerySelector.setAlignmentX(LEFT_ALIGNMENT);
            typeOfQuerySelector.setSize(new Dimension(300,25));
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
                        measure = possibleMeasures.get(source.getSelectedItem());
                        measureSelector.setEnabled(false);
                        ApplicazioneDatiMetereologiciGUI.getInstance().getContentPane().revalidate();
                    }
                    else if (source.equals(typeOfQuerySelector)){
                        type= typeOfQuery.get(source.getSelectedItem());
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
            this.setMaximumSize(new Dimension(920,20));
            this.setBorder(new EmptyBorder(0,0,10,0));
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            JLabel l1 = new JLabel("Seleziona l'arco temporale:");
            p1.add(l1);
            ActionListener listener = new PeriodSelectionListener();
            singlemonth = new JCheckBox("Mese iniziale:");
            singlemonth.setSize(200,20);
            singlemonth.addActionListener(listener);
            monthlyperiod = new JCheckBox("Mese finale:");
            monthlyperiod.addActionListener(listener);
            monthlyperiod.setSize(200,20);
            p1.add(singlemonth);p1.add(monthlyperiod);
            group.add(singlemonth);group.add(monthlyperiod);
            this.add(p1);
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
            TimeSelectionComboBoxListener listener1 = new TimeSelectionComboBoxListener();
            monthin =new JComboBox<>(QueryInfo.getInstance().getMonths());
            monthin.setSelectedItem(null);
            monthin.addItemListener(listener1);
            monthin.setEnabled(false);
            monthin.setSize(200,20);
            monthfin = new JComboBox(QueryInfo.getInstance().getMonths());
            monthfin.setSelectedItem(null);
            monthfin.setSize(200,20);
            monthfin.addItemListener(listener1);
            monthfin.setEnabled(false);
            p2.add(new JLabel("Seleziona una data iniziale:"));p2.add(monthin);
            p2.add(new JLabel("Seleziona una data finale:"));p2.add(monthfin);
            this.add(p2);
        }
        class PeriodSelectionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(source.equals(singlemonth)){
                    monthin.setEnabled(true);
                    monthfin.setSelectedItem(null);
                    monthf =null;
                    monthfin.setEnabled(false);
                }
                else if (source.equals(monthlyperiod)){
                    monthin.setEnabled(true);
                    monthfin.setEnabled(true);
                }
            }
        }
        class TimeSelectionComboBoxListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(source.equals(monthin)) monthinit = val;
                    else if (source.equals(monthfin)) monthf = val;
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
            if(e.getSource().equals(submitButton)){
                if(measure == null){
                    JOptionPane.showMessageDialog(null, "Non hai selezionato la misura");
                }
                if(singlemonth.isSelected()){
                    if(monthinit==null){
                        JOptionPane.showMessageDialog(null, "Non hai selezionato il mese per cui vuoi ottenere le misurazioni");
                    }
                    try {
                        switch (type) {
                            case STATE -> {
                                queryResult.add(new MonthlyQueryTable(DataAPI.getMonthlyMeasure(monthinit, measure, type, state),measure));
                            }
                            case STATION -> {
                                queryResult.add(new MonthlyQueryTable(DataAPI.getMonthlyMeasure(monthinit, measure, type, stationName),measure));
                            }
                            case TZONE -> {
                                queryResult.add(new MonthlyQueryTable(DataAPI.getMonthlyMeasure(monthinit, measure, type, timezone),measure));
                            }
                            case ZONE -> {
                                queryResult.add(new MonthlyQueryTable(DataAPI.getMonthlyMeasure(monthinit, measure, type, zone),measure));
                            }
                        }
                    }
                    catch (NoValuesForParamsException ex){
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new MonthlyMeasurePanel());
                        JOptionPane.showMessageDialog(null, "Nessuna misurazione valida per il periodo scelto");
                    }
                    queryResult.setVisible(true);
                }
                if(monthlyperiod.isSelected()){
                    if(monthinit==null || monthf==null){
                        JOptionPane.showMessageDialog(null, "Non hai selezionato il mese per cui vuoi ottenere le misurazioni");
                    }
                    try {
                        switch (type) {
                            case STATE -> {
                                queryResult.add(new XChartPanel<>(GraphCreator.getMonthlyPeriodMeasures(monthinit, monthf, measure, type, state)));
                            }
                            case STATION -> {
                                queryResult.add(new XChartPanel<>(GraphCreator.getMonthlyPeriodMeasures(monthinit, monthf, measure, type, stationName)));
                            }
                            case TZONE -> {
                                queryResult.add(new XChartPanel<>(GraphCreator.getMonthlyPeriodMeasures(monthinit, monthf, measure, type, timezone)));
                            }
                            case ZONE -> {
                                queryResult.add(new XChartPanel<>(GraphCreator.getMonthlyPeriodMeasures(monthinit, monthf, measure, type, zone)));
                            }
                        }
                    }
                    catch (IllegalArgumentException ex){
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new MonthlyMeasurePanel());
                        JOptionPane.showMessageDialog(null, "Nessuna misurazione valida per il periodo scelto");
                    }
                    queryResult.setVisible(true);
                }
            }
        }
    }
}
