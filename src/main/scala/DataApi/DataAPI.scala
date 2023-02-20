package DataApi

import Enums.QueryType
import org.apache.spark.sql.functions.{col, count, lit, round, udf}
import org.apache.spark.sql._

import scala.jdk.CollectionConverters._

import java.util

object DataAPI {
  private var dayly :DataFrame = null
  private var monthly :DataFrame = null
  private var hourly :DataFrame = null
  private var precip :DataFrame = null
  private var remarks :DataFrame = null
  private var stations : DataFrame = null
  private var sparkSession: SparkSession =null
  def start(spark: SparkSession )={
    sparkSession=spark
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
    dayly = d.reduce(_ union _).cache()
    monthly = m.reduce(_ union _).cache()
    hourly = h.reduce(_ union _).cache()
    precip = p.reduce(_ union _).cache()
    remarks = r.reduce(_ union _).cache()
    stations = s.reduce(_ union _).dropDuplicates().cache()*/
    dayly = spark.read.option("header", "true").option("inferschema", "true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302daily.txt")
    monthly = spark.read.option("header", "true").option("inferschema", "true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302monthly.txt")
    hourly = spark.read.option("header", "true").option("inferschema", "true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302hourly.txt")
    precip = spark.read.option("header", "true").option("inferschema", "true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302precip.txt")
    remarks = spark.read.option("header", "true").option("inferschema", "true").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302remarks.txt")
    stations = spark.read.option("header", "true").option("inferschema", "true").option("delimiter", "|").csv("D:\\datimeteorologici\\datimeteo\\QCLCD201302\\201302station.txt")
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
    val stationOfInterest = getStationsOfInterest(tipo,param,"Location")
    val ris : Dataset[Row] =  df.join(stationOfInterest, "WBAN").select("Location",measure).filter("YearMonthDay =" + data).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull))
    val m: scala.collection.mutable.Map[String,String] =  scala.collection.mutable.Map[String,String]()
    sparkSession.sparkContext.parallelize(ris.collect(),4).map(row=> m.put(row.getString(0),row.getString(1)))
    return m.asJava
  }
  def getMeasureInPeriod(set: String, in: String, fin:String, measure:String, tipo: QueryType, param: String):DataFrame = {
    val df: DataFrame = getDatas(set)
    var date="YearMonthDay"
    val stationOfInterest = getStationsOfInterest(tipo,param,"Location")
    if(set=="hourly"){
      date = "Date"
      return df.join(stationOfInterest, "WBAN").select("WBAN","Location", date ,"Time", measure).filter(date+">=" + in + " AND "+  date+"<=" + fin)
    }
    else {
      return df.join(stationOfInterest, "WBAN").select("WBAN","Location", date , measure).filter(date+">=" + in + " AND "+  date+"<=" + fin)
    }
  }
  def getMeasureInHourlyPeriod(data: String, in: String, fin: String, measure: String, tipo: QueryType, param: String): DataFrame = {
    val df: DataFrame = getDatas("hourly")
    val stationOfInterest = getStationsOfInterest(tipo,param,"Location").filter(generateStationsQuery(tipo, param)).distinct()
    return df.join(stationOfInterest, "WBAN").select("WBAN","Location","Date","Time",measure).filter("Date=" + data + " AND Time>=" + in + " AND Time<= "+fin)
  }
  def getMonthlyMeasure( month:String, measure:String, tipo:QueryType,param:String): util.Map[String,String]={
    val df: DataFrame = getDatas("monthly")
    val stationOfInterest = getStationsOfInterest(tipo,param,"Location").filter(generateStationsQuery(tipo, param))
    val ris : Dataset[Row] = df.join(stationOfInterest, "WBAN").select("Location",measure).filter("YearMonth=" + month).filter(col(measure).notEqual("-").and(col(measure).notEqual("M")).and(col(measure).isNotNull))
    val m: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map[String, String]()
    sparkSession.sparkContext.parallelize(ris.collect(),4).map(row => m.put(row.getString(0), row.getString(1)))
    return m.asJava
  }
  def getMonthlyMeasureInPeriod( in: String, fin:String, measure: String, tipo: QueryType, param:String): DataFrame = {
    val df: DataFrame = getDatas("monthly")
    val stationOfInterest = getStationsOfInterest(tipo,param,"Location").filter(generateStationsQuery(tipo, param))
    return df.join(stationOfInterest, "WBAN").select("WBAN","Location","YearMonth",measure).filter("YearMonth <= " + fin +" AND YearMonth >= "+ in)
  }
  def getReliabilityOfStations(set:String, measure:String): Array[Array[String]]={
    val df: DataFrame = getDatas(set)
    val missingValue :DataFrame = df.filter( measure+"= 'M'").groupBy("WBAN").agg(functions.count(measure).as("NumberOfMissing"))
    val other: DataFrame = df.groupBy("WBAN").agg(functions.count(measure).as("NumberOfMeasures"))
    val ris : DataFrame = missingValue.join(other, "WBAN").withColumn("One",lit(1)).withColumn("Reliability", round((col("One") - col("NumberOfMissing").divide(col("NumberOfMeasures")))*100)).select("WBAN","Reliability")
    val stationsName = getDatas("stations").select("WBAN","Location").dropDuplicates()
    return ris.join(stationsName, "WBAN").select("Location","Reliability").collect().map(row => Array(row.getString(0),row.getDouble(1).toString))
  }
  def getPrecipitationOver(in:String, fin:String,threshold:String, queryType: QueryType, param:String ):DataFrame={
    val df = getMeasureInPeriod("precip",in,fin,"Precipitation", queryType, param)
    val filtered :DataFrame = df.filter("Precipitation>="+ threshold).groupBy("WBAN").agg(count("WBAN").as("Filtered"))
    val unfiltered : DataFrame = df.groupBy("WBAN").count()
    val calculate = udf( calculatePercentage _)
    val partial:DataFrame = filtered.join(unfiltered,"WBAN").withColumn("Percentage", calculate(col("Filtered"),col("count")))
    val stationsNames : DataFrame = getStationsOfInterest(queryType,param,"Location")
    return partial.join(stationsNames,"WBAN").select("Location","Percentage")
  }

  def calculatePercentage(over:Double, total: Double):Double={
    return Math.round((over/total)*100)
  }

  def getDistributionOfWheaterType(in:String,fin:String, stato:String):DataFrame={
    val df = getMeasureInPeriod("hourly",in,fin,"WeatherType", QueryType.STATE, stato)
    val numMeasures:Long= df.count()
    return df.groupBy("WeatherType").agg(round((functions.count("WBAN")/numMeasures.toFloat)*100).as("Distribution"))
  }
  def getWindChill(date:String, queryType: QueryType, param:String):DataFrame={
    val windchillcalc = udf(calculateWindChill _)
    val stationOfInterest = getStationsOfInterest(queryType,param,"Location")
    return getDatas("hourly").join(stationOfInterest, "WBAN").filter("Date=" + date ).withColumn("WindChill", windchillcalc(col("DryBulbCelsius"),col("WindSpeed"))).select("WBAN","Location","Date",col("Time").toString(),"WindChill")
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
  return dayly.select("YearMonthDay").dropDuplicates("YearMonthDay").collect().map(row=>row.getInt(0).toString())
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
  def getMonths():Array[String]={
    return monthly.select("YearMonth").dropDuplicates().collect().map(row=>row.getInt(0).toString)
  }
}