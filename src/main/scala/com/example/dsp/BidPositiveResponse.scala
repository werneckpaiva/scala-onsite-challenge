package com.example.dsp

/**
  * Created by ricardo on 9/18/16.
  */
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
