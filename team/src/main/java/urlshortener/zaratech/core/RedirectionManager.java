package urlshortener.zaratech.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.domain.RedirectionException;

public class RedirectionManager {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionManager.class);

    /**
     * Method that checks if the URL given redirects to itself
     * 
     * @param url
     *            - URL to check
     * @return True if the URL given redirects to itself.
     * @throws RedirectionException
     */
    public static boolean isRedirectedToSelf(String url) throws RedirectionException {

        URI uri;
        boolean isRedirected = false;

        try {
            uri = new URI(url);

            HttpClient client = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();

            try {

                List<URI> redirectLocations;

                // HTTP GET
                client.execute(new HttpGet(uri), context);
                redirectLocations = context.getRedirectLocations();

                if (redirectLocations != null && !redirectLocations.isEmpty()) {
                    for (URI location : redirectLocations) {
                        if (location != null) {
                            if (url.equals(location.toString())) {

                                isRedirected = true;
                            }
                        }
                    }
                }
            } catch (ClientProtocolException e1) {
                logger.info("Failed request execution. Http protocol error");
                throw new RedirectionException();

            } catch (IOException e2) {
                logger.info("Failed request execution. Connection aborted.");

                throw new RedirectionException();
            }
        } catch (URISyntaxException e) {
            logger.info("Failed getting uri. Bad syntax");
            throw new RedirectionException();
        }

        return isRedirected;
    }
    
    public static int getNumberOfRedirections(String url) throws RedirectionException {

        URI uri;
        int numberOfRedirections = 0;

        try {
            uri = new URI(url);

            HttpClient client;
            HttpClientContext context;

            try {

                List<URI> redirectLocations;

                // HTTP GET
                
                URI location = uri;
                
                while(location != null){
                    client = HttpClientBuilder.create().build();
                    context = HttpClientContext.create();

                    client.execute(new HttpGet(location), context);
                    redirectLocations = context.getRedirectLocations();

                    if (redirectLocations != null && !redirectLocations.isEmpty()) {
                        location = redirectLocations.get(0);
                        
                        if (location != null) {
                            numberOfRedirections++;
                        }
                    } else {
                        location = null;
                    }
                }

            } catch (ClientProtocolException e1) {
                logger.info("Failed request execution. Http protocol error");
                throw new RedirectionException();

            } catch (IOException e2) {
                logger.info("Failed request execution. Connection aborted.");

                throw new RedirectionException();
            }
        } catch (URISyntaxException e) {
            logger.info("Failed getting uri. Bad syntax");
            throw new RedirectionException();
        }

        logger.info("URI " + uri.toString() + " HAS " + numberOfRedirections + " REDIRECTIONS");
        return numberOfRedirections;
    }
    
    public static void checkList(ShortURLRepository shortURLRepository){
        
        final Long LIMIT = 50L;
        final int MAX_REDIRECTIONS = 5;
        
        Long count = shortURLRepository.count();
        Long offset = 0L;
        
        List<ShortURL> list;
        
        while(count > 0L){
            list = shortURLRepository.list(LIMIT, offset);
            offset += LIMIT;
            count -= LIMIT;
            
            for(ShortURL su : list){
                try {
                    
                    logger.info("CHECKING URI " + su.getTarget());
                    
                    int nor = getNumberOfRedirections(su.getTarget());
                    if(nor >= MAX_REDIRECTIONS){
                        su = shortURLRepository.mark(su, false);
                    } else {
                        su = shortURLRepository.mark(su, true);
                    }
                } catch (RedirectionException e) {
                    su = shortURLRepository.mark(su, false);
                }
            }
        }
    }
}
