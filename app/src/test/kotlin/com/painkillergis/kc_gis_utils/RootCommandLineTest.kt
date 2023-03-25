package com.painkillergis.kc_gis_utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import picocli.CommandLine
import java.sql.DriverManager

class RootCommandLineTest : FunSpec({
  val commandLine = CommandLine(RootCommandLine())
  test("add and populate info column from info api by pin") {
    val db = tempfile()
    val connectionString = "jdbc:sqlite:$db"
    val connection = DriverManager.getConnection(connectionString)
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
    val wireMockServer = WireMockServer()
    wireMockServer.start()
    wireMockServer.stubFor(
      get(urlPathEqualTo("/parcelviewer2/pvinfoquery.ashx")).withQueryParam("pin", equalTo("first pin"))
        .willReturn(aResponse().withBody("""{"identifier":"PIN","items":[{"first":"item"}]}"""))
    )
    wireMockServer.stubFor(
      get(urlPathEqualTo("/parcelviewer2/pvinfoquery.ashx")).withQueryParam("pin", equalTo("second pin"))
        .willReturn(aResponse().withBody("""{"identifier":"PIN","items":[{"second":"item"}]}"""))
    )
    wireMockServer.stubFor(
      get(urlPathEqualTo("/parcelviewer2/pvinfoquery.ashx")).withQueryParam("pin", equalTo("third pin"))
        .willReturn(aResponse().withBody("""{"identifier":"PIN","items":[{"third":"item"}]}"""))
    )
    captureOutput {
      commandLine.execute(
        connectionString,
        tableName,
        pinColumnName,
        infoColumnName,
        wireMockServer.baseUrl()
      )
    } shouldBe Pair(
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
      Pair("first pin", """[{"first":"item"}]"""),
      Pair("second pin", """[{"second":"item"}]"""),
      Pair("third pin", """[{"third":"item"}]"""),
    )
  }
})
