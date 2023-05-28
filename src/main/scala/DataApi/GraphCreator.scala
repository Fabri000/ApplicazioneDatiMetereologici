package DataApi

import DataApi.DataAPI.{getMeasureInHourlyPeriod, getMeasureInPeriod}
import Enums.QueryType
import Exceptions.NoValuesForParamsException
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.col
import org.knowm.xchart
import org.knowm.xchart.XYChart

import java.text.SimpleDateFormat
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
        tmp.add(new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6))-1, Integer.parseInt(data.substring(6, 8))))
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

  def getPrecipitationOverVals(in:String, fin:String,threshold:String, queryType: QueryType, param:String ): Array[Array[Object]] = {
    val result = DataAPI.getPrecipitationOver(in, fin, threshold, queryType, param).collect()
    var i = 0
    val datas = new Array[Array[Object]](result.size)
    result.foreach(row=>{
      val r = new Array[Object](2)
      r(0)= row.getString(0)
      r(1)= row.getDouble(1).toString
      datas(i)=r
      i=i+1
    })
    return datas
  }

  def getDistriibutionWeatherType(in:String, fin:String,state:String):Array[Array[Object]]={
    val result = DataAPI.getDistributionOfWheaterType(in,fin,state).collect()
    var i = 0
    val datas = new Array[Array[Object]](result.size)
    result.foreach(row => {
      val r = new Array[Object](2)
      r(0) = row.getString(0)
      r(1) = row.getDouble(1).toString
      datas(i) = r
      i = i + 1
    })
    return datas
  }
  def getWindChillGraph(date:String,queryType: QueryType,param:String):XYChart={
    val values = DataAPI.getWindChill(date,queryType, param).filter(col("WindChill").isNotNull)
    values.show()
    val ris = new XYChart(900, 700)
    var xAxisVals = new util.HashMap[Int, java.util.ArrayList[Date]]()
    var yAxisVals = new util.HashMap[Int, java.util.ArrayList[java.lang.Double]]()
    var stationNames = new util.HashMap[Int, String]()
    values.collect().map(row => {
      val code = row.getInt(0)
      stationNames.put(code, row.getString(1))
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!xAxisVals.keySet.contains(code)) {
        xAxisVals.put(code, new util.ArrayList[Date]())
      }
      else {
        val data = row.getInt(2).toString
        var hour = 0
        var min = 0
        if (row.getInt(3) < 100) {
          hour = 0
          min = row.getInt(3)
        }
        else if (
          row.getInt(3) < 1000
        ) {
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0, 1))
          min = Integer.parseInt(t.substring(1, 3))
        }
        else {
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0, 2))
          min = Integer.parseInt(t.substring(2, 4))
        }
        var tmp = xAxisVals.get(code)
        tmp.add(new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6)), Integer.parseInt(data.substring(6, 8)), hour, min))
        xAxisVals.put(code, tmp)
      }
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!yAxisVals.keySet.contains(code)) {
        yAxisVals.put(code, new util.ArrayList[java.lang.Double]())
      }
      else {
        var tmp = yAxisVals.get(code)
        tmp.add(row.getDouble(4))
        yAxisVals.put(code, tmp)
      }
    })
    stationNames.keySet().forEach(x => {
      ris.addSeries(stationNames.get(x), xAxisVals.get(x), yAxisVals.get(x))
    })
    return ris
  }

  def getHourlyMeasureGraphForDateVal(data: String, in: String, fin: String, measure: String, tipo: QueryType, param: String): XYChart= {
    val values = getMeasureInHourlyPeriod(data, in, fin, measure, tipo, param).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull))
    val ris = new XYChart(900, 700)
    var xAxisVals = new util.HashMap[Int, java.util.ArrayList[Date]]()
    var yAxisVals = new util.HashMap[Int, java.util.ArrayList[java.lang.Double]]()
    var stationNames = new util.HashMap[Int, String]()
    values.collect().map(row => {
      val code = row.getInt(0)
      stationNames.put(code, row.getString(1))
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!xAxisVals.keySet.contains(code)) {
        xAxisVals.put(code, new util.ArrayList[Date]())
      }
      else {
        val data = row.getInt(2).toString
        var hour=0
        var min = 0
        if(row.getInt(3)<100){
          hour=0
          min=row.getInt(3)
        }
        else if(
          row.getInt(3)<1000
        ){
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0,1))
          min = Integer.parseInt(t.substring(1,3))
        }
        else{
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0, 2))
          min = Integer.parseInt(t.substring(2, 4))
        }
        var tmp = xAxisVals.get(code)
        tmp.add(new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6))-1, Integer.parseInt(data.substring(6, 8)),hour,min))
        xAxisVals.put(code, tmp)
      }
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!yAxisVals.keySet.contains(code)) {
        yAxisVals.put(code, new util.ArrayList[java.lang.Double]())
      }
      else {
        var tmp = yAxisVals.get(code)
        tmp.add(double2Double(java.lang.Double.parseDouble(row.getString(4))))
        yAxisVals.put(code, tmp)
      }
    })
    stationNames.keySet().forEach(x => {
      ris.addSeries(stationNames.get(x), xAxisVals.get(x), yAxisVals.get(x))
    })
    return ris
  }

  def getDaylyMeasureForHourlyPeriod(in: String, fin: String, measure: String, tipo: QueryType, param: String): XYChart = {
    val values = getMeasureInPeriod("hourly",in,fin, measure, tipo, param).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull))
    val ris = new XYChart(1000, 700)
    var xAxisVals = new util.HashMap[Int, java.util.ArrayList[Date]]()
    var yAxisVals = new util.HashMap[Int, java.util.ArrayList[java.lang.Double]]()
    var stationNames = new util.HashMap[Int, String]()
    values.collect().map(row => {
      val code = row.getInt(0)
      stationNames.put(code, row.getString(1))
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!xAxisVals.keySet.contains(code)) {
        xAxisVals.put(code, new util.ArrayList[Date]())
      }
      else {
        val data = row.getInt(2).toString
        var hour = 0
        var min = 0
        if (row.getInt(3) < 100) {
          hour = 0
          min = row.getInt(3)
        }
        else if (
          row.getInt(3) < 1000
        ) {
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0, 1))
          min = Integer.parseInt(t.substring(1, 3))
        }
        else {
          val t = row.getInt(3).toString
          hour = Integer.parseInt(t.substring(0, 2))
          min = Integer.parseInt(t.substring(2, 4))
        }
        var tmp = xAxisVals.get(code)
        tmp.add(new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6))-1, Integer.parseInt(data.substring(6, 8)), hour, min))
        xAxisVals.put(code, tmp)
      }
    })
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!yAxisVals.keySet.contains(code)) {
        yAxisVals.put(code, new util.ArrayList[java.lang.Double]())
      }
      else {
        var tmp = yAxisVals.get(code)
        tmp.add(double2Double(java.lang.Double.parseDouble(row.getString(4))))
        yAxisVals.put(code, tmp)
      }
    })
    stationNames.keySet().forEach(x => {
      ris.addSeries(stationNames.get(x), xAxisVals.get(x), yAxisVals.get(x))
    })
    return ris
  }

  def getMonthlyPeriodMeasures(in: String, fin:String, measure: String, tipo: QueryType, param:String): XYChart={
    val values = DataAPI.getMonthlyMeasureInPeriod(in,fin, measure, tipo, param).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull)).dropDuplicates().orderBy("YearMonth")
    values.show()
    val ris = new XYChart(1000, 700)
    var xAxisVals = new util.HashMap[Int, java.util.ArrayList[Date]]()
    var yAxisVals = new util.HashMap[Int, java.util.ArrayList[java.lang.Double]]()
    var stationNames = new util.HashMap[Int, String]()
    val formatter: SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    values.collect().map(row => {
      val code = row.getInt(0)
      stationNames.put(code, row.getString(1))
    })
    if (stationNames.keySet().isEmpty){
      throw new NoValuesForParamsException()
    }
    values.collect().map(row => {
      val code = row.getInt(0)
      if (!xAxisVals.keySet.contains(code)) {
        xAxisVals.put(code, new util.ArrayList[Date]())
      }
      else {
        var tmp = xAxisVals.get(code)
        tmp.add(formatter.parse(row.getInt(2).toString))
        xAxisVals.put(code, tmp)
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
    stationNames.keySet().forEach(x => {
      ris.addSeries(stationNames.get(x), xAxisVals.get(x), yAxisVals.get(x))
    })
    return ris
  }


}