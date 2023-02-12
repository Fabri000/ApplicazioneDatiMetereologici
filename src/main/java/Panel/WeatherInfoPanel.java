package Panel;

import DataApi.GraphCreator;
import Enums.QueryType;
import Panel.SubPanel.WeatherDistributionTableVisualization;
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

public class WeatherInfoPanel extends JPanel {
    private String datain,datafin,param;
    private QueryType type;
    private JCheckBox weatherdistribution,windchill;
    private JComboBox datainselector,datafinselector,stateselector,stationselector,zoneselector,timezoneselector, queryTypeselector;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public WeatherInfoPanel(){
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new dateSelectionPanel());
        this.add(new QueryTypeSelectionPanel());
        this.add(new QuerySelectionPanel());
        JPanel submitPanel = new JPanel(); submitPanel.setSize( new Dimension(100,20));
        submitButton = new JButton("Cerca");
        submitButton.addActionListener(new submitButtonLister());
        submitButton.setEnabled(false);
        submitPanel.add(submitButton);
        this.add(submitPanel);
        queryResult= new JPanel();
        queryResult.setVisible(false);
        this.add(queryResult);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        newResearchButton = new JButton("Nuova ricerca");
        newResearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new WeatherInfoPanel());
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

    class dateSelectionPanel extends JPanel{
        ButtonGroup group = new ButtonGroup();
        weatherInfoBoxListeser checkboxlistener = new weatherInfoBoxListeser();
        dateSelectionListener comboboxlistener = new dateSelectionListener();
        public dateSelectionPanel(){
            this.setSize(900,400);
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            JPanel checkboxs = new JPanel();
            checkboxs.setLayout( new BoxLayout(checkboxs,BoxLayout.X_AXIS));
            checkboxs.add(new JLabel("Seleziona le informazioni metereologiche che vuoi osservare:"));
            checkboxs.add(Box.createRigidArea(new Dimension(15,0)));
            weatherdistribution = new JCheckBox("Distribuzione tipi di meteo");
            weatherdistribution.addActionListener(checkboxlistener);
            group.add(weatherdistribution);
            windchill = new JCheckBox("Vento gelido");
            windchill.addActionListener(checkboxlistener);
            group.add(windchill);
            checkboxs.add(weatherdistribution); checkboxs.add(windchill);
            this.add(checkboxs);
            JPanel comboboxs = new JPanel();
            comboboxs.setLayout(new BoxLayout(comboboxs,BoxLayout.X_AXIS));
            comboboxs.add(new JLabel("Seleziona data iniziale"));
            comboboxs.add(Box.createRigidArea(new Dimension(15,0)));
            datainselector = new JComboBox(QueryInfo.getInstance().getDate());
            datainselector.setEnabled(false);
            datainselector.addItemListener( comboboxlistener);
            datainselector.setSelectedItem(null);
            datainselector.setMaximumSize(new Dimension(200,20));
            comboboxs.add(datainselector);
            comboboxs.add(Box.createRigidArea(new Dimension(15,0)));
            comboboxs.add(new JLabel("Seleziona data finale"));
            comboboxs.add(Box.createRigidArea(new Dimension(15,0)));
            datafinselector = new JComboBox(QueryInfo.getInstance().getDate());
            datafinselector.setSelectedItem(null);
            datafinselector.addItemListener( comboboxlistener);
            datafinselector.setEnabled(false);
            datafinselector.setMaximumSize(new Dimension(200,20));
            comboboxs.add(datafinselector);
            this.add(comboboxs);
        }

        class weatherInfoBoxListeser implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if(source.equals(weatherdistribution)){
                    datain=null;
                    datainselector.setSelectedItem(null);
                    datainselector.setEnabled(true);
                    datafin = null;
                    datafinselector.setSelectedItem(null);
                    datafinselector.setEnabled(true);
                    stateselector.setEnabled(true);
                }
                else if(source.equals(windchill)){
                    datain=null;
                    datainselector.setSelectedItem(null);
                    datainselector.setEnabled(true);
                    datafin = null;
                    datafinselector.setSelectedItem(null);
                    datafinselector.setEnabled(false);
                    queryTypeselector.setEnabled(true);
                }
            }
        }
        class  dateSelectionListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(source.equals(datainselector)) datain=val;
                    else if (source.equals(datafinselector)) datafin =val;
                }
            }
        }
    }

    class QueryTypeSelectionPanel extends JPanel{
        public QueryTypeSelectionPanel(){
            this.setSize(900,20);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.add(new JLabel("Seleziona tipo:"));
            queryTypeselector = new JComboBox(QueryInfo.getInstance().getTypeOfQuery().keySet().toArray(new String[0]));
            queryTypeselector.setMaximumSize(new Dimension(200,20));
            queryTypeselector.addItemListener(new QueryTypeSelectionListener());
            queryTypeselector.setSelectedItem(null);
            queryTypeselector.setEnabled(false);
            this.add(queryTypeselector);
        }

        class QueryTypeSelectionListener implements ItemListener{

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox source = (JComboBox<String>) e.getSource();
                    type = QueryInfo.getInstance().getTypeOfQuery().get( source.getSelectedItem().toString());
                    switch (type){
                        case STATION -> stationselector.setEnabled(true);
                        case STATE -> stateselector.setEnabled(true);
                        case ZONE -> zoneselector.setEnabled(true);
                        case TZONE -> timezoneselector.setEnabled(true);
                    }
                    source.setEnabled(false);
                }
            }
        }
    }

    class QuerySelectionPanel extends JPanel{
        public QuerySelectionPanel(){
            this.setSize(900,20);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.add(new JLabel("Seleziona lo stato:"));
            stateselector = new JComboBox(QueryInfo.getInstance().getStates());
            stateselector.addItemListener(new QuerySelectionListener());
            stateselector.setSelectedItem(null);
            stateselector.setEnabled(false);
            stateselector.setMaximumSize(new Dimension(200,20));
            this.add(stateselector);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(new JLabel("Seleziona la stazione:"));
            stationselector = new JComboBox(QueryInfo.getInstance().getStations());
            stationselector.addItemListener(new QuerySelectionListener());
            stationselector.setSelectedItem(null);
            stationselector.setEnabled(false);
            stationselector.setMaximumSize(new Dimension(200,20));
            this.add(stationselector);
            this.add(new JLabel("Seleziona la zona:"));
            zoneselector= new JComboBox(QueryInfo.getInstance().getZone());
            zoneselector.addItemListener(new QuerySelectionListener());
            zoneselector.setSelectedItem(null);
            zoneselector.setEnabled(false);
            zoneselector.setMaximumSize(new Dimension(200,20));
            this.add(zoneselector);
            this.add(new JLabel("Seleziona il fuso orario:"));
            timezoneselector= new JComboBox(QueryInfo.getInstance().getTimezone());
            timezoneselector.addItemListener(new QuerySelectionListener());
            timezoneselector.setSelectedItem(null);
            timezoneselector.setEnabled(false);
            timezoneselector.setMaximumSize(new Dimension(200,20));
            this.add(timezoneselector);
        }

        class QuerySelectionListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox c =(JComboBox<String>) e.getSource();
                    if(type==QueryType.STATION) param= c.getSelectedItem().toString().split("-")[0];
                    else param= c.getSelectedItem().toString();
                    submitButton.setEnabled(true);
                }
            }
        }
    }

    class  submitButtonLister implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(submitButton)){
                if(weatherdistribution.isSelected()){
                    if(datain==null || datafin==null){
                        JOptionPane.showMessageDialog(null, "Devi selezionare le date per il periodo di interesse");
                    }
                    else if (Integer.parseInt(datain)>Integer.parseInt(datafin)) {
                        JOptionPane.showMessageDialog(null, "Il periodo selezionato non è valido");
                        ApplicazioneDatiMetereologiciGUI.getInstance().setView(new WeatherInfoPanel());
                    }
                    Object[][] ris = GraphCreator.getDistriibutionWeatherType(datain,datafin,param);
                    queryResult.add(new WeatherDistributionTableVisualization(ris));
                }
                else if (windchill.isSelected()) {
                    if(datain==null) JOptionPane.showMessageDialog(null, "Devi selezionare le date per il periodo di interesse");
                    queryResult.add(new XChartPanel<>(GraphCreator.getWindChillGraph(datain,type,param)));
                }
                queryResult.setVisible(true);
            }
        }
    }
}
