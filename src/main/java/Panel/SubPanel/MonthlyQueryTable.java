package Panel.SubPanel;

import Exceptions.NoValuesForParamsException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class MonthlyQueryTable extends JPanel {

    public MonthlyQueryTable(Map<String,String> measures, String searched) throws NoValuesForParamsException{
        if(measures.keySet().size()==0) throw  new NoValuesForParamsException();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        Object[][] vals = new Object[measures.keySet().size()][2];
        int i = 0;
        for (String k: measures.keySet()) {
            vals[i][0]=k;
            vals[i][1]=measures.get(k);
            i++;
        }
        JTable ris = new JTable(new DefaultTableModel(vals, new String[]{"Station",searched}));
        ris.setEnabled(false);
        this.add(new JScrollPane(ris));
    }
}
