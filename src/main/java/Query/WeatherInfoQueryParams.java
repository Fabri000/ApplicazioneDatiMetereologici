package Query;

import DataApi.GraphCreator;
import Enums.PossibleWeatherInfo;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.WeatherDistributionTableVisualization;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;

public class WeatherInfoQueryParams extends QueryParams{
    private String datai,dataf;
    private QueryType type;
    private PossibleWeatherInfo info;

    public PossibleWeatherInfo getInfo() {
        return info;
    }


    public QueryType getType() {
        return type;
    }

    public void setInfo(PossibleWeatherInfo info) {
        this.info = info;
    }
    public void setParam(String param){
        super.param = param;
    }
    public void setDatai(String datai) {
        this.datai = datai;
    }

    public void setDataf(String dataf) {
        this.dataf = dataf;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    @Override
    public void verify() throws UncompleteQueryParamInitialization, WrongDateInitialization {
        switch (info){
            case WINDCHILL -> {
                if(datai==null || param==null ) throw new UncompleteQueryParamInitialization();
            }
            case WEATHER_DISTRIBUTION -> {
                if(datai==null || dataf == null || param==null) throw  new UncompleteQueryParamInitialization();
                if( Integer.parseInt(datai)>Integer.parseInt(dataf)) throw new WrongDateInitialization();
            }
        }
    }

    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        JPanel ris = null;
        switch (info){
            case WEATHER_DISTRIBUTION -> {
                ris =new WeatherDistributionTableVisualization(GraphCreator.getDistriibutionWeatherType(datai,dataf,param));
            }
            case WINDCHILL -> {
                ris = new XChartPanel<>(GraphCreator.getWindChillGraph(datai,type,param));
            }
        }
        return ris;
    }
}
