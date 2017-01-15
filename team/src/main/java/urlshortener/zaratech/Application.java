package urlshortener.zaratech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.store.RedisSrv;

@SuppressWarnings("deprecation")
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        RedisSrv redis = new RedisSrv();
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
    
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
      JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();

      // Defaults
      redisConnectionFactory.setHostName("127.0.0.1");
      redisConnectionFactory.setPort(6379);
      return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, UploadTaskData> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, UploadTaskData> redisTemplate = new RedisTemplate<String, UploadTaskData>();
        redisTemplate.setConnectionFactory(cf);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
