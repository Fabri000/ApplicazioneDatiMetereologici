import org.apache.spark.sql.SparkSession;

import javax.swing.*;
import javax.xml.crypto.Data;


public class ApplicazioneDatiMetereologici  {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        ApplicazioneDatiMetereologiciGUI gui = new ApplicazioneDatiMetereologiciGUI(new JPanel());
        gui.setVisible(true);

        //DataAPI.start(spark);
    }
}
class ApplicazioneDatiMetereologiciGUI extends JFrame {
    JPanel view=null;
    public ApplicazioneDatiMetereologiciGUI(JPanel view){
        setTitle("Applicazione Dati metereologici");
        this.view=view;
    }

    public void setView(JPanel view){
        this.view=view;
    }

}
