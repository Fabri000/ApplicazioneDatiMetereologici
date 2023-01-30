import DataApi.DataAPI;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;
import org.apache.spark.sql.SparkSession;


public class ApplicazioneDatiMetereologici  {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        DataAPI.start(spark);
        QueryInfo.getInstance();
        ApplicazioneDatiMetereologiciGUI.getInstance().setVisible(true);

    }
}

