package Panel.SubPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QueryGraphVisualize extends JPanel {

    public QueryGraphVisualize(String title){
        this.setBorder(new EmptyBorder(50,100,0,100));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        JLabel panelTitle = new JLabel( title);
        this.add(panelTitle);

    }
}
