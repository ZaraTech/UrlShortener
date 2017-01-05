package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.core.QrManager;
import urlshortener.zaratech.core.RedirectionManager;
import urlshortener.zaratech.core.UploadManager;
import urlshortener.zaratech.domain.TaskDetails;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.store.TaskDetailsStore;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    private HeadersManager headersManager;
    
    @Autowired
    private Scheduler scheduler;
    
    @Autowired
    private TaskDetailsStore tdStore;

    @Override
    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
        logger.info("Requested redirection with hash " + id);
        return super.redirectTo(id, request);
    }

    @Override
    public ResponseEntity<ShortURL> singleShortener(@RequestParam("url") String url,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        ResponseEntity<ShortURL> response;

        if (RedirectionManager.isRedirectedToSelf(url)) {

            logger.info("Uri redirects to itself, short url can't be created");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {

            logger.info("Uri doesn't redirects to itself. Creating short url ...");
            response = QrManager.getUriWithQR(super.singleShortener(url, sponsor, request));
        }

        return response;
    }

    @Override
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("url") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new short for CSV file '" + csvFile.getOriginalFilename() + "'");
        return super.multiShortener(csvFile, sponsor, request);
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<UrlDetails> showDetailsJson(@PathVariable String id) {

        logger.info("Requested JSON details for id '" + id + "'");

        UrlDetails details = headersManager.getDetails(id);

        return new ResponseEntity<UrlDetails>(details, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", produces = "text/html", method = RequestMethod.GET)
    public ResponseEntity<UrlDetails> showDetailsHtml(@PathVariable String id, HttpServletRequest request) {

        logger.info("Requested HTML details for id '" + id + "' --> REDIRECT");

        HttpHeaders h = new HttpHeaders();

        try {
            h.setLocation(new URI(request.getRequestURL() + ".html"));
            return new ResponseEntity<UrlDetails>(h, HttpStatus.TEMPORARY_REDIRECT);

        } catch (URISyntaxException e) {
            return new ResponseEntity<UrlDetails>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/task/{id:.*}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<TaskDetails> showTaskDetails(@PathVariable String id) {

        logger.info("Requested TASK progress for id '" + id + "'");

        TaskDetails details = tdStore.find(id);
        
        // TODO ERROR si no esta la task en cache

        return new ResponseEntity<TaskDetails>(details, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/task-start/{id:.*}", method = RequestMethod.GET)
    public ResponseEntity<?> startTask(@PathVariable String id) {

        logger.info("Requested START TASK progress for id '" + id + "'");

        UploadManager.startTask(scheduler, tdStore, id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
