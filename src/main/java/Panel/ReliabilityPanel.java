package Panel;

import DataApi.DataAPI;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.ReliabilityTableVisualization;
import Query.ReliabilityQueryParams;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ReliabilityPanel extends JPanel {
    private ReliabilityQueryParams params;
    private JCheckBox dayly,hourly,monthly;
    private JComboBox measureselector;
    private JButton submitButton, newResearchButton, returnHomeButton;
    private JPanel queryResult;
    public ReliabilityPanel(){
        params=new ReliabilityQueryParams();
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
        queryResult = new JPanel();
        queryResult.setVisible(false);
        this.add(queryResult);
        newResearchButton = new JButton("Nuova ricerca");
        this.add(UIElemCreator.createNavigationButtonPanel(newResearchButton,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newResearchButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new ReliabilityPanel());
                }
            }
        }));
        this.add(Box.createRigidArea(new Dimension(40,0)));
    }
    class DataSetSelectionPanel extends JPanel{

        ButtonGroup group = new ButtonGroup();
        public DataSetSelectionPanel(){
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setMaximumSize(new Dimension(920,20));
            this.setBorder(new EmptyBorder(0,0,10,0));
            DataSetSelectionListener listener = new DataSetSelectionListener();
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            p1.add(UIElemCreator.createLabel("Seleziona l'arco temporale:"));
            p1.add(Box.createRigidArea(new Dimension(20,0)));
            hourly = UIElemCreator.createCheckBox("Orarie");
            hourly.addActionListener(listener);
            dayly = UIElemCreator.createCheckBox("Giornaliero");
            dayly.addActionListener(listener);
            monthly = UIElemCreator.createCheckBox("Mensile");
            monthly.addActionListener(listener);
            group.add(hourly); group.add(dayly); group.add(monthly);
            p1.add(hourly);
            p1.add(Box.createRigidArea(new Dimension(15,0)));
            p1.add(dayly);
            p1.add(Box.createRigidArea(new Dimension(15,0)));
            p1.add(monthly);
            this.add(p1);
            JPanel p2  = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
            p2.add(UIElemCreator.createLabel("Seleziona la misura:"));
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
                    params.setSet("hourly");
                    params.setParam(null);
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(QueryInfo.getInstance().getHourlymeasures().keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
                else if(e.getSource().equals(dayly)){
                    params.setSet("dayly");
                    params.setParam(null);
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(QueryInfo.getInstance().getDaylymeasures().keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
                else if (e.getSource().equals(monthly)) {
                    params.setSet("monthly");
                    params.setParam(null);
                    measureselector.removeAllItems();
                    measureselector.setModel(new DefaultComboBoxModel(QueryInfo.getInstance().getMonthlymeasures().keySet().toArray(new String[0])));
                    measureselector.setEnabled(true);
                    measureselector.setSelectedItem(null);
                }
                submitButton.setEnabled(false);
            }
        }

        class MeasureSelectionListener implements  ItemListener{
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    JComboBox<String> source = (JComboBox<String>) e.getSource();
                    String val = source.getSelectedItem().toString();
                    if(params.getSet().equals("hourly")) params.setParam(QueryInfo.getInstance().getHourlymeasures().get(val));
                    else if(params.getSet().equals("dayly")) params.setParam(QueryInfo.getInstance().getDaylymeasures().get(val));
                    else if (params.getSet().equals("monthly")) params.setParam(QueryInfo.getInstance().getMonthlymeasures().get(val));
                    submitButton.setEnabled(true);
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
                throw new RuntimeException(ex);
            } catch (WrongDateInitialization ex) {
                throw new RuntimeException(ex);
            } catch (NoValuesForParamsException ex) {
                JOptionPane.showMessageDialog(null, "Nel periodo selezionato non ci sono misurazioni valide per la richiesta");
                ApplicazioneDatiMetereologiciGUI.getInstance().setView(new HourlyMeasurePanel());
            }
            queryResult.setVisible(true);
        }
    }
}