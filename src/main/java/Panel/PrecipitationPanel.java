package Panel;
import DataApi.GraphCreator;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.PrecipitationTableVisualization;
import Query.PrecipitationQueryParams;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;
import org.apache.spark.internal.config.UI;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PrecipitationPanel extends JPanel {
    private PrecipitationQueryParams params;
    private JComboBox datain,datafin,thresholdselector;
    private JComboBox  stateselectionbox, timezoneselectionbox, stationselectionbox, zoneselectionbox;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public PrecipitationPanel(){
        params = new PrecipitationQueryParams();
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
        newResearchButton = new JButton("Nuova ricerca");
        this.add(UIElemCreator.createNavigationButtonPanel(newResearchButton,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new PrecipitationPanel());
                }
            }
        }));
        this.add(Box.createRigidArea(new Dimension(40,0)));
    }
    class ThresholdsSelectionPanel extends JPanel{
        public ThresholdsSelectionPanel(){
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setMaximumSize(new Dimension(800,20));
            this.add(UIElemCreator.createLabel("Seleziona il valore minimo di precipitazioni:"));
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
                  params.setThreshold(thresholdselector.getSelectedItem().toString());
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
            this.add(UIElemCreator.createLabel("Seleziona data iniziale:"));
            this.add(Box.createRigidArea(new Dimension( 20,0)));
            datain = new JComboBox(QueryInfo.getInstance().getDate());
            datain.setSelectedItem(null);
            datain.addItemListener(listener);
            datain.setMaximumSize(new Dimension(200,20));
            this.add(datain);
            this.add(Box.createRigidArea(new Dimension(100,0)));
            this.add(UIElemCreator.createLabel("Seleziona data finale:"));
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
                        params.setDatai(val);
                    } else if (source.equals(datafin)) {
                        params.setDataf(val);
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
            this.add(UIElemCreator.createLabel("Seleziona i parametri di interesse:"));
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
    class SelectionQueryParamsPanel extends JPanel{
        public SelectionQueryParamsPanel(){
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setSize(1000,20);
            this.setBorder(new EmptyBorder(0,0,10,0));
            SelectionQueryParamsListener listener = new SelectionQueryParamsListener();
            JLabel l2 = new JLabel();
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
            timezoneselectionbox.setMaximumSize(new Dimension(200,20));
            timezoneselectionbox.setEnabled(false);
            this.add(timezoneselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(UIElemCreator.createLabel("Seleziona uno stato:"));
            this.add(Box.createRigidArea(new Dimension(10,0)));
            stateselectionbox.setSelectedItem(null);
            stateselectionbox.addItemListener(listener);
            stateselectionbox.setMaximumSize(new Dimension(200,20));
            stateselectionbox.setEnabled(false);
            this.add(stateselectionbox);
            this.add(Box.createRigidArea(new Dimension(15,0)));
            this.add(UIElemCreator.createLabel("Seleziona una zona:"));
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
            try {
                params.verify();
                queryResult.add(params.createGraph());
            } catch (UncompleteQueryParamInitialization ex) {
                JOptionPane.showMessageDialog(null, "Alcuni parametri della query non sono stati inizializati");
            } catch (WrongDateInitialization ex) {
                JOptionPane.showMessageDialog(null, "Il periodo selezionato non è valido");
                params.setDatai(null);
                datain.setSelectedItem(null);
                params.setDataf(null);
                datafin.setSelectedItem(null);
            } catch (NoValuesForParamsException ex) {
                JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new PrecipitationPanel());
            }
            queryResult.setVisible(true);
        }
    }
}
