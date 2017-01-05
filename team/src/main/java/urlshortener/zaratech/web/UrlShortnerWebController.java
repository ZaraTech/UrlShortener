package urlshortener.zaratech.web;

import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.common.domain.Click;
import urlshortener.common.repository.ClickRepository;

@Controller
public class UrlShortnerWebController {

    @Autowired
    private HeadersManager headersManager;

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = { "", "/", "/single" }, method = RequestMethod.GET)
    public String indexSingle(HttpServletRequest request) {
        return "single";
    }

    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    public String indexMulti(HttpServletRequest request) {
        return "multi";
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String showStatistics(HttpServletRequest request, Map<String, Object> model) {
        List<Click> clicks =clickRepository.listAll();
        model.put("clicks", clicks);
        return "statistics";
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
