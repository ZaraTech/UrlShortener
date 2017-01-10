package urlshortener.zaratech.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.embedded.RedisServer;

public class RedisSrv {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisSrv.class);
    
    private static RedisServer redisServer;
    
    public RedisSrv(){
        logger.info("Starting Redis Embedded Server...");
        
        
        try {
            redisServer = new RedisServer(6379);
            redisServer.start();
        } catch (Exception e) {
            logger.info("Redis Embedded Server is already started");
        }
    }
    
    public void stop(){
        redisServer.stop();
    }
}
