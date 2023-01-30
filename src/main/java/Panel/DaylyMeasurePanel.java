package Panel;

import Enums.QueryType;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;

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
    private String state, stationName, timezone, datainit, dataf, hourinit, hourf;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox;

    private JCheckBox specificdata,periodindays,dataandhour;
    private JComboBox datain, datafin, hourin, hourfin;
    private JButton submitButton;
    public DaylyMeasurePanel(){
        stateselectionbox =new JComboBox<String>(QueryInfo.getInstance().getStates()); timezoneselectionbox=new JComboBox<String>(QueryInfo.getInstance().getTimezone()); stationselectionbox=new JComboBox<String>(QueryInfo.getInstance().getStations());
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new TypeAndMeasureSelectionPanel());
        this.add( Box.createVerticalStrut(5));
        this.add(new TimePeriodSelectionPanel());
        this.add( new SelectionQueryParamsPanel());
        JPanel submitPanel = new JPanel(); submitPanel.setPreferredSize( new Dimension(100,20));
        submitButton = new JButton("Cerca");
        submitButton.addActionListener(new submitButtonLister());
        submitButton.setEnabled(false);
        submitPanel.add(submitButton);
        this.add(submitPanel);
        JPanel p = new JPanel();
        p.setSize(920,600);
        this.add(p);
    }
    class TypeAndMeasureSelectionPanel extends  JPanel{
        Map<String,String> possibleMeasures= Map.of(
                "Temperatura Massima","Tmax",
                "Temperatura Minima", "Tmin",
                "Temperatura Media", "Tavg",
                "Tramonto","Sunset",
                "Alba","Sunrise",
                "Precipitazioni Nevose","SnowFall",
                "Pressione al livello del mare","SeaLevel",
                "Velocità del vento","AvgSpeed"
        );
        Map<String, QueryType> typeOfQuery=Map.of(
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
                        measure= possibleMeasures.get(source.getSelectedItem());
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

            this.setSize(900,20);
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            JLabel l1 = new JLabel("Seleziona l'arco temporale:");
            p1.add(l1);
            ActionListener listener = new PeriodSelectionListener();
            specificdata = new JCheckBox("Giorno");
            specificdata.setSize(300,20);
            specificdata.addActionListener(listener);
            periodindays = new JCheckBox("Giorni");
            periodindays.addActionListener(listener);
            periodindays.setSize(300,20);
            dataandhour = new JCheckBox("Giorno e fascia oraria");
            dataandhour.addActionListener(listener);
            dataandhour.setSize(300,20);
            p1.add(specificdata);p1.add(periodindays);p1.add(dataandhour);
            group.add(specificdata);group.add(periodindays); group.add(dataandhour);
            this.add(p1);
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
            TimeSelectionComboBoxListener listener1 = new TimeSelectionComboBoxListener();
            datain=new JComboBox<>(QueryInfo.getInstance().getDate());
            datain.setSelectedItem(null);
            datain.addItemListener(listener1);
            datain.setEnabled(false);
            datain.setSize(300,20);
            datafin = new JComboBox(QueryInfo.getInstance().getDate());
            datafin.setSelectedItem(null);
            datafin.setSize(300,20);
            datafin.addItemListener(listener1);
            datafin.setEnabled(false);
            hourin=new JComboBox<>(QueryInfo.getInstance().getFasceorarie());
            hourin.setSelectedItem(null);
            hourin.setSize(300,20);
            hourin.addItemListener(listener1);
            hourin.setEnabled(false);
            hourfin=new JComboBox(QueryInfo.getInstance().getFasceorarie());
            hourfin.setSelectedItem(null);
            hourfin.setSize(300,20);
            hourfin.addItemListener(listener1);
            hourfin.setEnabled(false);
            JLabel l2  = new JLabel("Seleziona una data iniziale:");
            p2.add(l2);p2.add(datain);
            JLabel l3  = new JLabel("Seleziona una data finale:");
            p2.add(l3);p2.add(datafin);
            JLabel l4 = new JLabel("Seleziona un orario iniziale:");
            p2.add(l4);p2.add(hourin);
            JLabel l5  = new JLabel("Seleziona un orario finale:");
            p2.add(l5);p2.add(hourfin);
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
                    hourinit=null;
                    hourin.setSelectedItem(null);
                    hourin.setEnabled(false);
                    hourfin.setSelectedItem(null);
                    hourf=null;
                    hourfin.setEnabled(false);

                }
                else if (source.equals(periodindays)){
                    datain.setEnabled(true);
                    datafin.setEnabled(true);
                    hourinit=null;
                    hourin.setSelectedItem(false);
                    hourin.setEnabled(false);
                    hourf=null;
                    hourfin.setSelectedItem(null);
                    hourfin.setEnabled(false);
                }
                else if (source.equals(dataandhour)){
                    datain.setEnabled(true);
                    dataf=null;
                    datafin.setSelectedItem(null);
                    datafin.setEnabled(false);
                    hourin.setEnabled(true);
                    hourfin.setEnabled(true);
                }
            }
        }
        class TimeSelectionComboBoxListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    val.replace(":","");
                    if(source.equals(datain)) datainit=val;
                    else if (source.equals(datafin)) dataf =val;
                    else if (source.equals(hourin)) hourinit= val;
                    else if (source.equals(hourfin)) hourf=val;
                }
            }
        }
    }
    class SelectionQueryParamsPanel extends JPanel{
        public SelectionQueryParamsPanel(){
            this.setSize(900,20);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            JLabel l1 = new JLabel("Seleziona una regione:");
            this.add(l1);
            SelectionQueryParamsListener listener = new SelectionQueryParamsListener();
            JLabel l2 = new JLabel("Seleziona una stazione:");
            this.add(l2);
            stationselectionbox.setSelectedItem(null);
            stationselectionbox.setSize(300,20);
            stationselectionbox.addItemListener(listener);
            stationselectionbox.setEnabled(false);
            this.add(stationselectionbox);
            JLabel l3 = new JLabel("Seleziona un fuso orario:");
            this.add(l3);
            timezoneselectionbox.setSelectedItem(null);
            timezoneselectionbox.setSize(300,20);
            timezoneselectionbox.addItemListener(listener);
            timezoneselectionbox.setEnabled(false);
            this.add(timezoneselectionbox);
            JLabel l4 = new JLabel("Seleziona uno stato:");
            this.add(l4);
            stateselectionbox.setSelectedItem(null);
            stateselectionbox.setSize(300,20);
            stateselectionbox.addItemListener(listener);
            stateselectionbox.setEnabled(false);
            this.add(stateselectionbox);
        }
        class SelectionQueryParamsListener implements ItemListener{
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED){
                        submitButton.setEnabled(true);
                        JComboBox<String> source = (JComboBox<String>) e.getSource();
                        String val = source.getSelectedItem().toString();
                        if(source.equals(timezoneselectionbox)) timezone=val;
                        else if (source.equals(stateselectionbox)) state =val;
                        else if (source.equals(stationName)) stationName = val;
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
                if(specificdata.isSelected()){
                    if(datainit==null) {JOptionPane.showMessageDialog(null, "Non hai selezionato la data per cui vuoi ottenere le misurazioni");}
                    switch (type){
                        case STATE -> {

                        }
                        case STATION -> {

                        }
                        case TZONE -> {

                        }
                    }
                }
                else if ( periodindays.isSelected() ){
                    if((datainit==null || dataf == null) || (Integer.parseInt(datainit) > Integer.parseInt(dataf))){
                        JOptionPane.showMessageDialog(null, "Il periodo per cui vuoi ottenere le misurazioni non è valido");
                        datainit = null; dataf = null; datafin.setSelectedItem(null); datain.setSelectedItem(null);
                    }
                    switch (type){
                        case STATE -> {

                        }
                        case STATION -> {

                        }
                        case TZONE -> {

                        }
                    }
                }
                else if (dataandhour.isSelected()){
                    if((datainit==null || hourinit==null || hourf==null ) || (Integer.parseInt(hourinit) > Integer.parseInt(hourf))){
                        JOptionPane.showMessageDialog(null, "Il periodo per cui vuoi ottenere le misurazioni non è valido");
                        datainit=null; hourinit=null;hourf=null;
                        datain.setSelectedItem(null); hourin.setSelectedItem(null);hourfin.setSelectedItem(null);
                    }
                    switch (type){
                        case STATE -> {

                        }
                        case STATION -> {

                        }
                        case TZONE -> {

                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Non hai selezionato un periodo di tempo");

                }
            }
        }
    }
}