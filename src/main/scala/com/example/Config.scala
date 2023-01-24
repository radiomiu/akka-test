package com.example

import com.typesafe.config.ConfigFactory

trait Config {
  lazy val conf = ConfigFactory.load()

  lazy val secret   = conf.getConfig("auth").getString("secret")

  lazy val http      = conf.getConfig("http")
  lazy val interface = http.getString("interface")
  lazy val port      = http.getInt("port")

  lazy val timeout = conf.getConfig("routes").getDuration("ask-timeout")

  lazy val mapboxUrl = conf.getConfig("routes").getConfig("mapbox").getString("url")
  lazy val mapboxToken = conf.getConfig("routes").getConfig("mapbox").getString("token")
}

object Config extends Config