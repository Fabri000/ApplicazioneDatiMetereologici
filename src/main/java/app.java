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
        String dataset,state, misura,station,giorno;
       /*while(true){
            System.out.print("--------------\nInserisci qui il dataset da interrogare:");
            dataset=in.nextLine();
            if(dataset==null) break;
            System.out.print("---------------- \nInserisci qui la data interessata:");
            giorno=in.nextLine();
            System.out.print("---------------- \nInserisci qui la misura interessata:");
            misura=in.nextLine();
            System.out.print("---------------- \nInserisci qui lo stato di interesse:");
            state=in.nextLine();
            Starter.getMeasureState(dataset,giorno,misura,state).show();
        }*/
        Starter.getDatas("stations").select("*").where("State='CO' AND ClimateDivisionCode='02'").show();

        System.out.println("----------\nFine esecuzione\n--------------");
    }
}