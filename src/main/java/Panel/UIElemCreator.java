package Panel;

import SingletonClasses.ApplicazioneDatiMetereologiciGUI;

import javax.swing.*;
import java.awt.*;

public class UIElemCreator {
    private static Font labelFont = new Font("Calibri",Font.BOLD,20);
    private static Font checkBoxFont = new Font("Calibri", Font.PLAIN,17);
    public static JLabel createLabel(String text){
        JLabel ris = new JLabel(text);
        ris.setFont(labelFont);
        return ris;
    }

    public static JCheckBox createCheckBox(String text){
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(checkBoxFont);
        checkBox.setMaximumSize(new Dimension(200,20));
        return checkBox;
    }
}
