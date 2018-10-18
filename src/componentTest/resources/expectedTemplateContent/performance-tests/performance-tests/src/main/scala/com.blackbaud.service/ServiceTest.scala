package com.blackbaud.service

import io.gatling.core.Predef.{exec, forever, scenario}
import io.gatling.http.Predef.http
import io.gatling.core.Predef._

class ServiceTest extends Simulation {

  var maxTestRuntimeSec: Int = Option(System.getProperty("max.test.runtime.sec")).map(_.toInt).getOrElse(30)
  var debug = false
  var userCount: Int = Option(System.getProperty("user.count")).map(_.toInt).getOrElse(10)
  var rampUserOverSec: Int = Option(System.getProperty("ramp.user.over.sec")).map(_.toInt).getOrElse(10)
  var pauseBeforeStopSec: Int = Option(System.getProperty("pause.before.stop.sec")).map(_.toInt).getOrElse(1)
  var environment: String = Option(System.getProperty("environment")).getOrElse("local")

  var baseURL = ""
  var requestName = ""
  var scenarioName = "SCENARIO_NAME"

  if(environment.equals("local")) {
    baseURL = "http://localhost:PORT"
    requestName = "REPLACE_ME"
  } else if(environment.equals("test")) {
    baseURL = "TEST_ENV_URL"
    requestName = "REPLACE_ME"
  } else if(environment.equals("prod")) {
    baseURL = "PROD_ENV_URL"
    requestName = "REPLACE_ME"
  }

  val httpConf = http
    .baseURL(baseURL)

  val workflow = exitBlockOnFail {
      group("Blackbaud Authentication") {
        exec(BBAuth.getBBAuthAccessToken(username, password))
          .exec(BBAuth.putAccessTokenInSession())
      }

      group("Test Setup") {
         exec(http("requestName")
            .get("/setupUri")
            .header("Authorization", "${access_token}"))
      }

      .repeat(30) {
         exec(http(requestName)
            .get("yourEndpoint")
            .header("Authorization", "${access_token}"))
      }
    }

  val closed_test_scenario = scenario("Gatling Test").exec(
    forever() {
      exec(workflow)
    }
  )
  // Execute the scenarios
  //---------------------------------------------------------
  setUp(
    closed_test_scenario.inject(atOnceUsers(userCount)).protocols(httpConf)
  ).maxDuration(maxTestRuntimeSec)
}