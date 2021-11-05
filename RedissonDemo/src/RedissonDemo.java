import java.util.function.BiFunction;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/*
  
  https://www.baeldung.com/redis-redisson
  
 */
public class RedissonDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		config.setTransportMode(TransportMode.NIO);
		RedissonClient redissonClient = Redisson.create(config);
		
		//RedissonClient redissonClient = Redisson.create();
		storingMap(redissonClient);
		asyncMethod(redissonClient);

	}
	
	private static void storingMap(RedissonClient redissonClient) {
		RMap<String,Object> map = redissonClient.getMap("map");
		System.out.println(map.addAndGet("one", 1));
		map.put("two", 2);
		System.out.println("Map:"+map.toString());
	}
	
	private static void asyncMethod(RedissonClient redissonClient) {
		RAtomicLong atomicLong = redissonClient.getAtomicLong("myLong");
		RFuture<Boolean>isSet = atomicLong.compareAndSetAsync(6, 27);
		//isSet.handleAsync(fn)
		
		 
		//final Boolean result = isSet.get()
		
		 BiFunction<Boolean, Throwable,
		Boolean> getResult = (result , exception) ->{
			if(result.booleanValue())
				System.out.println("Success");
			return result;
		};
		
		isSet.handleAsync( getResult);
		
	}

}
