package Panel.SubPanel;

import Exceptions.NoValuesForParamsException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PeriodQueryTableVisualization extends JPanel {

    public PeriodQueryTableVisualization(Object[][] values, String measure) throws NoValuesForParamsException {
        if (values.length==0) throw new  NoValuesForParamsException();
        this.setMaximumSize(new Dimension(800,600));
        this.setLayout(new BorderLayout());
        JTable t = new JTable(new DefaultTableModel(values, new String[]{"Location","Data","Misura"}));
        t.setEnabled(false);
        t.setFillsViewportHeight(false);
        JScrollPane p = new JScrollPane(t);
        this.add(t.getTableHeader(),BorderLayout.PAGE_START);
        this.add(p,BorderLayout.CENTER);
    }
}
