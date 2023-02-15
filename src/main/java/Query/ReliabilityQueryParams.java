package Query;

import DataApi.DataAPI;
import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;
import Panel.SubPanel.ReliabilityTableVisualization;

import javax.swing.*;

public class ReliabilityQueryParams extends QueryParams{
    private String set;

    public String getSet() {
        return set;
    }

    public void setParam(String param){
        super.param=param;
    }

    public void setSet(String set) {
        this.set = set;
    }

    @Override
    public void verify() throws UncompleteQueryParamInitialization, WrongDateInitialization {
        if(set==null || param==null) throw  new UncompleteQueryParamInitialization();
    }

    @Override
    public JPanel createGraph() throws NoValuesForParamsException {
        return new ReliabilityTableVisualization(DataAPI.getReliabilityOfStations(set,param));
    }
}
