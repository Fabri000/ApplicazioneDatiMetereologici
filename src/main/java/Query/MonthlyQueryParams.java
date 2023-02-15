package Query;

import DataApi.DataAPI;
import DataApi.GraphCreator;
import Enums.QueryPeriod;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.MonthlyQueryTable;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;

public class MonthlyQueryParams extends QueryParams{
    private String measure, monthi,monthf;
    private QueryType type;
    private QueryPeriod period;

    public QueryType getType() {
        return type;
    }

    public QueryPeriod getPeriod() {
        return period;
    }

    public void setParam(String param){
        super.param=param;
    }
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setMonthi(String monthi) {
        this.monthi = monthi;
    }

    public void setMonthf(String monthf) {
        this.monthf = monthf;
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
            case SINGLE_MONTH -> {
                if(measure==null || monthi==null || param == null) throw  new UncompleteQueryParamInitialization();

            }
            case PERIOD_MONTHS -> {
                if(measure==null || monthi == null || monthf == null || param==null) throw new UncompleteQueryParamInitialization();
                if (Integer.parseInt(monthi)>Integer.parseInt(monthf)) throw new WrongDateInitialization();
            }
        }
    }

    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        JPanel ris = null;
        switch (period){
            case SINGLE_MONTH -> {
                ris = new MonthlyQueryTable(measure,DataAPI.getMonthlyMeasure(monthi, measure, type, param));
            }
            case PERIOD_MONTHS -> {
                ris = new XChartPanel<>(GraphCreator.getMonthlyPeriodMeasures(monthi, monthf, measure, type, param));
            }
        }
        return ris;
    }
}
