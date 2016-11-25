package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

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

        ResponseEntity<ShortURL> response = getUriWithQR(super.singleShortener(url, sponsor, request));

        return response;

    }

    @Override
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("url") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new short for CSV file '" + csvFile.getOriginalFilename() + "'");
        return super.multiShortener(csvFile, sponsor, request);
    }

    private ResponseEntity<ShortURL> getUriWithQR(ResponseEntity<ShortURL> response) {

        ShortURL body = response.getBody();
        HttpHeaders headers = response.getHeaders();
        HttpStatus status = response.getStatusCode();
        URI uriQR;
        URI uri = body.getUri();

        try {
            logger.info("Requested new QR for uri " + uri.toString());

            // API QRserver
            // http://api.qrserver.com/v1/create-qr-code/?data="+ uri +
            // "!&size=100x100"

            // API de Google
            uriQR = new URI(
                    "http://chart.googleapis.com/chart?cht=qr&chs=100x100&chl=" + uri.toString() + "&choe=UTF-8");
            body.setQR(uriQR);

            logger.info("QR obtained " + uriQR);

        } catch (URISyntaxException e) {
            logger.info("Uri-QR assignment failed.");
        }

        ResponseEntity<ShortURL> responseWithQR = new ResponseEntity<>(body, headers, status);

        return responseWithQR;
    }

}

