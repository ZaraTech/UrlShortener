package urlshortener.zaratech.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.RedirectionManager;

public class RedirectionCheckScheduledTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionCheckScheduledTask.class);
    
    private ShortURLRepository shortURLRepository;

    public RedirectionCheckScheduledTask(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public void run() {
        logger.info("--- STARTING SCHEDULED REDIRECTION CHECK ---");
        RedirectionManager.checkNorFromList(shortURLRepository);
        logger.info("--- ENDING SCHEDULED REDIRECTION CHECK ---");
    }
}
