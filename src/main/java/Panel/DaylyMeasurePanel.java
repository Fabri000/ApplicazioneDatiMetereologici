package Panel;

import Enums.QueryType;

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

    public DaylyMeasurePanel(){
        this.setSize(new Dimension(1920,1080));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.add(new InitialPanel());
        this.add(Box.createVerticalStrut(200));
    }

    class InitialPanel extends  JPanel{
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
                "Regione", QueryType.DIVCOD,
                "Fuso orario",QueryType.TZONE,
                "Stazione",QueryType.STATION
        );
        private JComboBox<String> measureSelector,typeOfQuerySelector;
        private CustomJComboBoxListener comboBoxListener;

        public InitialPanel(){
            comboBoxListener = new CustomJComboBoxListener(this);
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            JPanel p1 = new JPanel();
            p1.setSize(500,300);
            p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
            JTextField text  = new JTextField("Seleziona la misura interessata:");
            text.setEditable(false);
            text.setMaximumSize(new Dimension(400,25));
            text.setBorder(null);
            p1.add(text);
            measureSelector = new JComboBox<>(possibleMeasures.keySet().toArray(new String[0]));
            measureSelector.setAlignmentX(LEFT_ALIGNMENT);
            measureSelector.setMaximumSize(new Dimension(300,25));
            measureSelector.addItemListener(comboBoxListener);
            p1.add(measureSelector);
            this.add(p1);
            this.add(Box.createHorizontalStrut(200));
            JPanel p2 = new JPanel();
            p2.setSize(500,300);
            p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
            JTextField t2  = new JTextField("Seleziona i parametri di interesse:");
            t2.setEditable(false);
            t2.setMaximumSize(new Dimension(400,25));
            t2.setBorder(null);
            p2.add(t2);
            typeOfQuerySelector = new JComboBox<>(typeOfQuery.keySet().toArray(new String[0]));
            typeOfQuerySelector.setAlignmentX(LEFT_ALIGNMENT);
            typeOfQuerySelector.setMaximumSize(new Dimension(300,25));
            typeOfQuerySelector.addItemListener(comboBoxListener);
            p2.add(typeOfQuerySelector);
            this.add(p2);
        }

        public class CustomJComboBoxListener implements ItemListener {

            private JPanel panel;

            public CustomJComboBoxListener(JPanel panel){
                this.panel=panel;
            }
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()== ItemEvent.SELECTED){
                    JComboBox source = (JComboBox) e.getSource();
                    if(source.equals(measureSelector)){
                        measure= possibleMeasures.get(source.getSelectedItem());
                        measureSelector.setEditable(false);
                        panel.add( new TimePeriodSelectionPanel());
                        panel.repaint();
                    }
                    else if (source.equals(typeOfQuerySelector)){
                        type= typeOfQuery.get(source.getSelectedItem());
                    }
                }
            }
        }
    }


    class TimePeriodSelectionPanel extends JPanel{
        JCheckBox specificdata;
        JCheckBox periodindays;
        JCheckBox dataandhour;
        public TimePeriodSelectionPanel(){
            this.setSize(900,300);
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setAlignmentX(CENTER_ALIGNMENT);
            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
            JTextField text  = new JTextField("Seleziona che tipo di arco temporale vuoi analizzare:");
            text.setEditable(false);
            text.setMaximumSize(new Dimension(400,25));
            text.setBorder(null);
            p1.add(text);
            ActionListener listener = new PeriodSelectionListener(this);
            specificdata = new JCheckBox("Giorno");
            specificdata.addActionListener(listener);
            periodindays = new JCheckBox("Giorni");
            periodindays.addActionListener(listener);
            dataandhour = new JCheckBox("Giorno e fascia oraria");
            dataandhour.addActionListener(listener);
            p1.add(specificdata);p1.add(periodindays);p1.add(dataandhour);
            this.add(p1);
        }

        class PeriodSelectionListener implements ActionListener{
            private JPanel panel;

            public PeriodSelectionListener(JPanel panel){
                this.panel=panel;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox source = (JComboBox) e.getSource();
                if(source.equals(specificdata)){

                }
                else if (source.equals(periodindays)){

                }
                else if (source.equals(dataandhour)){

                }
            }
        }


    }
}
