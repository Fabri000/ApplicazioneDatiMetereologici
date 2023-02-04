package DataApi

import DataApi.DataAPI.getMeasureInPeriod
import Enums.QueryType
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col
import org.knowm.xchart.XYChart

import java.{lang, util}
import java.util.Date
import scala.jdk.CollectionConverters._
import scala.collection.mutable

object GraphCreator {
  def getDaylyMeasureGraphForDoubleVal(set: String, in: String, fin: String, measure: String, tipo: QueryType, param: String): XYChart= {
    val values = getMeasureInPeriod(set, in, fin, measure, tipo, param).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull));
    values.show()
    val ris = new XYChart(900,700)
    var xAxisVals = new util.HashMap[Int,java.util.ArrayList[Date]]()
    var yAxisVals = new util.HashMap[Int,java.util.ArrayList[java.lang.Double]]()
    var stationNames = new util.HashMap[Int,String]()
    values.collect().map(row=>{
      val code = row.getInt(0)
      stationNames.put(code,row.getString(1))
    })
    values.collect().map(row=>{
      val code = row.getInt(0)
      if(! xAxisVals.keySet.contains(code)){
        xAxisVals.put(code, new util.ArrayList[Date]())
      }
      else{
        val data =row.getInt(2).toString
        var tmp = xAxisVals.get(code)
        tmp.add(new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6)), Integer.parseInt(data.substring(6, 8))))
        xAxisVals.put(code,tmp)
      }
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!yAxisVals.keySet.contains(code)) {
        yAxisVals.put(code, new util.ArrayList[java.lang.Double]())
      }
      else {
        var tmp = yAxisVals.get(code)
        tmp.add(double2Double(java.lang.Double.parseDouble(row.getString(3))))
        yAxisVals.put(code, tmp)
      }
    })
    stationNames.keySet().forEach(x=>{
      ris.addSeries(stationNames.get(x),xAxisVals.get(x),yAxisVals.get(x))
    })
    return ris
  }
  def getDaylyMeasureGraphForDateVal(set: String, in: String, fin: String, measure: String, tipo: QueryType, param: String): Array[Array[Object]]= {
    val values = getMeasureInPeriod(set, in, fin, measure, tipo, param).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull)).collect()
    val datas= new Array[Array[Object]](values.size)
    var i = 0
    values.foreach(row => {
      val r = new Array[Object](3)
      r(0)= row.getString(1)
      val data = row.getInt(2).toString
      r(1)= data.substring(6, 8)+"-"+ data.substring(4, 6) +"-"+data.substring(0, 4)
      r(2) = row.getString(3).substring(0,2)+":"+row.getString(3).substring(2,4)
      datas(i)=r
      i=i+1
    })
    return datas
  }

  def updateMaps(row:Row,stationNames:util.HashMap[String,String], xAxisVals:util.HashMap[String,java.util.ArrayList[Date]], yAxisVals:util.HashMap[String,java.util.ArrayList[java.lang.Double]]): Unit = {
    val code = row.getString(0)
    if (!stationNames.keySet().contains(code)) {
      stationNames.put(code, row.getString(1))
    }
    if (!xAxisVals.keySet().contains(code)) {
      xAxisVals.put(code, new java.util.ArrayList())
    }
    val date = row.getString(2)
    val tmpX = xAxisVals.get(code)
    tmpX.add(new Date(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8))))
    xAxisVals.put(code, tmpX)
    if (!yAxisVals.keySet().contains(code)) {
      yAxisVals.put(code, new util.ArrayList[lang.Double]())
    }
    val tmpY = yAxisVals.get(code)
    tmpY.add(double2Double(java.lang.Double.parseDouble(row.getString(3))))
    yAxisVals.put(code, tmpY)
  }
}