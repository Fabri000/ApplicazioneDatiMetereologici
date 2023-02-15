package Query;

import Exceptions.NoValuesForParamsException;
import Exceptions.UncompleteQueryParamInitialization;
import Exceptions.WrongDateInitialization;

import javax.swing.*;

public abstract class QueryParams {
    String param;

    abstract void verify() throws UncompleteQueryParamInitialization, WrongDateInitialization;
    abstract JPanel createGraph() throws NoValuesForParamsException;
}
