package Panel;

import SingletonClasses.ApplicazioneDatiMetereologiciGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIElemCreator {
    private static Font labelFont = new Font("Calibri",Font.BOLD,20);
    private static Font checkBoxFont = new Font("Calibri", Font.PLAIN,17);
    public static JLabel createLabel(String text){
        JLabel ris = new JLabel(text);
        ris.setOpaque(false);
        ris.setFont(labelFont);
        return ris;
    }

    public static JCheckBox createCheckBox(String text){
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(checkBoxFont);
        checkBox.setMaximumSize(new Dimension(200,20));
        return checkBox;
    }


    public  static  JPanel createSubmitPanel(ActionListener listener, JButton submitButton){
        JPanel submitPanel = new JPanel();
        submitPanel.setMaximumSize( new Dimension(100,40));
        submitButton.addActionListener(listener);
        submitButton.setEnabled(false);
        submitPanel.add(submitButton);
        submitPanel.add(Box.createVerticalStrut(20));
        return submitPanel;
    }

    public static JPanel createNavigationButtonPanel(JButton newResearchButton,ActionListener newQuery){
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));

        JButton returnHomeButton=new JButton("Ritorna alla home");
        returnHomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(returnHomeButton)){
                    ApplicazioneDatiMetereologiciGUI.getInstance().setView(new Dashboard());
                }
            }
        });
        newResearchButton.addActionListener(newQuery);
        buttons.add( newResearchButton );
        buttons.add(Box.createRigidArea(new Dimension(10,0)));
        buttons.add( returnHomeButton);
        return buttons;
    }
}
