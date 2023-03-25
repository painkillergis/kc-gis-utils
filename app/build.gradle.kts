plugins {
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:31.1-jre")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

application {
  mainClass.set("com.painkillergis.kc_gis_utils.AppKt")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
