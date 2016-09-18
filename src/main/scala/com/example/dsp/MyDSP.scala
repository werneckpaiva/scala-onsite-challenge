package com.example.dsp

import com.example.RequestLimit

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
