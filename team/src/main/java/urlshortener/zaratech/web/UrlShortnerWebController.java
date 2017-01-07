package urlshortener.zaratech.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.domain.UrlDetails;

@Controller
public class UrlShortnerWebController {

    @Autowired
    private HeadersManager headersManager;

    @RequestMapping(value = { "", "/", "/single" }, method = RequestMethod.GET)
    public String indexSingle(HttpServletRequest request) {
        return "single";
    }

    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    public String indexMulti(HttpServletRequest request) {
        return "multi";
    }
    
    @RequestMapping(value = "/multi-async", method = RequestMethod.GET)
    public String indexMultiAsync(HttpServletRequest request) {
        return "multi-async";
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+.html", method = RequestMethod.GET)
    public String showDetails(@PathVariable String id, Map<String, Object> model) {

        UrlDetails details = headersManager.getDetails(id);

        model.put("id", id);
        model.put("date", details.getDate().toString());
        model.put("target", details.getTarget());
        model.put("clicks", details.getClicks().toString());
        model.put("visitors", details.getVisitors());
        return "details";
    }
    
}
