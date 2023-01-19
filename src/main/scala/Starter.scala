
import org.apache.spark.sql.{DataFrame, SparkSession}
import QueryType.STATE,QueryType.TZONE,QueryType.DIVCOD


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

  def getMeasureByDay (set:String, data:String, measure:String, tipo:QueryType , values: Array[String] ):DataFrame={
    val df: DataFrame = getDatas(set)
    if(tipo == QueryType.STATION){
      return df.select(measure).filter("YearMonthDay="+data+" AND WBAN="+values(0))
    }
    else{
      val query = tipo match {
        case QueryType.STATE => "State='" + values(0) + "'"
        case QueryType.TZONE => "TimeZone=" + values(0)
        case QueryType.DIVCOD => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay =" + data)
    }
  }

  def getMeasureInPeriod(set: String, in: String, fin:String, measure: String, tipo: QueryType, values: Array[String]): DataFrame = {
    val df: DataFrame = getDatas(set)
    if (tipo == QueryType.STATIONP) {
      return df.select(measure).filter("YearMonthDay<="+fin+" AND "+"YearMonthDay>="+in+" AND WBAN="+ values[0])
    }
    else {
      val query = tipo match {
        case QueryType.STATEP => "State='" + values(0) + "'"
        case QueryType.TZONEP => "TimeZone=" + values(0)
        case QueryType.DIVCODP => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay  >=" + in + " AND YearMonthDay <= " + fin)
    }
  }

  def getMeasureInHourlyPeriod(set: String, data: String, in: String, fin: String, measure: String, tipo: QueryType, values: Array[String]): DataFrame = {
    val df: DataFrame = getDatas(set)
    if (tipo == QueryType.STATIONHP) {
      return df.select(measure).filter("YearMonthDay=" + data + " AND " + "Time>=" + in + "AND Time<= "+fin+ " AND WBAN=" + values[0])
    }
    else {
      val query = tipo match {
        case QueryType.STATEHP => "State='" + values(0) + "'"
        case QueryType.TZONEHP => "TimeZone=" + values(0)
        case QueryType.DIVCODHP => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").dropDuplicates().select(measure).filter("YearMonthDay=" + data + " AND " + "Time>=" + in + "AND Time<= "+fin+ " AND WBAN=" + values[0])
    }
  }

}
