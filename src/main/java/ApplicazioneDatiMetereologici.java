import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import org.apache.spark.sql.SparkSession;


public class ApplicazioneDatiMetereologici  {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();

        ApplicazioneDatiMetereologiciGUI.getInstance().setVisible(true);
        //DataAPI.start(spark);
    }
}

