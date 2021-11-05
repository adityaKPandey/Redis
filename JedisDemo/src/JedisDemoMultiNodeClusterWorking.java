import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
  https://www.baeldung.com/jedis-java-redis-client-library
 */
public class JedisDemoMultiNodeClusterWorking {

	public static void main(String[] args) throws InterruptedException {

		//Jedis jedis =  new Jedis(new HostAndPort("localhost", 6379)) ;

		Jedis jedis ;
		//=  new Jedis(new HostAndPort("3.109.137.27", 6379)) ;

		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		
		//nodes.add(new HostAndPort("3.109.224.88", 6379));
		//nodes.add(new HostAndPort("3.108.120.61" , 6379));
		// even if we specify only 1 node , JedisCluster figures out all other nodes in the cluster
		nodes.add(new HostAndPort("3.109.137.27", 6379));
		
		/*
		  
		  In the above gist, we provided only 1 host i.e 3.109.137.27 with port 6001 to connect to, However when connection is established 
		  JedisCluster gathers information about other nodes in the cluster. So even if 3.109.137.27:6379 goes down, 
		  it will still be able to communicate with the cluster through some other node eg. 3.109.224.88 or 3.108.120.61 etc. 
		  This essentially means that JedisCluster manages failover scenarios. Until and unless there is an operational cluster, 
		  JedisCluster will be able to connect to it.
		  
		 */

		GenericObjectPoolConfig pc = new JedisPoolConfig();
		int connectionTimeout = 1000;
		int soTimeout = 1000;
		int maxAttempts = 10;

		// Add properties for truststore
		System.setProperty("javax.net.ssl.trustStore", "[path/to/truststore]");
		System.setProperty("javax.net.ssl.trustStoreType", "JKS");
		System.setProperty("javax.net.ssl.trustStorePassword", "[trust-store-password]");

		
		//soTimeout: This stops the request from dragging on after connection succeeds.
		JedisCluster jedisCluster = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts , pc);
		
		Map<String,JedisPool> clusterNodesMap = jedisCluster.getClusterNodes();
		System.out.println(clusterNodesMap.size());
		clusterNodesMap.entrySet().forEach((c) -> {System.out.println("key:"+c.getKey()  + " , " + c.getValue().isClosed() );});
		
		//jedis =  new Jedis(new HostAndPort("3.109.137.27", 6379)) ;
		Random random = new Random();
		int hostIndex = random.nextInt(3) ;
		System.out.println("Host index:"+hostIndex);
		//jedisCluster.set
		//jedis = jedisCluster.getConnectionFromSlot(hostIndex);
		jedis = null;
		int i = 0;
		
		/*
		while( i < 100) {
			
			hostIndex = random.nextInt(16300) ;
			System.out.println("Host index:"+hostIndex);
			jedis = jedisCluster.getConnectionFromSlot(hostIndex);
			System.out.println("host:"+jedis.getClient().getHost());
			i++;
			// int slot = JedisClusterCRC16.getSlot(key);
			  //return connectionHandler.getConnectionFromSlot(slot);
		}
		*/	
		jedis = jedisCluster.getConnectionFromSlot(1);
		System.out.println("host from slot:"+jedis.getClient().getHost());
		storeRetrieveString(jedis);
		storeRetrieveLists(jedis);
		
		storeRetrieveString(jedisCluster);
		storeRetrieveLists(jedisCluster);
		jedis.close();
		jedisCluster.close();
	}

	private static void storeRetrieveString(Jedis jedis) throws InterruptedException {
		String key1 = "qwqwrmfkmd//gfd12";
		jedis.set(key1,"DATA1"); 
		jedis.setex("expiry_key", 10, "qwerty") ;
		
		System.out.println("Val1:"+jedis.get("expiry_key"));
		Thread.currentThread().sleep(10*1000);
		System.out.println("Val1:"+jedis.get("expiry_key"));
		
		System.out.println("Val1:"+jedis.get(key1));
	}
	
	private static void storeRetrieveLists(Jedis jedis) {
		String listName = "list#names";
		String [] names = { "Adi","Ami","Ashahar","Amit","Avinash" };
		jedis.lpush(listName, "Adi" );
		jedis.rpush(listName, "Ami");
		System.out.println(jedis.rpop(listName));
		System.out.println(jedis.lpop(listName));
		System.out.println(jedis.lpop(listName));
		System.out.println(jedis.lpop(listName));
		jedis.lpush(listName, "Adi" );
		jedis.rpush(listName, "Ami");
	}

	private static void storeRetrieveString(JedisCluster jedisCluster) {
		String key1 = "qwqwrmfkmd//gfd12";
		jedisCluster.set(key1,"DATA1");
		System.out.println("Val1:"+jedisCluster.get(key1));
	}
	
	private static void storeRetrieveLists(JedisCluster jedisCluster) {
		String listName = "list#names";
		String [] names = { "Adi","Ami","Ashahar","Amit","Avinash" };
		jedisCluster.lpush(listName, "Adi" );
		jedisCluster.rpush(listName, "Ami");
		System.out.println(jedisCluster.rpop(listName));
		System.out.println(jedisCluster.lpop(listName));
		System.out.println(jedisCluster.lpop(listName));
		System.out.println(jedisCluster.lpop(listName));
		jedisCluster.lpush(listName, "Adi" );
		jedisCluster.rpush(listName, "Ami");
		
	}

	


}
