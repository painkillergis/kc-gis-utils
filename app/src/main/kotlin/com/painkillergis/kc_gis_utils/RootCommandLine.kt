package com.painkillergis.kc_gis_utils

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
  override fun run() {
    val connection = DriverManager.getConnection(connectionString)
    connection.createStatement().execute("alter table $tableName add column $infoColumnName json")
    val resultSet =
      connection.createStatement().executeQuery("select $pinColumnName from $tableName")
    val pins = generateSequence {
      if (resultSet.next()) resultSet.getString(1)
      else null
    }
    val statement = connection.prepareStatement("update $tableName set $infoColumnName = ? where $pinColumnName = ?")
    statement.setString(1, "{}")
    pins.forEach { pin ->
      statement.setString(2, pin)
      statement.execute()
    }
  }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(RootCommandLine()).execute(*args))
