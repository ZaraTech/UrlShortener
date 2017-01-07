package urlshortener.zaratech.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.zaratech.domain.VCard;

public class QrManager {

    private static final Logger logger = LoggerFactory.getLogger(QrManager.class);

    /**
     * Method that assigns a QR code to the given HTTP response, which contains
     * the associated URI
     * 
     * @param body
     *            short URL to be included on QR code
     * @return ShortURL with QR code assigned
     */
    public static ShortURL getUriWithQR(ShortURL body) {
        try {

            URI uriQR;
            URI uri = body.getUri();
            String link = "";

            logger.info("Requested new QR for uri " + uri.toString());

            link = uri.toString();

            // API de Google
            uriQR = new URI("http://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + link + "&choe=UTF-8");
            body.setQR(uriQR);

            logger.info("Uri-QR obtained " + uriQR);

            return body;

        } catch (URISyntaxException e) {
            logger.info("Uri-QR assignment failed.");

            return null;
        }
    }

    /**
     * Method that assigns a QR code to the given HTTP response, which contains
     * VCard information and the associated URI.
     * 
     * @param body
     *            short URL to be included on QR code
     * @param vCardFName
     *            name of the object the vCard represents
     * @param vCardCheckbox
     *            boolean associated with html checkbox
     * @return ShortURL with QR code assigned
     */
    public static ShortURL getUriWithQR(ShortURL body, String vCardFName, Boolean vCardCheckbox, String errorRadio) {

        // TODO en vez de ShortURL deberia devolver:
        // JSON --> si OK (URL acortada / URL QR)
        // Error --> si falla
        // Crear una clase con la info que interesa y no todo

        // TODO Gestionar el incremento de params mediante RequestMapping,
        // RequestParam en vez de
        // enviar params sueltos. El controlador tiene el mapeo entre peticiones
        // web y
        // params y se lo pasa a la logica de negocio quien comprueba que estan
        // los
        // params necesarios (si no, excepcion)
        try {

            URI uriQR;
            URI uri = body.getUri();
            String link = "";

            logger.info("Requested new QR for uri " + uri.toString());

            if (vCardCheckbox != null && vCardCheckbox == true && vCardFName != null && !vCardFName.isEmpty()) {

                logger.info("Adding VCard information.");

                VCard vcard = new VCard(vCardFName, uri);

                link = vcard.getEncodedVCard();

            } else {
                logger.info("No VCard Information.");

                link = uri.toString();
            }
            
            if (errorRadio == null){
                errorRadio = "L"; // default
            }
            
            // API de Google
            uriQR = new URI("http://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + link + "&choe=UTF-8&chld="
                    + errorRadio);
            body.setQR(uriQR);

            logger.info("Uri-QR obtained " + uriQR);

            return body;

        } catch (URISyntaxException e) {
            logger.info("Uri-QR assignment failed.");

            return null;
        }
    }
}
