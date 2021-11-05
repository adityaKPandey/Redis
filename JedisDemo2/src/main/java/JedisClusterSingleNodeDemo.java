import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClusterSingleNodeDemo {
	
  public static void main(String[] args) {
	  
      JedisCluster jc = createJedisCluster();
    
      jc.set("foo11", "bar");
      String value = jc.get("foo11");

      System.out.println(value);
      
      
      usingExpiry(jc);
	  
	  usingHashes(jc);
	  
	  usingSets(jc);
	  
	  usingSortedSets(jc);
	  
	  
	   
 }

private static void usingSortedSets(JedisCluster jc) {
	Map<String, Double> rocketsVsLaunchYear = new HashMap<>();
	String key = "sorted_rocket" ;
	rocketsVsLaunchYear.put("Apollo 11", 1969d) ;
	rocketsVsLaunchYear.put("Deep space 1", 1998d) ;
	rocketsVsLaunchYear.put("Falcon 1", 2008d) ;
	
	
	jc.zadd(key, rocketsVsLaunchYear) ;
	
	System.out.println("No of entries for given key:"+jc.zcard(key)) ;
	
	System.out.println("Mission launched between 1970 and 2008:"+jc.zrangeByScore(key, 1970, 2008));
	System.out.println("Mission launched between 1970 and 2008 , with scores:"+jc.zrangeByScoreWithScores(key, 1970, 2008));
	
	System.out.println("All mission entries :") ;
	jc.zrange(key, 0, -1).stream().forEach((t)->System.out.print(t+","))	;
	System.out.println();
	System.out.println("All mission entries with scores:" +jc.zrangeWithScores(key, 0, -1)) ;
}



private static void usingSets(JedisCluster jc) {
	  jc.sadd("tags", "react" , "react native" , "graphql" , "javascript") ;
	  System.out.println("Set tags:"+jc.smembers("tags"));
	  
	  System.out.println("is member:"+jc.sismember("tags", "react native"));
	  System.out.println("is member:"+jc.sismember("tags", "reactor"));
	  
	  System.out.println("tags set's item count:"+jc.scard("tags"));
	  
	  System.out.println("tag pop:"+jc.spop("tags"));
	  System.out.println("tag pop:"+jc.spop("tags"));
	  
}

private static void usingHashes(JedisCluster jc) {
	Map<String,String> personDetails = new HashMap<>();
	  personDetails.put("name", "Qwerty") ;
	  personDetails.put("age", "23");
	  personDetails.put("score", "23.5");
	  
	  jc.hmset("person#1" , personDetails);
	  
	  List<String> data = jc.hmget("person#1", "name" , "age" );
	   
	  
	  
	  System.out.println(data);
	  
	  long result = jc.hincrBy("person#1", "age", 10);
	  
	  System.out.println("Result:" + result);
	  
	  System.out.println("Result:" + jc.hmget("person#1", "name" , "age" ));
}

private static void usingExpiry(JedisCluster jc) {
	// key: expiry_key expires after 10 seconds
      jc.setex("expiry_key", 10, "qwerty") ;
	  System.out.println("Val1:"+jc.get("expiry_key"));
	  
	  try {
	       Thread.currentThread().sleep(10*1000);
	  }catch(Exception e) {
	     e.printStackTrace();
	  }
	  
	  System.out.println("Val1:"+jc.get("expiry_key"));
}

private static JedisCluster createJedisCluster() {
	Set<HostAndPort> nodes = new HashSet<HostAndPort>();
      nodes.add(new HostAndPort("3.109.137.27", 6379));
      
     // nodes.add(new HostAndPort("3.108.120.61", 6379));

      
      int connectionTimeout = 1000;
      int soTimeout = 1000;
      int maxAttempts = 10;

      // Add properties for truststore
      System.setProperty("javax.net.ssl.trustStore", "[path/to/truststore]");
      System.setProperty("javax.net.ssl.trustStoreType", "JKS");
      System.setProperty("javax.net.ssl.trustStorePassword", "[trust-store-password]");

      JedisCluster jc = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts,  new JedisPoolConfig());
	return jc;
}
  
}