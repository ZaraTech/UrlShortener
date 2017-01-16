package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.AppMailSender;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.core.QrManager;
import urlshortener.zaratech.core.UploadManager;
import urlshortener.zaratech.domain.RedirectionDetails;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.zaratech.domain.Statistics;
import urlshortener.zaratech.domain.UserAgentDetails;
import urlshortener.zaratech.domain.VCard;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.store.UploadTaskDataStore;

@RestController
public class UrlShortenerControllerWithLogs {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    private HeadersManager headersManager;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private UploadTaskDataStore tdStore;

    @Autowired
    private AppMailSender mailSender;

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi|stats-ws).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
        logger.info("Requested redirection with hash " + id);
        UserAgentDetails ua = headersManager.getUA(request.getHeader("User-Agent"));

        ShortURL su = shortURLRepository.findByKey(id);
        if (su != null) {

            if (su.isCorrect()) {
                createAndSaveClick(id, UploadManager.extractIP(request), ua.getBrowserName(), ua.getBrowserVersion(),
                        ua.getOsName());
                return createSuccessfulRedirectToResponse(su);
            } else {
                return new ResponseEntity<String>(su.getLastCorrectDate().toString(), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/statistics", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<Statistics> showStatistics(@RequestParam(value = "desde", required = false) String desde,
            @RequestParam(value = "hasta", required = false) String hasta, HttpServletRequest request) {
        logger.info("Requested filter with params " + desde + "  " + hasta);

        List<Click> clicks;

        if (desde == null && hasta == null || desde.equals("") && hasta.equals("")) {
            //logger.info("Requested filter all " + desde + "  " + hasta);
            clicks = clickRepository.listAll();

        } else if (hasta.equals("") && !desde.equals("")) {
            //logger.info("Requested filter only since " + desde + "  " + hasta);
            clicks = clickRepository.listSince(desde);

        } else if (desde.equals("") && !hasta.equals("")) {
            //logger.info("Requested filter only for " + desde + "  " + hasta);
            clicks = clickRepository.listFor(hasta);

        } else {
            //logger.info("Requested filter for and since " + desde + "  " + hasta);
            clicks = clickRepository.listSinceAndFor(desde, hasta);
        }

        if (clicks != null) {
            Statistics statistics = headersManager.getStatistics(clicks);
            return new ResponseEntity<Statistics>(statistics, HttpStatus.OK);

        } else {
            //logger.info("CLICKS NULL!!!!");
            return new ResponseEntity<Statistics>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createAndSaveClick(String hash, String ip, String browser, String version, String os) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), null, browser, version, os, null, ip,
                null);
        cl = clickRepository.save(cl);
        logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    @RequestMapping(value = "/link-single", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> singleShortener(@RequestParam("url") String url,
            @RequestParam(value = "vCardFName", required = false) String vCardFName,
            @RequestParam(value = "vCardCheckbox", required = false) Boolean vCardCheckbox,
            @RequestParam(value = "errorCorrection", required = false) String errorCorrection,
            @RequestParam(value = "mailCheckbox", required = false) Boolean mailCheckbox,
            @RequestParam(value = "email", required = false) String email, HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        ResponseEntity<ShortURL> response = UploadManager.singleShort(shortURLRepository, url, request, vCardFName,
                vCardCheckbox, errorCorrection);

        if (response.getStatusCode().equals(HttpStatus.CREATED)) {

            if (mailCheckbox != null && mailCheckbox == true && email != null && !email.isEmpty()) {

                logger.info("Preparing to send email...");

                VCard vcard = null;
                String urlBase = BaseUrlManager.getLocalBaseUrl(request);

                if (vCardCheckbox != null && vCardCheckbox == true && vCardFName != null && !vCardFName.isEmpty()) {

                    vcard = new VCard(vCardFName, response.getBody().getUri());
                }
                if (errorCorrection == null || errorCorrection.isEmpty()) {
                    errorCorrection = "L"; // default
                }

                // send mail with QR code
                mailSender.sendMail(email, QrManager.createQRImage(urlBase, errorCorrection, vcard));
            }
        }

        return response;
    }

    @RequestMapping(value = "/link-single-async-checks", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> singleShortenerAsyncChecks(@RequestParam("url") String url,
            @RequestParam(value = "vCardFName", required = false) String vCardFName,
            @RequestParam(value = "vCardCheckbox", required = false) Boolean vCardCheckbox,
            @RequestParam(value = "errorCorrection", required = false) String errorCorrection,
            HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        return UploadManager.singleShortAsyncChecks(scheduler, shortURLRepository, url, request, vCardFName,
                vCardCheckbox, errorCorrection);
    }

    @RequestMapping(value = "/link-multi", method = RequestMethod.POST)
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("file") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor,
            @RequestParam(value = "vCardFName", required = false) String vCardFName,
            @RequestParam(value = "vCardCheckbox", required = false) Boolean vCardCheckbox,
            @RequestParam(value = "errorCorrection", required = false) String errorCorrection,
            HttpServletRequest request) {

        logger.info("Requested new multi-short for CSV file '" + csvFile.getOriginalFilename() + "'");

        return UploadManager.multiShortSync(shortURLRepository, csvFile, request, vCardFName, vCardCheckbox,
                errorCorrection);
    }

    @RequestMapping(value = "/link-multi-async-file", method = RequestMethod.POST)
    public ResponseEntity<RedirectionDetails> multiShortenerAsyncFile(@RequestParam("file") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new ASYNC multi-short for CSV file '" + csvFile.getOriginalFilename() + "'");

        return UploadManager.multiShortAsync(scheduler, shortURLRepository, tdStore, csvFile, request);
    }

    @RequestMapping(value = "/link-multi-async-input", method = RequestMethod.POST)
    public ResponseEntity<RedirectionDetails> multiShortenerAsyncInput(@RequestParam("input") String urlList,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new ASYNC multi-short for FORM DATA");

        String[] urls = urlList.split("\r?\n");
        List<String> urlsList = new LinkedList<String>();

        for (String url : urls) {
            urlsList.add(url);
        }

        return UploadManager.multiShortAsync(scheduler, shortURLRepository, tdStore, urlsList, request);
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
    public ResponseEntity<UploadTaskData> showTaskDetails(@PathVariable String id) {

        logger.info("Requested TASK progress for id '" + id + "'");

        UploadTaskData details = tdStore.find(id);

        // TODO ERROR si no esta la task en cache

        return new ResponseEntity<UploadTaskData>(details, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/qr/{id:.*}", produces = MediaType.IMAGE_PNG_VALUE, method = RequestMethod.GET)
    public byte[] getQr(@PathVariable String id,
            @RequestParam(value = "vCardFName", required = false) String vCardFName,
            @RequestParam(value = "errorCorrection", required = false) String errorCorrection,
            HttpServletRequest request) {

        try {
            VCard vcard = null;
            String urlBase = BaseUrlManager.getLocalBaseUrl(request);

            if (vCardFName != null && !vCardFName.isEmpty()) {
                vcard = new VCard(vCardFName, new URI(urlBase + "/" + id));
            }

            BufferedImage image = QrManager.createQRImage(urlBase, errorCorrection, vcard);

            // convert image to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            return imageInByte;

        } catch (URISyntaxException e) {

            logger.info("QR image failed. Bad URI syntax.");
            return null;
        } catch (IOException e) {

            logger.info("QR image failed. Convert to byte array failed.");
            return null;
        }
    }
}
