import org.apache.spark.sql.Dataset;
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
        while(true){
            System.out.print("--------------\nInserisci qui il dataset da interrogare:");
            dataset=in.nextLine();
            if(dataset==null) break;
            System.out.print("---------------- \nInserisci qui la data interessata:");
            giorno=in.nextLine();
            System.out.print("---------------- \nInserisci qui la misura interessata:");
            misura=in.nextLine();
            System.out.println("La misura ricercata è: "+misura+"\n");
            System.out.print("---------------- \nInserisci qui lo stato di interesse:");
            state=in.nextLine();
        }
        System.out.println("----------\nFine esecuzione\n--------------");
    }
}