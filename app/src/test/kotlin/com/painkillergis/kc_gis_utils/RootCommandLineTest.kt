package com.painkillergis.kc_gis_utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import picocli.CommandLine

class RootCommandLineTest : FunSpec({
  val commandLine = CommandLine(RootCommandLine())
  test("app has a greeting") {
    val (out, _) = captureOutput { commandLine.execute() }
    out shouldBe "Hello World!\n"
  }
})
