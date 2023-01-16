
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

import java.util.Scanner;

import static org.apache.spark.sql.functions.col;

public class app {
    public static void main (String[] args){
        SparkSession spark =SparkSession.builder()
                .master( "local")
                .appName("Applicazione dati meteo")
                .getOrCreate();
        Starter.start(spark);

        Scanner in = new Scanner(System.in);
        String query;
        String dataset;
        while(true){
            System.out.println("--------------\nInserisci qui il dataset da interrogare");
            dataset=in.nextLine();
            if(dataset==null) break;
            Dataset f = Starter.getDatas(dataset);
            f.printSchema();
            f.createOrReplaceTempView(dataset);
            System.out.print("---------------- \nInserisci qui la query per le stazioni:");
            query=in.nextLine();
            if(query==null) break;
            System.out.println("La tua query: "+query+"\n");
            spark.sql(query).show();
        }
        System.out.println("----------\nFine esecuzione\n--------------");
    }
}