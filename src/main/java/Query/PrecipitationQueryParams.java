package Query;

import DataApi.GraphCreator;
import Enums.QueryType;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.PrecipitationTableVisualization;

import javax.swing.*;

public class PrecipitationQueryParams extends QueryParams{
    private String threshold,datai,dataf;
    private QueryType type;

    public QueryType getType() {
        return type;
    }
    public void setParam(String param){
        super.param=param;
    }
    public void setThreshold(String threshold) {
        this.threshold = threshold;
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
        if(param==null || threshold==null || datai == null || dataf == null ) throw  new UncompleteQueryParamInitialization();
        if(Integer.parseInt(datai)>Integer.parseInt(dataf)) throw  new WrongDateInitialization();
    }

    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        JPanel ris = new PrecipitationTableVisualization(GraphCreator.getPrecipitationOverVals(datai, dataf, threshold, type, param));
        return ris;
    }
}
