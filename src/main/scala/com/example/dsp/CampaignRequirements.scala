package com.example.dsp

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
    locations.isEmpty || locations.contains(getLocationFromIp(bidRequest.ip))
  }

  def getLocationFromIp(ip:String) = {
    Thread.sleep(10)
    "DE"
  }
}