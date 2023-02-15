package Panel;

import DataApi.GraphCreator;
import Enums.QueryPeriod;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Query.HourlyQueryParams;
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

public class HourlyMeasurePanel extends JPanel {
    private HourlyQueryParams params;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JCheckBox dataandhourperiod,periodindays;
    private JComboBox datain, datafin, hourin,hourfin;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;

    public HourlyMeasurePanel(){
        params=new HourlyQueryParams();
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
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new HourlyMeasurePanel());
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
            this.setMaximumSize(new Dimension(920,20));
            JPanel p1 = new JPanel();
            p1.setSize(0,20);
            p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
            JLabel l1  = new JLabel("Seleziona la misura di interesse:");
            p1.add(l1);
            measureSelector = new JComboBox<>(QueryInfo.getInstance().getHourlymeasures().keySet().toArray(new String[0]));
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
            typeOfQuerySelector = new JComboBox<>(QueryInfo.getInstance().getTypeOfQuery().keySet().toArray(new String[0]));
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
                       params.setMeasure(QueryInfo.getInstance().getHourlymeasures().get(source.getSelectedItem()));
                        measureSelector.setEnabled(false);
                        ApplicazioneDatiMetereologiciGUI.getInstance().getContentPane().revalidate();
                    }
                    else if (source.equals(typeOfQuerySelector)){
                        params.setType(QueryInfo.getInstance().getTypeOfQuery().get(source.getSelectedItem()));
                        typeOfQuerySelector.setEnabled(false);
                        switch (params.getType()){
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
            dataandhourperiod = new JCheckBox("Giorno e Fascia oraria");
            dataandhourperiod.setSize(200,20);
            dataandhourperiod.addActionListener(listener);
            periodindays = new JCheckBox("Periodo di giorni");
            periodindays.addActionListener(listener);
            periodindays.setSize(200,20);
            p1.add(dataandhourperiod);p1.add(periodindays);
            group.add(dataandhourperiod);group.add(periodindays);
            this.add(p1);
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
            TimeSelectionComboBoxListener listener1 = new TimeSelectionComboBoxListener();
            datain=new JComboBox<>(QueryInfo.getInstance().getDate());
            datain.setSelectedItem(null);
            datain.addItemListener(listener1);
            datain.setEnabled(false);
            datain.setSize(200,20);
            datafin = new JComboBox(QueryInfo.getInstance().getDate());
            datafin.setSelectedItem(null);
            datafin.setSize(200,20);
            datafin.addItemListener(listener1);
            datafin.setEnabled(false);
            hourin = new JComboBox(QueryInfo.getInstance().getFasceorarie());
            hourin.setSelectedItem(null);
            hourin.addItemListener(listener1);
            hourin.setEnabled(false);
            hourin.setSize(200,20);
            hourfin = new JComboBox(QueryInfo.getInstance().getFasceorarie());
            hourfin.setSelectedItem(null);
            hourfin.addItemListener(listener1);
            hourfin.setEnabled(false);
            hourfin.setSize(200,20);
            p2.add(new JLabel("Seleziona una data iniziale:"));p2.add(datain);
            p2.add(new JLabel("Seleziona una data finale:"));p2.add(datafin);
            p2.add(new JLabel("Selezione un'orario iniziale:")); p2.add(hourin);
            p2.add(new JLabel("Selezione un'orario finale:")); p2.add(hourfin);
            this.add(p2);
        }
        class PeriodSelectionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(source.equals(dataandhourperiod)){
                    datain.setEnabled(true);
                    hourin.setEnabled(true);
                    hourfin.setEnabled(true);
                    datafin.setSelectedItem(null);
                    params.setDataf(null);
                    datafin.setEnabled(false);
                    params.setPeriod(QueryPeriod.HOUR_PERIOD_FOR_DAY);
                }
                else if (source.equals(periodindays)){
                    datain.setEnabled(true);
                    datafin.setEnabled(true);
                    hourfin.setEnabled(false);
                    hourfin.setSelectedItem(null);
                    params.setHourf(null);
                    hourin.setEnabled(false);
                    hourin.setSelectedItem(null);
                    params.setHouri(null);
                    params.setPeriod(QueryPeriod.PERIOD_DAYS);
                }
            }
        }
        class TimeSelectionComboBoxListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    val=val.replace(":","");
                    if(source.equals(datain)) params.setDatai(val);
                    else if (source.equals(datafin)) params.setDataf(val);
                    else if (source.equals(hourin)) params.setHouri(val);
                    else if (source.equals(hourfin)) params.setHourf(val);
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
                    if (source.equals(stationselectionbox)) params.setParam(val.split("-")[0]);
                    else params.setParam(val);
                }
            }
        }
    }
    class  submitButtonLister implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(submitButton)){
                try {
                    params.verify();
                    queryResult.add(params.createGraph());
                } catch (UncompleteQueryParamInitialization ex) {
                    JOptionPane.showMessageDialog(null, "Alcuni parametri della query non sono stati inizializati");
                } catch (WrongDateInitialization ex) {
                    JOptionPane.showMessageDialog(null, "Il periodo selezionato non è valido");
                    datafin.setSelectedItem(null);
                    params.setDataf(null);
                    datain.setSelectedItem(null);
                    params.setDatai(null);
                    hourin.setSelectedItem(null);
                    params.setHouri(null);
                    hourfin.setSelectedItem(null);
                    params.setHourf(null);
                    params.setPeriod(null);
                } catch (NoValuesForParamsException ex) {
                    JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new HourlyMeasurePanel());
                }
                queryResult.setVisible(true);
            }
        }
    }
}