package SingletonClasses;
import DataApi.DataAPI;
import Enums.QueryType;

import java.math.BigDecimal;
import java.util.Map;

public class QueryInfo {
    static QueryInfo instance=null;
    private String[] fasceorarie;
    private String[] date,months, timezone,states,stations,zone,thresholds;
    private Map<String, QueryType> typeOfQuery;
    private Map<String,String> daylymeasures,hourlymeasures,monthlymeasures;
    private QueryInfo(){
        date = DataAPI.getAllDates();
        timezone = DataAPI.getTimezone();
        states = DataAPI.getStates();
        stations = DataAPI.getStationsName();
        zone = DataAPI.getZone();
        months = DataAPI.getMonths();
        hourlymeasures= Map.of(
                "Temperatura","DryBulbCelsius",
                "Visibilità","Visibility",
                "Precipitazioni","HourlyPrecip",
                "Pressione al livello del mare","SeaLevelPressure",
                "Velocità del vento","WindSpeed"
        );
        daylymeasures= Map.of(
                "Temperatura Massima","Tmax",
                "Temperatura Minima", "Tmin",
                "Temperatura Media", "Tavg",
                "Tramonto","Sunset",
                "Alba","Sunrise",
                "Precipitazioni Nevose","SnowFall",
                "Pressione al livello del mare","SeaLevel",
                "Velocità del vento","AvgSpeed"
        );
        monthlymeasures= Map.of(
                "Temperatura Massima Media","AvgMaxTemp",
                "Temperatura Minima Media","AvgMinTemp",
                "Temperatura Media","AvgTemp",
                "Pressione al livello del mare massima", "MaxSeaLevelPressure",
                "Pressione al livello del mare minima","MinSeaLevelPressure",
                "Precipitazioni","TotalMonthlyPrecip",
                "Precipitazioni nevose", "TotalSnowfall",
                "Velocità del vento","AvgWindSpeed"
        );
        typeOfQuery = Map.of(
                "Zona", QueryType.ZONE,
                "Stato", QueryType.STATE,
                "Fuso orario",QueryType.TZONE,
                "Stazione",QueryType.STATION
        );
        thresholds = new String[] {"0.1","0.5","1","1.5","2","2.5","3.0","3.5","4.0","4.5","5.0","5.5","6","6.5","7.0"};
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
    public String[] getStations(){return stations;}
    public String[] getZone(){return zone;}
    public String[] getMonths(){return months;}
    public String[] getThresholds(){return  thresholds;}
    public Map<String,QueryType> getTypeOfQuery(){return typeOfQuery;}

    public Map<String, String> getDaylymeasures() {
        return daylymeasures;
    }

    public Map<String, String> getHourlymeasures() {
        return hourlymeasures;
    }

    public Map<String, String> getMonthlymeasures() {
        return monthlymeasures;
    }
}
