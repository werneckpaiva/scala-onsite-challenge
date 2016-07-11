package com.example

trait BidResponse{
  def toJson():String
}

case class BidPositiveResponse(auctionId:String, bidValue:Double) extends BidResponse{
  
  def toJson():String = {
    f"""{
        "auction_id":${auctionId},
        "result": "bid",
        "bid": ${bidValue}%1.3f,
        "currency": "USD",
        "creative": "http://videos-bucket.com/video123.mov",
        "winning_notification": "http://localhost:8080/winner/${auctionId}"
      }"""
  }
}

case class BidNoResponse(auctionId:String) extends BidResponse{
  def toJson():String = {
    s"""{
      "auction_id":${auctionId},
      "result": "no_bid"
    }"""
  }
}

case class BidRequest(auction_id:String, ip:String, bundle_name:String, connection_type:String)

object CampaignRequirements {

  def loadFromFile(fileName: String) = {
    val conf = io.Source.fromFile(fileName).getLines()
    conf.next()
    val allConfig = conf.map(_.split(";")).toList
    val locations = allConfig.map(_(3)).toSet
    val bundleNames = allConfig.map(_(4)).toSet
    val connectionTypes = allConfig.map(_(5)).toSet
    CampaignRequirements(connectionTypes, bundleNames, locations)
  }
}

case class CampaignRequirements(
    connectionTypes:Set[String]=Set.empty[String], 
    bundleNames:Set[String]=Set.empty[String],
    locations:Set[String]=Set.empty[String]){

  def matchRequirements(bidRequest: BidRequest):Boolean = {
    matchConnectionTypeRequirements(bidRequest) && 
      matchBundleNameRequirements(bidRequest) && 
      matchLocationRequirements(bidRequest)
  }

  def matchConnectionTypeRequirements(bidRequest: BidRequest) = {
    !connectionTypes.isEmpty && connectionTypes.contains(bidRequest.connection_type)
  }

  def matchBundleNameRequirements(bidRequest: BidRequest) = {
    !bundleNames.isEmpty && bundleNames.contains(bidRequest.bundle_name)
  }

  def matchLocationRequirements(bidRequest: BidRequest) = {
    !locations.isEmpty && locations.contains(getLocationFromIp(bidRequest.ip))
  }
  
  def getLocationFromIp(ip:String) = {
    Thread.sleep(10)
    "DE"
  }
}


class MyDSP(budget:Double, requirements:CampaignRequirements, rateLimit:Int=0) {

  val random = scala.util.Random
  val MIN_VALUE = 0.035
  val MAX_VALUE = 0.055
 
  private var curBudget = budget

  val requestLimit = new RequestLimit(rateLimit)
  
  val acceptedBids = scala.collection.mutable.HashMap.empty[String, Double]
  
  def requestBid(bidRequest:BidRequest):BidResponse = {
    if (requestLimit.exceeded || runOutOfMoney || !requirements.matchRequirements(bidRequest)){
      return BidNoResponse(bidRequest.auction_id)
    }
    requestLimit.register
    val bidValue = calculateBidValue
    registerAcceptedBid(bidRequest.auction_id, bidValue)
    BidPositiveResponse(bidRequest.auction_id, bidValue)
  }

  def calculateBidValue():Double = {
    MIN_VALUE + (((MAX_VALUE - MIN_VALUE) * random.nextDouble).toInt / 1000.0)
  }

  def runOutOfMoney():Boolean = {
    curBudget < MIN_VALUE
  }

  def currentBudget():Double = {
    curBudget
  }

  def registerAcceptedBid(auction_id:String, bidValue:Double) = {
    acceptedBids += (auction_id -> bidValue)
  }

  def notifyWinning(auction_ID:String):Boolean = {
    val bid = acceptedBids.remove(auction_ID)
    if (bid.isEmpty){
      false
    } else {
      curBudget -= bid.get
      true
    }
  }

}