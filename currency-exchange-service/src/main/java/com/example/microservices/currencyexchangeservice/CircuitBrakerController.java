package com.example.microservices.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBrakerController {
	
	//we created this controller to user resilience 
	//which make the microservices control if they down or slow we will manage what to do 
	Logger logger = LoggerFactory.getLogger(CircuitBrakerController.class);
	
	@GetMapping("/sample-api")
	@Retry(name="default",fallbackMethod="defaultresponse")//this make 3 call if service is down
	@CircuitBreaker(name="default",fallbackMethod="defaultresponse")//break the api calls and return default 
	@RateLimiter(name="default")//to allow specific number of calls in seconds 10/1000 exm.
	@Bulkhead(name="default")//to cut specific amount of calls concurrently we can define them in applc.propert
	public String sampleApi() {
		logger.info("sampple api call received");
		//we created this to make root fail so we can manage
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-failure",
				String.class);
		
		return forEntity.getBody();
	}
	
	//this will called when above service doesn't response after 3 call 
	public String defaultresponse(Exception ex) {
		return "default response";
	}

}
