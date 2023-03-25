package com.painkillergis.kc_gis_utils

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Parameters
import java.sql.DriverManager
import kotlin.system.exitProcess

@CommandLine.Command
class RootCommandLine : Runnable {
  @Parameters
  private lateinit var connectionString: String

  @Parameters
  private lateinit var tableName: String

  @Parameters
  private lateinit var pinColumnName: String

  @Parameters
  private lateinit var infoColumnName: String

  @Parameters
  private lateinit var infoBaseURL: String

  override fun run() = runBlocking {
    val httpClient = HttpClient { defaultRequest { url(infoBaseURL) } }
    val connection = DriverManager.getConnection(connectionString)
    connection.createStatement().execute("alter table $tableName add column $infoColumnName json")
    val resultSet =
      connection.createStatement().executeQuery("select $pinColumnName from $tableName")
    val pins = generateSequence {
      if (resultSet.next()) resultSet.getString(1)
      else null
    }
    val statement = connection.prepareStatement("update $tableName set $infoColumnName = ? where $pinColumnName = ?")
    pins.forEach { pin ->
      val info = httpClient.get("/parcelviewer2/pvinfoquery.ashx") { parameter("pin", pin) }.bodyAsText()
        .replace(Regex("""^\{"identifier":"PIN","items":"""), "")
        .replace(Regex("}$"), "")
      statement.setString(1, info)
      statement.setString(2, pin)
      statement.execute()
    }
  }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(RootCommandLine()).execute(*args))
