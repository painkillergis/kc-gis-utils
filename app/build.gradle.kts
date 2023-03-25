plugins {
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:31.1-jre")
  implementation("info.picocli:picocli:4.7.1")
  implementation("org.xerial:sqlite-jdbc:3.41.2.0")
  testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
  testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

application {
  mainClass.set("com.painkillergis.kc_gis_utils.RootCommandLineKt")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
