package com.example

import com.example.dsp._
import org.specs2.mutable.Specification


class MyDSPSpec extends Specification {

  "Testing MyDSP" >> {

    "positive bid response when request matches all requirements" >> {
      val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
      val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "WiFi")
      val result = dsp.requestBid(bidRequest)

      result must haveClass[BidPositiveResponse]
    }

    "negative bid response when request does not match connection requirement" >> {
      val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
      val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "3G")
      val result = dsp.requestBid(bidRequest)
      result must haveClass[BidNoResponse]
    }

    "Avoid spend more money than current budget" >> {
      val dsp = new MyDSP(0.01, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
      val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "3G")
      val result = dsp.requestBid(bidRequest)
      result must haveClass[BidNoResponse]
    }

    "Success when notify winning" >> {
      val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
      val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "WiFi")
      val bidResponse = dsp.requestBid(bidRequest)
      val result = dsp.notifyWinning("123")
      result must beTrue

      val bidValue = bidResponse.asInstanceOf[BidPositiveResponse].bidValue
      val remaining = (1000 - bidValue)
      remaining must be equalTo dsp.currentBudget
    }

    "Failure when notify invalid winning" >> {
      val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
      val result = dsp.notifyWinning("123")
      result must beFalse
    }
  }

}