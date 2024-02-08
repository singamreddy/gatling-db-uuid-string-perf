import io.gatling.gradle.LogHttp

plugins {
    java
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id("io.gatling.gradle") version "3.10.3"
}

repositories {
    mavenLocal()
    mavenCentral()
}

gatling {
    // WARNING: options below only work when logback config file isn't provided
    logLevel = "WARN" // logback root level
    logHttp  = LogHttp.NONE // set to 'ALL' for all HTTP traffic in TRACE, 'FAILURES' for failed HTTP traffic in DEBUG
}

dependencies {
    gatling("ru.tinkoff:gatling-jdbc-plugin_2.13:0.10.3")
    gatling("org.postgresql:postgresql:42.7.1")
}