package Query;

import DataApi.DataAPI;
import DataApi.GraphCreator;
import Enums.QueryPeriod;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.PeriodQueryTableVisualization;
import Panel.SubPanel.QueryTableVisualization;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;

public class DaylyQueryParams extends QueryParams {
    private String measure,datai,dataf;
    private QueryType type;
    private QueryPeriod period;
    public DaylyQueryParams(){}

    public QueryType getType() {
        return type;
    }

    public QueryPeriod getPeriod() {
        return period;
    }
    public void setMeasure(String measure) {
        this.measure = measure;
    }
    public void setParam(String param){
        super.param=param;
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

    public void setPeriod(QueryPeriod period) {
        this.period = period;
    }

    @Override
    public void verify() throws UncompleteQueryParamInitialization, WrongDateInitialization {
        switch (period){
            case SINGLE_DAY -> {
                if(type==null || measure==null || datai==null || param == null )throw new UncompleteQueryParamInitialization();
            }
            case PERIOD_DAYS -> {
                if(type==null || measure==null || datai==null || dataf==null){
                    throw new UncompleteQueryParamInitialization();
                }
                if(Integer.parseInt(datai)>Integer.parseInt(dataf)){
                    throw new WrongDateInitialization();
                }
            }
        }
    }

    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        JPanel ris = null;
        switch (period){
            case PERIOD_DAYS -> {
                if(measure.equals("Sunset")|| measure.equals("Sunrise")) {
                    ris = new PeriodQueryTableVisualization(measure,GraphCreator.getDaylyMeasureGraphForDateVal("dayly", datai, dataf, measure, type, param));
                }
                else {ris = new XChartPanel<>(GraphCreator.getDaylyMeasureGraphForDoubleVal("dayly",datai,dataf,measure,type,param));}
            }
            case SINGLE_DAY -> {
                ris= new QueryTableVisualization(measure, DataAPI.getMeasureByDay("dayly", datai, measure, type, param));
            }
        }
        return ris;
    }
}
