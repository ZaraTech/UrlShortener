package urlshortener.zaratech.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
            body.setQr(uriQR);

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
    public static ShortURL getUriWithQR(ShortURL body, String vCardFName, Boolean vCardCheckbox,
            String errorCorrection) {

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

            if (errorCorrection == null) {
                errorCorrection = "L"; // default
            }

            // API de Google
            uriQR = new URI("http://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + link + "&choe=UTF-8&chld="
                    + errorCorrection);
            body.setQr(uriQR);

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
     *            short URL
     * @param urlBase
     *            String with local address and server port
     * @param vCardFName
     *            name of the object the vCard represents
     * @param vCardCheckbox
     *            boolean associated with html checkbox
     * @param errorCorrection
     *            String error correction level (L, M, Q, H)
     * @return ShortURL with QR code assigned
     */
    public static ShortURL getLocalUriWithQR(ShortURL body, String urlBase, String vCardFName, Boolean vCardCheckbox,
            String errorCorrection) {

        try {
            String id = body.getHash();
            URI uriQR;
            URI uri = body.getUri();

            logger.info("Requested new QR for uri " + uri.toString());

            if (errorCorrection == null || errorCorrection.isEmpty()) {
                errorCorrection = "L"; // default
            }
            logger.info("errorCorrection: " + errorCorrection);

            if (vCardCheckbox != null && vCardCheckbox == true && vCardFName != null && !vCardFName.isEmpty()) {

                logger.info("Adding VCard information.");
                String newVCardFName = URLEncoder.encode(vCardFName, "UTF-8");

                uriQR = new URI(urlBase + "/qr/" + id + "?vCardFName=" + newVCardFName + "&errorCorrection="
                        + errorCorrection);

            } else {
                logger.info("No VCard Information.");

                uriQR = new URI(urlBase + "/qr/" + id + "?errorCorrection=" + errorCorrection);
            }

            body.setQr(uriQR);

            logger.info("Uri-QR obtained " + uriQR);

            return body;

        } catch (URISyntaxException e) {
            logger.info("Uri-QR assignment failed. Bad syntax.");

            return null;
        } catch (UnsupportedEncodingException e) {
            logger.info("Uri-QR VCardFName encode failed.");

            return null;
        }
    }

    /**
     * Creates an image with a QR code inside
     * 
     * @param qrCodeText
     *            text to encode into QR code
     * @param errorCorrection
     *            Error correction level (L, M, Q, H)
     * @param vcard
     *            VCard to include into QR code
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRImage(String qrCodeText, String errorCorrection, VCard vcard) {

        int size = 300;

        if (errorCorrection == null || errorCorrection.isEmpty()) {
            errorCorrection = "L"; // default
        }

        logger.info("Creating QR image...errorCorrection " + errorCorrection);

        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, String> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix;
        
        try {
            if (vcard != null) {

                // QR code with VCard information
                byteMatrix = qrCodeWriter.encode(vcard.getVCard(), BarcodeFormat.QR_CODE, size, size, hintMap);

            } else {
                byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
            }

            // Make the BufferedImage that are to hold the QRCode
            int matrixWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);

            // Paint and save the image using the ByteMatrix
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            return image;
            
        } catch (WriterException e) {
            
            return null;
        }
    }
}
