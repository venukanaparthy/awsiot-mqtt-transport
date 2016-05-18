package com.esri.gest.test.awsiot;

import com.google.common.util.concurrent.RateLimiter;

public class EventsRateLimit {

	 public static int M = 1000;
	 public static int N = 100;
	 public static RateLimiter limiter = RateLimiter.create(N);
	 public static void main(final String[] args)
	 {		
		 String msg = null;
		 for (int i =0; i < M; i++){			 			 
			 msg = String.format("Messsage - %d", i);
			  publishMessage(msg); 				
		 }				 
	 }
	 
	 public static void publishMessage(String msg) {
		 if(limiter.tryAcquire()) {			
			 System.out.println(msg);
		 }else {
		      System.out.println("================");
		 }
	 }
}

