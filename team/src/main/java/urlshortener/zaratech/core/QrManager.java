package urlshortener.zaratech.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;

public class QrManager {

    private static final Logger logger = LoggerFactory.getLogger(QrManager.class);

    /**
     * Method that assigns a QR code to the given HTTP response, which contains
     * the associated URI
     * 
     * @param response HTTP response
     * @return HTTP response with QR code assigned
     */
    public static ShortURL getUriWithQR(ShortURL body) {
    	
		//TODO en vez de ShortURL deberia devolver:
		//	JSON --> si OK (URL acortada / URL QR)
		//	Error --> si falla
		//Crear una clase con la info que interesa y no todo 
		
		//TODO Gestionar el incremento de params mediante RequestMapping, RequestParam en vez de 
		//enviar params sueltos. El controlador tiene el mapeo entre peticiones web y
		//params y se lo pasa a la logica de negocio quien comprueba que estan los
		//params necesarios (si no, excepcion)
        try {
            URI uri = body.getUri();
            logger.info("Requested new QR for uri " + uri.toString());

            //TODO: AÑADIR VCARD ¿?

            // API de Google
            URI uriQR = new URI(
                    "http://chart.googleapis.com/chart?cht=qr&chs=100x100&chl=" + uri.toString() + "&choe=UTF-8");
            body.setQR(uriQR);

            logger.info("Uri-QR obtained " + uriQR);
            
            return body;
            
        } catch (URISyntaxException e) {
            logger.info("Uri-QR assignment failed.");

            return null;
        }
    }
}
