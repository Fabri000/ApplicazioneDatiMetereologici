package DataApi

import Enums.QueryType
import org.apache.spark.sql.functions.{col, lit, round, udf}
import org.apache.spark.sql._

import scala.jdk.CollectionConverters._
import com.datawizards.splot.api.implicits._
import org.knowm.xchart.{QuickChart, SwingWrapper, XYChart}

import java.util
import java.util.{Date, GregorianCalendar}

object DataAPI {
  private var dayly :DataFrame = null
  private var monthly :DataFrame = null
  private var hourly :DataFrame = null
  private var precip :DataFrame = null
  private var remarks :DataFrame = null
  private var stations : DataFrame = null
  def start(spark: SparkSession )={
    val d= new Array[DataFrame](12)
    val m= new Array[DataFrame](12)
    val h= new Array[DataFrame](12)
    val p= new Array[DataFrame](12)
    val r= new Array[DataFrame](12)
    val s= new Array[DataFrame](12)

    /*for (i<- 0 to 11){
      var input="D:\\datimeteorologici\\datimeteo\\QCLCD2013"+ {if( i+1<10 ) "0"+(i+1) else (i+1) } +"\\2013"+{if( (i+1)<10 ) "0"+(i+1) else (i+1) }+"daily.txt"
      d(i)=spark.read.option("header","true").option("inferschema","true").csv(input)
      input="D:\\datimeteorologici\\datimeteo\\QCLCD2013"+ {if( i+1<10 ) "0"+(i+1) else (i+1) } +"\\2013"+{if( (i+1)<10 ) "0"+(i+1) else (i+1) }+"monthly.txt"
      m(i)=spark.read.option("header","true").option("inferschema","true").csv(input)
      input = "D:\\datimeteorologici\\datimeteo\\QCLCD2013" + {if (i + 1 < 10) "0" + (i + 1) else (i + 1)} + "\\2013" + {if ((i + 1) < 10) "0" + (i + 1) else (i + 1)} + "hourly.txt"
      h(i)= spark.read.option("header","true").option("inferschema","true").csv(input)
      input = "D:\\datimeteorologici\\datimeteo\\QCLCD2013" + {if (i + 1 < 10) "0" + (i + 1) else (i + 1)} + "\\2013" + {if ((i + 1) < 10) "0" + (i + 1) else (i + 1)} + "precip.txt"
      p(i) =spark.read.option("header", "true").option("inferschema", "true").csv(input)
      input = "D:\\datimeteorologici\\datimeteo\\QCLCD2013" + {if (i + 1 < 10) "0" + (i + 1) else (i + 1)} + "\\2013" + {if ((i + 1) < 10) "0" + (i + 1) else (i + 1)} + "remarks.txt"
      r(i)= spark.read.option("header", "true").option("inferschema", "true").csv(input)
      input= "D:\\datimeteorologici\\datimeteo\\QCLCD2013" + {if (i + 1 < 10) "0" + (i + 1) else (i + 1)} + "\\2013" + {if ((i + 1) < 10) "0" + (i + 1) else (i + 1)} + "station.txt"
      s(i) = spark.read.option("header", "true").option("inferschema", "true").option("delimiter","|").csv(input)
    }
    dayly = d.reduce(_ union _)
    monthly = m.reduce(_ union _)
    hourly = h.reduce(_ union _)
    precip = p.reduce(_ union _)
    remarks = r.reduce(_ union _)
    stations = s.reduce(_ union _).dropDuplicates()*/
    dayly = spark.read.option("header","true").option("inferschema","true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302daily.txt")
    monthly =spark.read.option("header","true").option("inferschema","true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302monthly.txt")
    hourly=spark.read.option("header","true").option("inferschema","true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302hourly.txt")
    precip = spark.read.option("header","true").option("inferschema","true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302precip.txt")
    remarks = spark.read.option("header","true").option("inferschema","true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302remarks.txt")
    stations = spark.read.option("header", "true").option("inferschema", "true").option("delimiter","|").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302station.txt")
  }
  def getDatas(dataframe:String): DataFrame ={
    val df = dataframe match {
      case "dayly"=> dayly
      case "monthly"=> monthly
      case "hourly"=>hourly
      case "precip"=>precip
      case "remarks"=> remarks
      case "stations" => stations
    }
    return df
  }
  def getMeasureByDay (set:String, data:String, measure:String, tipo:QueryType, param:String ): java.util.Map[String,String]={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN", "Location").filter(generateStationsQuery(tipo, param))
    val ris : Dataset[Row] =  df.join(stationOfInterest, "WBAN").select("Location",measure).filter("YearMonthDay =" + data)
    val m: scala.collection.mutable.Map[String,String] =  scala.collection.mutable.Map[String,String]()
    ris.collect().map(row=> m.put(row.getString(0),row.getString(1)))
    return m.asJava
  }
  def getMeasureInPeriod(set: String, in: String, fin:String, measure:String, tipo: QueryType, param: String):DataFrame = {
    val df: DataFrame = getDatas(set)
    var date="YearMonthDay"
    if(set=="hourly") date = "Date"
    val stationOfInterest = stations.select("WBAN","Location").filter(generateStationsQuery(tipo, param))
    return df.join(stationOfInterest, "WBAN").select("WBAN","Location", date , measure).filter("YearMonthDay>=" + in + " AND  YearMonthDay<=" + fin)
  }
  def getMeasureInHourlyPeriod(data: String, in: String, fin: String, measure: String, tipo: QueryType, param: String): DataFrame = {
    val df: DataFrame = getDatas("hourly")
    val stationOfInterest = stations.select("WBAN").filter(generateStationsQuery(tipo, param)).distinct()
    return df.join(stationOfInterest, "WBAN").select("Name","Date","Time",measure).filter("Date=" + data + " AND Time>=" + in + " AND Time<= "+fin)
  }
  def getMonthlyMeasure(set:String, month:String, measure:String, tipo:QueryType,param:String): DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter(generateStationsQuery(tipo, param))
    return df.join(stationOfInterest, "WBAN").select("Name",measure).filter("YearMonth=" + month)
  }
  def getMonthlyMeasureInPeriod(set: String, in: String, fin:String, measure: String, tipo: QueryType, param:String): DataFrame = {
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter(generateStationsQuery(tipo, param))
    return df.join(stationOfInterest, "WBAN").select("Name","YearMonth",measure).filter("YearMonth<=" + fin +"YearMonth>="+ in)
  }
  def getReliabilityOfStation(set:String, station:String, measure: String) : Float ={
    val df: DataFrame = getDatas(set)
    var stationofinterest:String=null
    stations.select("WBAN").filter("Name='"+ station+"'").collect().map(row=> stationofinterest = row.getString(0))
    return Math.round((1- (df.filter("WBAN ='"+stationofinterest+"' AND " + measure+"= 'M'").count().toFloat / df.filter("WBAN ='"+stationofinterest+"'").count()))*100)
  }
  def getReliabilityOfStations(set:String, measure:String): DataFrame={
    val df: DataFrame = getDatas(set)
    val missingValue :DataFrame = df.filter( measure+"= 'M'").groupBy("WBAN").agg(functions.count(measure).as("NumberOfMissing"))
    val other: DataFrame= df.groupBy("WBAN").agg(functions.count(measure).as("NumberOfMeasures"))
    return missingValue.join(other, "WBAN").withColumn("One",lit(1)).withColumn("Reliability", round((col("One") - col("NumberOfMissing").divide(col("NumberOfMeasures")))*100)).select("WBAN","Reliability")
  }
  def getPrecipitationOver(in:String, fin:String,threshold:String, queryType: QueryType, param:String ):Float={
    val df = getMeasureInPeriod("precip",in,fin,"Precipitation", queryType, param)
    val filtered :Long = df.filter("Precipitation>="+ threshold).count()
    val unfiltered : Long = df.count()
    return Math.round((filtered.toFloat/unfiltered)*100)
  }
  def getDistributionOfWheaterType(in:String,fin:String, stato:String):DataFrame={
    val df = getMeasureInPeriod("hourly",in,fin,"WeatherType", QueryType.STATE, stato)
    val numMeasures:Long= df.count()
    return df.groupBy("WeatherType").agg(round((functions.count("WBAN")/numMeasures.toFloat)*100).as("Distribution"))
  }
  def getWindChill(date:String, stato:String):DataFrame={
    val windchillcalc = udf(calculateWindChill _)
    val stationOfInterest = stations.select("WBAN").filter("State='" + stato )
    return getDatas("hourly").join(stationOfInterest, "WBAN").filter("Date=" + date ).withColumn("WindChill", windchillcalc(col("DryBulbCelsius"),col("WindSpeed"))).select("WBAN","Date",col("Time").toString(),"DryBulbCelsius","WindSpeed","WindChill")
  }
  def calculateWindChill(temperature:Double,speed:Double):Double={
    return 35.74+0.62*temperature-35.75*Math.pow(speed,0.16)+0.4275*temperature*Math.pow(speed,0.16)
  }

  def generateStationsQuery(tipo:QueryType,param:String):String={
    var ris:String=null
    val query = tipo match {
      case QueryType.ZONE => ris = "Name='"+param+"'"
      case QueryType.STATION =>ris = "WBAN=" + param
      case QueryType.STATE =>  ris ="State='" + param + "'"
      case QueryType.TZONE =>  ris ="TimeZone=" + param
    }
    return ris
  }

  def getStationsOfInterest(tipo:QueryType,param:String, reqinfo: String): DataFrame={
    return stations.select("WBAN", reqinfo).filter(generateStationsQuery(tipo, param)).dropDuplicates()
  }
  def getAllDates():Array[String]={
  return dayly.select("YearMonthDay").collect().map(row=>row.getInt(0).toString())
  }
  def getStationsName(): Array[String] ={
    return  stations.filter(col("Location").isNotNull.and(col("WBAN").isNotNull)).select("WBAN","Location").collect().map(row=> row.getInt(0)+"-"+row.getString(1))
  }
  def getStates(): Array[String] = {
    return stations.select("State").distinct().collect().map(row => row.getString(0))
  }
  def getZone():Array[String] = {
    return stations.filter(col("Name").isNotNull.and(col("WBAN").isNotNull)).select("Name").collect().map(row=> row.getString(0))
  }
  def getTimezone(): Array[String] = {
    return stations.select("TimeZone").filter("TimeZone is not Null").distinct().sort().collect().map(row => row.getInt(0).toString())
  }

}