package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val requirements = CampaignRequirements.loadFromFile("/Users/ricardo/Projects/Sandbox/scala/workspace/scala-onsite-challenge/src/main/resources/my_dsp.csv")
  println(requirements)
//  val requirements = new CampaignRequirements(Set("WiFi"), Set("com.whatsapp"))
  val dsp = new MyDSP(budget=150000, requirements=requirements, rateLimit=10)

  val myRoute =
    path("bid_request") {
      get {
        parameters('auction_id, 'ip, 'bundle_name, 'connection_type) { (auction_id, ip, bundle_name, connection_type) =>
          complete {
            val bidRequest = BidRequest(auction_id, ip, bundle_name, connection_type)
            val bidResponse = dsp.requestBid(bidRequest)
            bidResponse.toJson()
          }
        }
      }
    } ~ path("winner" / Segment) { auction_id =>
      get {
        complete {
          dsp.notifyWinning(auction_id)
          ""
        }
      }
    }
}