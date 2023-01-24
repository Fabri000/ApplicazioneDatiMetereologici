
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import QueryType.STATE
import QueryType.TZONE
import QueryType.DIVCOD
import org.apache.spark.sql.functions.{col, lit, round, udf}



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
    stations = s.reduce(_ union _).dropDuplicates()
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

  def getMeasureByDay (set:String, data:String, measure:String, tipo:QueryType , values: String* ):DataFrame={
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
      return df.join(stationOfInterest, "WBAN").select("WBAN",measure).filter("YearMonthDay =" + data)
    }
  }

  def getMeasureInPeriod(set: String, in: String, fin:String, measure:String, tipo: QueryType, values: String*): DataFrame = {
    val df: DataFrame = getDatas(set)
    var date="YearMonthDay"
    if(set=="hourly") date = "Date"
    if (tipo == QueryType.STATION) {
      return df.select(date,measure).filter(date+"<="+fin+" AND "+date+">="+in+" AND WBAN="+ values(0))
    }
    else {
      val query = tipo match {
        case QueryType.STATE => "State='" + values(0) + "'"
        case QueryType.TZONE => "TimeZone=" + values(0)
        case QueryType.DIVCOD => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").select("WBAN", date , measure).filter(date+">=" + in + " AND "+date+"<=" + fin)
    }
  }
  def getMeasureInHourlyPeriod(data: String, in: String, fin: String, measure: String, tipo: QueryType, values:String*): DataFrame = {
    val df: DataFrame = getDatas("hourly")
    if (tipo == QueryType.STATION) {
      return df.select("WBAN","Date","Time",measure).filter("Date=" + data + " AND " + "Time>=" + in + "AND Time<= "+fin+ " AND WBAN=" + values(0))
    }
    else {
      val query = tipo match {
        case QueryType.STATE => "State='" + values(0) + "'"
        case QueryType.TZONE => "TimeZone=" + values(0)
        case QueryType.DIVCOD => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").select("WBAN","Date","Time",measure).filter("Date=" + data + " AND Time>=" + in + " AND Time<= "+fin)
    }
  }
  def getMonthlyMeasure(set:String, month:String, measure:String, tipo:QueryType, values:String*): DataFrame={
    val df: DataFrame = getDatas(set)
    if (tipo == QueryType.STATION) {
      return df.select(measure).filter("YearMonth=" + month + " AND WBAN=" + values(0))
    }
    else {
      val query = tipo match {
        case QueryType.STATE => "State='" + values(0) + "'"
        case QueryType.TZONE => "TimeZone=" + values(0)
        case QueryType.DIVCOD => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").select("WBAN",measure).filter("YearMonth=" + month)
    }
  }
  def getMonthlyMeasureInPeriod(set: String, in: String, fin:String, measure: String, tipo: QueryType, values: String*): DataFrame = {
    val df: DataFrame = getDatas(set)
    if (tipo == QueryType.STATION) {
      return df.select("YearMonth",measure).filter("YearMonth<=" + fin +"YearMonth>="+ in + " AND WBAN=" + values(0))
    }
    else {
      val query = tipo match {
        case QueryType.STATE => "State='" + values(0) + "'"
        case QueryType.TZONE => "TimeZone=" + values(0)
        case QueryType.DIVCOD => "State='" + values(0) + "' AND ClimateDivisionCode='" + values(1) + "'"
      }
      val stationOfInterest = stations.select("WBAN").filter(query)
      return df.join(stationOfInterest, "WBAN").select("WBAN","YearMonth",measure).filter("YearMonth<=" + fin +"YearMonth>="+ in)
    }
  }
  def getReliabilityOfStation(set:String, station:String, measure: String) : Float ={
    val df: DataFrame = getDatas(set)
    return Math.round((1- (df.filter("WBAN ='"+station+"' AND " + measure+"= 'M'").count().toFloat / df.filter("WBAN ='"+station+"'").count()))*100)
  }

  def getReliabilityOfStations(set:String, measure:String): DataFrame={
    val df: DataFrame = getDatas(set)
    val missingValue :DataFrame = df.filter( measure+"= 'M'").groupBy("WBAN").agg(functions.count(measure).as("NumberOfMissing"))
    val other: DataFrame= df.groupBy("WBAN").agg(functions.count(measure).as("NumberOfMeasures"))
    return missingValue.join(other, "WBAN").withColumn("One",lit(1)).withColumn("Reliability", round((col("One") - col("NumberOfMissing").divide(col("NumberOfMeasures")))*100)).select("WBAN","Reliability")
  }

  def getPrecipitationOver(in:String, fin:String,threshold:String, queryType: QueryType, values : String* ):Float={
    val df = getMeasureInPeriod("precip",in,fin,"Precipitation", queryType, values(0),values(1))
    val filtered :Long = df.filter("Precipitation>="+ threshold).count()
    val unfiltered : Long = df.count()
    return Math.round((filtered.toFloat/unfiltered)*100)
  }

  def getDistributionOfWheaterType(in:String,fin:String, stato:String, divcode:String):DataFrame={
    val df = getMeasureInPeriod("hourly",in,fin,"WeatherType", QueryType.DIVCOD, stato, divcode)
    val numMeasures:Long= df.count()
    return df.groupBy("WeatherType").agg(round((functions.count("WBAN")/numMeasures.toFloat)*100).as("Distribution"))
  }

  def getWindChill(date:String, stato:String,divcod:String):DataFrame={
    val windchillcalc = udf(calculateWindChill _)
    //return getMeasureInHourlyPeriod(date,"0000","2355","",QueryType.DIVCOD,stato,divcod).withColumn("WindChill", windchillcalc(col("DryBulbCelsius"),col("WindSpeed"))).select("WBAN","Date",col("Time").toString(),"DryBulbCelsius","WindSpeed","WindChill")
    val stationOfInterest = stations.select("WBAN").filter("State='" + stato + "' AND ClimateDivisionCode='" + divcod + "'")
    return getDatas("hourly").join(stationOfInterest, "WBAN").filter("Date=" + date ).withColumn("WindChill", windchillcalc(col("DryBulbCelsius"),col("WindSpeed"))).select("WBAN","Date",col("Time").toString(),"DryBulbCelsius","WindSpeed","WindChill")
  }

  def calculateWindChill(temperature:Double,speed:Double):Double={
    return 35.74+0.62*temperature-35.75*Math.pow(speed,0.16)+0.4275*temperature*Math.pow(speed,0.16)
  }
}