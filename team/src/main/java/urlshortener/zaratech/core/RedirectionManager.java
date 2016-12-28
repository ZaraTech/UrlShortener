package urlshortener.zaratech.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import urlshortener.common.domain.ShortURL;

public class RedirectionManager {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionManager.class);

    /**
     * Method that checks if the URL given redirects to itself
     * 
     * @param url - URL to check
     * @return True if the URL given redirects to itself.
     */
    public static boolean isRedirectedToSelf(String url) {

        URI uri;
        URI location;
        boolean isRedirected = false;
        int responseCode = 0;

        try {
            uri = new URI(url);

            // HTTP GET
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ShortURL> response = restTemplate.getForEntity(uri.toString(), null);
            location = response.getHeaders().getLocation();
            responseCode = response.getStatusCodeValue();

            if (responseCode >= 300 && responseCode < 400) { // HTTP code 3XX - Redirection

                if (location != null) {

                    if (url.equals(location.toString())) {

                        isRedirected = true;
                    } else {

                        isRedirected = false;
                    }
                } else {

                    isRedirected = true;
                }
            } else {

                isRedirected = false;
            }
        } catch (RestClientException e) {
            logger.info("Failed checking redirection");

        } catch (URISyntaxException e) {
            logger.info("Failed getting uri. Bad syntax");
        }

        return isRedirected;
    }
}
