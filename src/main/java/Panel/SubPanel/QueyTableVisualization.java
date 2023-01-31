package Panel.SubPanel;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueyTableVisualization extends JPanel {
    public  QueyTableVisualization(String title, Map<String,String> measures){
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        JLabel panelTitle = new JLabel( title);
        Map<String,String> m = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (String k: m.keySet()
             ) {
           sb.append(k+" "+ m.get(k).substring(0,2)+":"+m.get(k).substring(2,4));
           sb.append("\n");
        }
        JTextField table = new JTextField(sb.toString());
        table.setEditable(false);
        this.add(panelTitle);
        this.add(table);
    }
}
