package Panel.SubPanel;

import Exceptions.NoValuesForParamsException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReliabilityTableVisualization extends JPanel {

    public ReliabilityTableVisualization(Object[][] values){
        this.setMaximumSize(new Dimension(800,600));
        this.setLayout(new BorderLayout());
        JTable t = new JTable(new DefaultTableModel(values, new String[]{"Stazione","Affidabilità"}));
        t.setEnabled(false);
        t.setFillsViewportHeight(false);
        JScrollPane p = new JScrollPane(t);
        this.add(t.getTableHeader(),BorderLayout.PAGE_START);
        this.add(p,BorderLayout.PAGE_START);
    }
}
