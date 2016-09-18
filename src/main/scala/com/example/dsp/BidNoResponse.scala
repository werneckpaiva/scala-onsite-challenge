package com.example.dsp

case class BidNoResponse(auctionId:String) extends BidResponse{
  def toJson():String = {
    s"""{
      "auction_id":${auctionId},
      "result": "no_bid"
    }"""
  }
}
