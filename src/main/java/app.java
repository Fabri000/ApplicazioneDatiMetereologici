import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Dataset$;
import org.apache.spark.sql.SparkSession;
import java.util.Scanner;

public class app {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        Starter.start(spark);
        Starter.getDatas("stations").printSchema();
        Scanner in = new Scanner(System.in);
        String dataset,state, misura,start,fin;
        String[] values = new String[1];
       /*while(true){
            System.out.print("--------------\nInserisci qui il dataset da interrogare:");
            dataset=in.nextLine();
            if(dataset==null) break;
            System.out.print("---------------- \nInserisci qui la data iniziale:");
            start=in.nextLine();
            System.out.print("---------------- \nInserisci qui la data finale:");
            fin=in.nextLine();
            System.out.print("---------------- \nInserisci qui la misura interessata:");
            misura=in.nextLine();
            System.out.print("---------------- \nInserisci qui lo stato di interesse:");
            values[0]=in.nextLine();
            Starter.getMeasureInPeriod(dataset,start,fin,misura,QueryType.STATE, values ).show();
        }*/

        Starter.getReliabilityOfStations("dayly","Tmax").show();
        System.out.println("----------\nFine esecuzione\n--------------");
    }
}