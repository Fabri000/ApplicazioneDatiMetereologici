package SingletonClasses;
import DataApi.DataAPI;

import java.util.Map;

public class QueryInfo {
    static QueryInfo instance=null;
    private String[] fasceorarie;
    private String[] date, timezone,states,stations,divcode;
    private QueryInfo(){
        date = DataAPI.getAllDates();
        timezone = DataAPI.getTimezone();
        states = DataAPI.getStates();
        stations = DataAPI.getStationsName();
        createFasceOrarie();
    }
    public static QueryInfo getInstance(){
        if(instance==null) instance=new QueryInfo();
        return  instance;
    }
    public void createFasceOrarie(){
        fasceorarie= new String[24*2];
        int j = 0;
        for(int i = 0; i<24;i++){
            if(i<10){
                fasceorarie[j]="0"+i+":00";
                j++;
                fasceorarie[j]="0"+i+":30";
                j++;
            }
            else {
                fasceorarie[j]=i+":00";
                j++;
                fasceorarie[j]=+i+":30";
                j++;
            }
        }
    }
    public String[] getFasceorarie(){
        return fasceorarie;
    }
    public String[] getDate() {
        return date;
    }
    public String[] getStates() {
        return states;
    }
    public String[] getTimezone() {
        return timezone;
    }

    public String[] getDivcode() {return divcode;}
    public String[] getStations(){return stations;}
}
