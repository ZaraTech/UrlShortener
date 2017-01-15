package urlshortener.zaratech.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.RedirectionManager;

public class RedirectionCheckTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionCheckTask.class);
    
    private ShortURLRepository shortURLRepository;
    private ShortURL su;

    public RedirectionCheckTask(ShortURLRepository shortURLRepository, ShortURL su) {
        this.shortURLRepository = shortURLRepository;
        this.su = su;
    }

    @Override
    public void run() {
        logger.info("--- STARTING REDIRECTION CHECK FOR URI " + su.getTarget() + " ---");
        RedirectionManager.checkNorFromUri(shortURLRepository, su);
    }
}
