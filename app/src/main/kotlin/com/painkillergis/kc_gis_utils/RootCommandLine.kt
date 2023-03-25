package com.painkillergis.kc_gis_utils

import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command
class RootCommandLine : Runnable {
  override fun run() {
    println("Hello World!")
  }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(RootCommandLine()).execute(*args))
