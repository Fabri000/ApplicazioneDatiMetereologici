
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
        Dataset f = Starter.getDatas("stations");
        System.out.println("Numero righe "+f.count()+"\n");
        f.printSchema();
        f.createOrReplaceTempView("stations");

        Scanner in = new Scanner(System.in);
        String query;
        while(true){
            System.out.print("---------------- \nInserisci qui la query per le stazioni:");
            query=in.nextLine();
            if(query==null) break;
            System.out.println("La tua queruy: "+query+"\n");
            spark.sql(query).show();
        }
    }
}