import DataApi.DataAPI;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;
import org.apache.spark.sql.SparkSession;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;


public class ApplicazioneDatiMetereologici  {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .config("spark.sql.files.minPartitionBytes", 36000)
                .master( "local[*]")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        DataAPI.start(spark);
        QueryInfo.getInstance();
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(ApplicazioneDatiMetereologiciGUI.getInstance());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        ApplicazioneDatiMetereologiciGUI.getInstance().setVisible(true);
    }
}