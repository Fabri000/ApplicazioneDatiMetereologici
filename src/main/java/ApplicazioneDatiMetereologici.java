import org.apache.spark.sql.SparkSession;

import javax.swing.*;


public class ApplicazioneDatiMetereologici  extends JFrame{
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        ApplicazioneDatiMetereologici gui = new ApplicazioneDatiMetereologici();
        gui.setVisible(true);
        //DataAPI.start(spark);
    }

    class ApplicazioneDatiMetereologiciGUI extends JFrame {

        public ApplicazioneDatiMetereologiciGUI(){
            setTitle("Applicazione Dati metereologici");
        }

    }
}

