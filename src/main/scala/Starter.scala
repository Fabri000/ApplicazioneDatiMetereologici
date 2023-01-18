
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}


object Starter {
  var dayly :DataFrame = null
  var monthly :DataFrame = null
  var hourly :DataFrame = null
  var precip :DataFrame = null
  var remarks :DataFrame = null
  var stations : DataFrame = null
  def start(spark: SparkSession )={
    val d= new Array[DataFrame](12)
    val m= new Array[DataFrame](12)
    val h= new Array[DataFrame](12)
    val p= new Array[DataFrame](12)
    val r= new Array[DataFrame](12)
    val s= new Array[DataFrame](12)

    for (i<- 0 to 11){
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
    stations = s.reduce(_ union _)
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

  def getStationMeasursByDay(set:String, data:String , measure:String, station:String):DataFrame={
      val df:DataFrame= getDatas(set)
      return df.select(measure).filter("YearMonthDay="+data+" AND WBAN="+station)
  }

  def getStationMeasureInPeriod(set:String, measure:String,station:String, in:String, fin:String):DataFrame={
    val df:DataFrame=getDatas(set)
    return df.select(measure).filter("YearMonthDay<="+fin+" AND "+"YearMonthDay>="+in+" AND WBAN="+station)
  }

  def getStationDayHourlyMeasureInPeriod(set: String, data:String, measure: String, station: String, in: String, fin: String): DataFrame = {
    val df: DataFrame = getDatas(set)
    return df.select(measure).filter("YearMonthDay="+data+" AND Time<=" + fin + " AND " + "Time>=" + in + " AND WBAN=" + station)
  }

  def getMeasureStateByDay(set:String, data:String, measure:String,state:String):DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select( "WBAN").filter("State='"+ state+"'")
    return df.join(stationOfInterest,  "WBAN").dropDuplicates().select(measure).filter("YearMonthDay="+data)
  }

  def getMeasureStateInPeriod(set:String, measure:String, state:String, in:String, fin:String):DataFrame={
    val df : DataFrame=getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("State='" + state + "'")
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay<="+fin+" AND YearMonthDay>="+in)
  }

  def getMeasureStateDayHourlyInPeriod(set: String, data:String, measure: String, state: String, in: String, fin: String): DataFrame ={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("State='" + state + "'")
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay=" + data+ " AND Time<=" + fin + " AND " + "Time>=" + in )
  }

  def getMeasureClimateDivisionByDay(set:String, data:String,measure:String, state:String, divcod:String): DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("State='" + state + "' AND ClimateDivisionCode='"+divcod+"'")
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay=" + data)
  }

  def getMeasureClimateDivisionInPeriod(set:String,measure:String, state:String, divcod:String, in: String, fin: String):DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("State='" + state + "' AND ClimateDivisionCode='"+divcod+"'")
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay  >="+in+" AND YearMonthDay <= "+fin )
  }
  def getMeasureClimateDivisionHourlyInPeriod(set:String,data:String,measure:String, state:String, divcod:String, in: String, fin: String): DataFrame = {
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("State='" + state + "' AND ClimateDivisionCode='" + divcod + "'")
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay ="+data+" AND Time <= "+fin+" AND Time >="+in )
  }

  def getMeasureTimezoneByDay(set:String, data:String, measure:String, tzone:String ): DataFrame = {
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("TimeZone="+tzone)
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay ="+data)
  }

  def getMeasureTimezoneInPeriod(set:String,measure:String, tzone:String, in: String, fin: String):DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("Timezone="+tzone)
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay  >=" + in + " AND YearMonthDay <= " + fin)
  }

  def  getMeasureTimezoneHourlyInPeriod(set:String,data:String,measure:String, tzone:String, in: String, fin: String):DataFrame={
    val df: DataFrame = getDatas(set)
    val stationOfInterest = stations.select("WBAN").filter("Timezone=" + tzone)
    return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay  >=" + in + " AND YearMonthDay <= " + fin)
  }

}