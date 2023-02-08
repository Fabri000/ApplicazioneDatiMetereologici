package Panel.SubPanel;
import Exceptions.NoValuesForParamsException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
public class QueryTableVisualization extends JPanel {
    public QueryTableVisualization(String measure, Map<String,String> measures) throws NoValuesForParamsException {
        if(measures.keySet().size()==0) throw  new NoValuesForParamsException();
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        Object[][] vals = new Object[measures.keySet().size()][2];
        int i = 0;
        for (String k: measures.keySet()) {
            vals[i][0]=k;
            if(measure.equals("Sunset")|| measure.equals("Sunrise")) {
                String val = measures.get(k).toString();
                vals[i][1]=val.substring(0,2)+":"+val.substring(2,4);
            }
            else  vals[i][1]=measures.get(k);
            i++;
        }
        JTable ris = new JTable(new DefaultTableModel(vals, new String[]{"Location",measure}));
        ris.setEnabled(false);
        this.add(new JScrollPane(ris));
    }
}