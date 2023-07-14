package com.exl.cv

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, column}


/**
 * @author ${user.name}
 */
object App {

  
  def main(args : Array[String]) {
    println("Hello World!" )

    val spark: SparkSession = SparkSession.builder()
      .master("local")
      .appName("SparkScalaPoc")
      .getOrCreate()

    System.setProperty("INPUT_FILE_PATH","src/test/resources/")
    val inputFilePath = System.getProperty("INPUT_FILE_PATH")
    val sampleFileDir = "sample_csv/zips.csv"
    val tableName = sampleFileDir.split("/")(1).replace(".csv","")
    val inputFileLocation = inputFilePath+sampleFileDir

    val csvData = spark.read.option("header", "true").option("inferSchema" , "true").csv(inputFileLocation)
    val columns = csvData.columns.map(columnName => columnName
      .replace("_","")
      .replace(".","_")
    ).toSeq

   val newCsvData= csvData.toDF(columns:_*)
    newCsvData.show()

    newCsvData.write
      .format("bigquery")
      .option("temporaryGcsBucket","gs://dataproc_ravi_poc/spark_jar/")
      .mode("overwrite")
      .option("table", s"test.$tableName")
     .save()

  }

}
