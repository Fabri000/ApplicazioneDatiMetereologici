package Panel;

import Enums.QueryPeriod;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Query.DaylyQueryParams;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DaylyMeasurePanel extends JPanel {
    private DaylyQueryParams params;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JCheckBox specificdata,periodindays;
    private JComboBox datain, datafin;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public DaylyMeasurePanel(){
        params=new DaylyQueryParams();
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
        newResearchButton = new JButton("Nuova ricerca");

        this.add(UIElemCreator.createNavigationButtonPanel(newResearchButton,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
                }
            }
        }));
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
            p1.add(UIElemCreator.createLabel("Seleziona la misura di interesse:"));
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
            p2.add(UIElemCreator.createLabel("Seleziona i parametri di interesse:"));
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
                        params.setMeasure(QueryInfo.getInstance().getDaylymeasures().get(source.getSelectedItem()));
                        measureSelector.setEnabled(false);
                        ApplicazioneDatiMetereologiciGUI.getInstance().getContentPane().revalidate();
                    }
                    else if (source.equals(typeOfQuerySelector)){
                        params.setType( QueryInfo.getInstance().getTypeOfQuery().get(source.getSelectedItem()));
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
            this.setMaximumSize(new Dimension(920,40));
            this.setBorder(new EmptyBorder(0,0,10,0));
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            p1.add(UIElemCreator.createLabel("Seleziona l'arco temporale:"));
            p1.add(Box.createRigidArea(new Dimension(20,0)));
            ActionListener listener = new PeriodSelectionListener();
            specificdata = UIElemCreator.createCheckBox("Data specifica");
            specificdata.addActionListener(listener);
            periodindays = UIElemCreator.createCheckBox("Periodo");
            periodindays.addActionListener(listener);
            p1.add(specificdata);
            p1.add(Box.createRigidArea(new Dimension(15,0)));
            p1.add(periodindays);
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
            p2.add(UIElemCreator.createLabel("Seleziona l'arco temporale:"));
            p2.add(Box.createRigidArea(new Dimension(20,0)));
            p2.add(datain);
            p2.add(Box.createRigidArea(new Dimension(20,0)));
            p2.add(UIElemCreator.createLabel("Seleziona l'arco temporale:"));
            p2.add(Box.createRigidArea(new Dimension(20,0)));
            p2.add(datafin);
            this.add(p2);
        }
        class PeriodSelectionListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(source.equals(specificdata)){
                    params.setPeriod(QueryPeriod.SINGLE_DAY);
                    datain.setEnabled(true);
                    datafin.setSelectedItem(null);
                    params.setDataf(null);
                    datafin.setEnabled(false);
                }
                else if (source.equals(periodindays)){
                    params.setPeriod(QueryPeriod.PERIOD_DAYS);
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
                    if(source.equals(datain)) params.setDatai(val);
                    else if (source.equals(datafin)) params.setDataf(val);
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
            this.add(UIElemCreator.createLabel("Seleziona una stazione:"));
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stationselectionbox.setSelectedItem(null);
            stationselectionbox.setMaximumSize(new Dimension(200,20));
            stationselectionbox.addItemListener(listener);
            stationselectionbox.setEnabled(false);
            this.add(stationselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(UIElemCreator.createLabel("Seleziona un fuso orario:"));
            this.add(Box.createRigidArea(new Dimension(10,0)));
            timezoneselectionbox.setSelectedItem(null);
            timezoneselectionbox.addItemListener(listener);
            timezoneselectionbox.setMaximumSize(new Dimension(200,40));
            timezoneselectionbox.setEnabled(false);
            this.add(timezoneselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(UIElemCreator.createLabel("Seleziona uno stato:"));
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stateselectionbox.setSelectedItem(null);
            stateselectionbox.addItemListener(listener);
            stateselectionbox.setMaximumSize(new Dimension(200,40));
            stateselectionbox.setEnabled(false);
            this.add(stateselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(UIElemCreator.createLabel("Seleziona una zona:"));
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
                }
                catch (NoValuesForParamsException ex) {
                    JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new DaylyMeasurePanel());
                } catch (UncompleteQueryParamInitialization ex) {
                    JOptionPane.showMessageDialog(null, "Alcuni parametri della query non sono stati inizializati");
                } catch (WrongDateInitialization ex) {
                    JOptionPane.showMessageDialog(null, "Il periodo selezionato non è valido");
                    params.setDatai(null);
                    datain.setSelectedItem(null);
                    params.setDataf(null);
                    datafin.setSelectedItem(null);
                }
                queryResult.setVisible(true);
            }

        }
    }
}