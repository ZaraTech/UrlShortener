package urlshortener.zaratech.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import urlshortener.zaratech.domain.UploadTaskData;

@Component
public class UploadTaskDataStore implements RedisStore<UploadTaskData>{

    @Autowired
    private RedisTemplate<String, UploadTaskData> redisTemplate;
    
    private static String TASK_DETAILS_KEY = "TaskDetails";

    @Override
    public RedisTemplate<String, UploadTaskData> getRedisTemplate()
    {
            return redisTemplate;
    }

    @Override
    public void save(UploadTaskData TaskDetails)
    {
        this.redisTemplate.opsForHash().put(TASK_DETAILS_KEY, TaskDetails.getId(), TaskDetails);
    }

    @Override
    public UploadTaskData find(String id)
    {
        return (UploadTaskData)this.redisTemplate.opsForHash().get(TASK_DETAILS_KEY, id);
    }
    
    @Override
    public void delete(String id)
    {
        this.redisTemplate.opsForHash().delete(TASK_DETAILS_KEY,id);  
    }
}
