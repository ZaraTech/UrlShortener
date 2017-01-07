package urlshortener.zaratech.core;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.hash.Hashing;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.zaratech.domain.NoQrException;
import urlshortener.zaratech.domain.RedirectionDetails;
import urlshortener.zaratech.domain.RedirectionException;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.scheduling.UploadTask;
import urlshortener.zaratech.store.UploadTaskDataStore;

public class UploadManager {

    private static final Logger logger = LoggerFactory.getLogger(UploadManager.class);

    public static ResponseEntity<ShortURL> singleShort(ShortURLRepository shortURLRepository, String url,
            HttpServletRequest reques, String vCardFName, Boolean vCardCheckbox, String errorRadio) {

        String ip = extractIP(request);

        try {
            ShortURL su = singleShort(shortURLRepository, null, url, ip, vCardFName, vCardCheckbox, errorRadio);

            if (su != null) {
                HttpHeaders h = new HttpHeaders();
                h.setLocation(su.getUri());
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ShortURL singleShort(ShortURLRepository shortURLRepository, String urlBase, String url, String ip, String vCardFName, Boolean vCardCheckbox, String errorRadio)
            throws NoQrException {

        try {
            if (RedirectionManager.isRedirectedToSelf(url)) {
                logger.info("Uri redirects to itself, short url can't be created");
                return null;
            } else {

                logger.info("Uri doesn't redirects to itself. Creating short url ...");

                ShortURL su = createAndSaveIfValid(shortURLRepository, urlBase, url, UUID.randomUUID().toString(), ip);

                if (su != null) {
                    HttpHeaders h = new HttpHeaders();
                    h.setLocation(su.getUri());
                    su = QrManager.getUriWithQR(su, vCardFName, vCardCheckbox, errorRadio);
                    if (su != null) {
                        return su;
                    } else {
                        logger.info("QR Exception");
                        throw new NoQrException();
                    }
                } else {
                    return null;
                }
            }
        } catch (RedirectionException e) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ResponseEntity<ShortURL[]> multiShortSync(ShortURLRepository shortURLRepository,
            MultipartFile csvFile, HttpServletRequest request) {

        LinkedList<String> urls = processFile(csvFile);
        String ip = extractIP(request);

        ShortURL[] su = multiShort(shortURLRepository, urls, ip);

        if (su != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    private static ShortURL[] multiShort(ShortURLRepository shortURLRepository, LinkedList<String> urls, String ip) {
        ShortURL[] su = new ShortURL[urls.size()];

        if (validateUrlList(urls)) {
            int i = 0;

            for (String url : urls) {
                try {
                    if (!RedirectionManager.isRedirectedToSelf(url)) {

                        ShortURL tmpSu = createAndSaveIfValid(shortURLRepository, null, url, UUID.randomUUID().toString(),
                                ip);

                        ShortURL tmpSu2 = QrManager.getUriWithQR(tmpSu);

                        if (tmpSu2 != null) {
                            su[i] = tmpSu2;

                        } else {
                            su[i] = tmpSu;
                        }

                        i++;
                    }
                } catch (RedirectionException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            return su;

        } else {
            return null;
        }
    }

    public static ResponseEntity<RedirectionDetails> multiShortAsync(Scheduler scheduler,
            ShortURLRepository shortURLRepository, UploadTaskDataStore tdStore, MultipartFile csvFile,
            HttpServletRequest request) {

        LinkedList<String> urls = processFile(csvFile);
        String ip = extractIP(request);

        String urlsStr = "";

        for (String url : urls) {
            urlsStr += url;
        }

        String id = Hashing.murmur3_32().hashString(urlsStr, StandardCharsets.UTF_8).toString();

        UploadTaskData details = new UploadTaskData(id);

        for (String url : urls) {
            try {
                details.addUrl(new URI(url));
            } catch (URISyntaxException e) {
                // do nothing -> malformed URL not added
            }
        }

        // save pending state
        tdStore.save(details);

        String baseUrl = "http://" + request.getLocalName() + ":" + request.getServerPort();
        scheduler.newUploadTask(new UploadTask(shortURLRepository, tdStore, details, ip, baseUrl));

        try {
            URI url = new URI(baseUrl + "/task/" + id);
            HttpHeaders h = new HttpHeaders();
            h.setLocation(url);
            RedirectionDetails rd = new RedirectionDetails(url);
            return new ResponseEntity<RedirectionDetails>(rd, h, HttpStatus.ACCEPTED);

        } catch (URISyntaxException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * Returns all the comma-separated URLs contained in the CSV file
     */
    private static LinkedList<String> processFile(MultipartFile csvFile) {

        InputStream is;

        try {
            is = csvFile.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            LinkedList<String> list = new LinkedList<String>();
            while ((line = br.readLine()) != null) {
                String[] urls = line.split(",");
                for (String url : urls) {
                    if (!url.trim().equals("")) {
                        list.addLast(url.trim());
                    }
                }
            }

            return list;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns true if, and only if, all URLs are valid
     */
    private static boolean validateUrlList(LinkedList<String> urls) {

        boolean resp = true;

        for (String url : urls) {
            resp &= isValid(url);
        }

        return resp;
    }

    private static ShortURL createAndSaveIfValid(ShortURLRepository shortURLRepository, String urlBase, String url,
            String owner, String ip) {

        if (isValid(url)) {
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();

            URI link;
            try {
                link = linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri();
            } catch (Exception e) {
                link = createLink(urlBase, id);
            }

            logger.info("createAndSaveIfValid: link = " + link.toString());

            ShortURL su = new ShortURL(id, url, link, null, new Date(System.currentTimeMillis()), owner,
                    HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);

            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

    private static URI createLink(String urlBase, String id) {
        URI url = null;
        try {
            url = new URI(urlBase + "/" + id);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static boolean isValid(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        return urlValidator.isValid(url);
    }
}
