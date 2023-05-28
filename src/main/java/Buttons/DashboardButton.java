package Buttons;

import javax.swing.*;
import java.awt.*;

public class DashboardButton extends JButton {

    public DashboardButton(String buttonLabel){
        this.setText(buttonLabel);
        this.setFont(new Font("Calibri", Font.BOLD,20));
        this.setMaximumSize(new Dimension(400,250));
    }
}
