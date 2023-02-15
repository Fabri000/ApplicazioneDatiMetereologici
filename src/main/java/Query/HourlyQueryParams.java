package Query;

import DataApi.GraphCreator;
import Enums.QueryPeriod;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;

public class HourlyQueryParams extends QueryParams{
    private String measure, datai,dataf,houri,hourf;
    private QueryType type;
    private QueryPeriod period;

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setDatai(String datai) {
        this.datai = datai;
    }

    public void setDataf(String dataf) {
        this.dataf = dataf;
    }

    public void setHouri(String houri) {
        this.houri = houri;
    }

    public void setHourf(String hourf) {
        this.hourf = hourf;
    }
    public void setParam(String param){
        super.param=param;
    }
    public void setType(QueryType type) {
        this.type = type;
    }

    public void setPeriod(QueryPeriod period) {
        this.period = period;
    }

    public QueryType getType() {
        return type;
    }

    public QueryPeriod getPeriod() {
        return period;
    }

    @Override
    public void verify() throws UncompleteQueryParamInitialization, WrongDateInitialization {
        switch (period){
            case HOUR_PERIOD_FOR_DAY-> {
                if(measure==null || datai==null || houri == null || hourf==null ) throw new UncompleteQueryParamInitialization();
                if(Integer.parseInt(houri)>Integer.parseInt(hourf) & ! hourf.equals("0000")) throw  new WrongDateInitialization();
            }
            case PERIOD_DAYS -> {
                if(measure==null || datai==null || dataf==null ) throw new UncompleteQueryParamInitialization();
                if(Integer.parseInt(datai)>Integer.parseInt(dataf)) throw  new WrongDateInitialization();
            }
        }
    }


    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        JPanel ris = null;
        switch (period){
            case HOUR_PERIOD_FOR_DAY ->{ ris  = new XChartPanel<>(GraphCreator.getHourlyMeasureGraphForDateVal(datai,houri,hourf,measure,type,param));}
            case PERIOD_DAYS -> {ris =new XChartPanel<>(GraphCreator.getDaylyMeasureForHourlyPeriod(datai,dataf,measure,type,param));}
        }
        return ris;
    }
}
