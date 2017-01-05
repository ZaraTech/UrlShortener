package urlshortener.zaratech.store;

import org.springframework.data.redis.core.RedisTemplate;

import urlshortener.zaratech.domain.TaskData;

public interface RedisStore<T extends TaskData> {
    
    public RedisTemplate<String, T> getRedisTemplate();

    public void save(T TaskDetails);

    public T find(String id);

    public void delete(String id);
}
