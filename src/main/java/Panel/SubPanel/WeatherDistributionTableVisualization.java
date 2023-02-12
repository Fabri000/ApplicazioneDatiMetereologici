package Panel.SubPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WeatherDistributionTableVisualization extends JPanel {

    public WeatherDistributionTableVisualization(Object[][] values){
        this.setLayout(new BorderLayout());
        JTable t = new JTable(new DefaultTableModel(values, new String[]{"Tipo meteo","percentuale giorni"}));
        t.setEnabled(false);
        t.setFillsViewportHeight(false);
        JScrollPane p = new JScrollPane(t);
        this.add(t.getTableHeader(),BorderLayout.PAGE_START);
        this.add(p,BorderLayout.PAGE_START);
    }
}
