package com.painkillergis.kc_gis_utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import picocli.CommandLine
import java.sql.DriverManager

class RootCommandLineTest : FunSpec({
  val commandLine = CommandLine(RootCommandLine())
  test("app has a greeting") {
    val db = tempfile()
    val url = "jdbc:sqlite:$db"
    val connection = DriverManager.getConnection(url)
    val tableName = "target_table_name"
    val pinColumnName = "PIN"
    val infoColumnName = "info"
    connection.createStatement().execute(
      """
      create table $tableName (
        $pinColumnName text(10)
      )
      """.trimIndent()
    )
    connection.createStatement().execute(
      """
      insert into $tableName ($pinColumnName)
      values ("first pin"), ("second pin"), ("third pin")
      """.trimIndent()
    )
    captureOutput { commandLine.execute(url, tableName, pinColumnName, infoColumnName) } shouldBe Pair(
      "",
      "",
    )
    val resultSet = connection.createStatement().executeQuery(
      "select $pinColumnName, $infoColumnName from $tableName"
    )
    generateSequence {
      if (resultSet.next()) Pair(resultSet.getString(1), resultSet.getString(2))
      else null
    }.toList() shouldBe listOf(
      Pair("first pin", "{}"),
      Pair("second pin", "{}"),
      Pair("third pin", "{}"),
    )
  }
})
