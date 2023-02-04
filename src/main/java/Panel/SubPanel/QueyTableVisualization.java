package Panel.SubPanel;

import Exceptions.NoValuesForParamsException;
import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import scala.Tuple2;
import scala.collection.Seq;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueyTableVisualization extends JPanel {
    public  QueyTableVisualization(String measure, Map<String,String> measures) throws NoValuesForParamsException {
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
