package urlshortener.zaratech.store;

import java.io.IOException;

import redis.embedded.RedisServer;

public class RedisSrv {
    
    public RedisSrv() throws IOException{
        
        RedisServer redisServer = new RedisServer(6379);
        redisServer.start();
    }
    
    // TODO stop

}
