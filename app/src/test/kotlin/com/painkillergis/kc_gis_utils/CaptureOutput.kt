package com.painkillergis.kc_gis_utils

import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun captureOutput(block: () -> Unit): Pair<String, String> {
  val oldOut = System.out
  val oldErr = System.err
  val outStream = ByteArrayOutputStream()
  val errStream = ByteArrayOutputStream()
  System.setOut(PrintStream(outStream))
  System.setErr(PrintStream(errStream))
  try {
    block()
  } finally {
    System.setOut(oldOut)
    System.setErr(oldErr)
  }
  return Pair(outStream.toString(), errStream.toString())
}