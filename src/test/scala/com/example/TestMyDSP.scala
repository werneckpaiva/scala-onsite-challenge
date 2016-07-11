package com.example

import org.junit.Test
import org.junit.Assert



class TestMyDSP {

  @Test
  def testMatchesRequirements:Unit = {
    val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
    val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "WiFi")
    val result = dsp.requestBid(bidRequest)
    Assert.assertEquals(classOf[BidPositiveResponse], result.getClass)
  }
  
  @Test
  def testDoesntMatchRequirements:Unit = {
    val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
    val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "3G")
    val result = dsp.requestBid(bidRequest)
    Assert.assertEquals(classOf[BidNoResponse], result.getClass)
  }
  
  @Test
  def testAvoidSpendMoreMoneyThanCurrentBudget:Unit = {
    val dsp = new MyDSP(0.01, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
    val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "3G")
    val result = dsp.requestBid(bidRequest)
    Assert.assertEquals(classOf[BidNoResponse], result.getClass)
  }

  @Test
  def testWinningNotification:Unit = {
    val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
    val bidRequest = BidRequest("123", "84.184.252.79", "com.whatsapp", "WiFi")
    val bidResponse = dsp.requestBid(bidRequest)
    val result = dsp.notifyWinning("123")
    Assert.assertTrue(result)
    
    val bidValue = bidResponse.asInstanceOf[BidPositiveResponse].bidValue
    Assert.assertEquals(1000 - bidValue, dsp.currentBudget, 0)
    
  }

  @Test
  def testWinningNotificationInvalidAuction:Unit = {
    val dsp = new MyDSP(1000, new CampaignRequirements(Set("WiFi"), Set("com.whatsapp")))
    val result = dsp.notifyWinning("123")
    Assert.assertFalse(result)
  }
}