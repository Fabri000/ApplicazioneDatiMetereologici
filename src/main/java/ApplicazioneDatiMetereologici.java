import DataApi.DataAPI;
import SingletonClasses.ApplicazioneDatiMetereologiciGUI;
import SingletonClasses.QueryInfo;
import org.apache.spark.sql.SparkSession;


public class ApplicazioneDatiMetereologici  {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .config("spark.sql.files.minPartitionBytes", 36000)
                .master( "spark://192.168.56.1:7077")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        DataAPI.start(spark);
        QueryInfo.getInstance();
        ApplicazioneDatiMetereologiciGUI.getInstance().setVisible(true);

    }
}