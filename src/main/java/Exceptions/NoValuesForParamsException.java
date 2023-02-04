package Exceptions;

public class NoValuesForParamsException extends Exception{
    public NoValuesForParamsException(){
        System.out.println("Non ci sono misurazioni valide per i valori specificati");
    }
}
