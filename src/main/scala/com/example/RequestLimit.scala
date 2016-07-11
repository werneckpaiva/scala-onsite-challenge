package com.example
import scala.collection.mutable.Queue

class RequestLimit(rateLimit:Int) {

  val bucket = Queue[Long]()

  val runner = new Thread(new Runnable {
    def run() {
      while(true){
        removeOldValuesFromQueue()
        Thread.sleep(100)
      }
    }
  }).start

  def removeOldValuesFromQueue():Unit = {
    if (bucket.isEmpty) return
    val ttl = System.currentTimeMillis - 10000
    while(!bucket.isEmpty && bucket.head < ttl){
      bucket.dequeue()
    }
  }

  def exceeded:Boolean = {
    (rateLimit > 0 && bucket.size >= rateLimit)
    
  }

  def register:Unit = {
    bucket.enqueue(System.currentTimeMillis)
  }
}