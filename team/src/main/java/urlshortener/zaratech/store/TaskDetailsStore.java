package urlshortener.zaratech.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import urlshortener.zaratech.domain.TaskDetails;

@Component
public class TaskDetailsStore {

    @Autowired
    private RedisTemplate<String, TaskDetails> redisTemplate;
    
    private static String TASK_DETAILS_KEY = "TaskDetails";

    public RedisTemplate<String, TaskDetails> getRedisTemplate()
    {
            return redisTemplate;
    }

    public void save(TaskDetails TaskDetails)
    {
        this.redisTemplate.opsForHash().put(TASK_DETAILS_KEY, TaskDetails.getId(), TaskDetails);
    }

    public TaskDetails find(String id)
    {
        return (TaskDetails)this.redisTemplate.opsForHash().get(TASK_DETAILS_KEY, id);
    }

    public void delete(String id)
    {
        this.redisTemplate.opsForHash().delete(TASK_DETAILS_KEY,id);  
    }
}
