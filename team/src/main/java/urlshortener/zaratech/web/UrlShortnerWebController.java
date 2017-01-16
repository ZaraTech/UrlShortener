package urlshortener.zaratech.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.domain.UrlDetails;

@Controller
public class UrlShortnerWebController {
    
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {}

    @Autowired
    private HeadersManager headersManager;

    @RequestMapping(value = { "", "/", "/single" }, method = RequestMethod.GET)
    public String indexSingle(HttpServletRequest request) {
        return "single";
    }

    @RequestMapping(value = "/single-async-checks", method = RequestMethod.GET)
    public String indexSingleAsyncChecks(HttpServletRequest request) {
        return "single-async-checks";
    }

    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    public String indexMulti(HttpServletRequest request) {
        return "multi";
    }

    @RequestMapping(value = "/multi-async", method = RequestMethod.GET)
    public String indexMultiAsync(HttpServletRequest request) {
        return "multi-async";
    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String showStatistics(HttpServletRequest request) {
        return "statistics";
    }

    @RequestMapping(value = "/stats-stream", method = RequestMethod.GET)
    public String showStatisticsStream(HttpServletRequest request) {
        return "statistic-stream";
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+.html", method = RequestMethod.GET)
    public String showDetails(@PathVariable String id, Model model) {

        UrlDetails details = headersManager.getDetails(id);

        if (details != null) {
            model.addAttribute("id", id);
            model.addAttribute("date", details.getDate().toString());
            model.addAttribute("target", details.getTarget());
            model.addAttribute("clicks", details.getClicks().toString());
            model.addAttribute("visitors", details.getVisitors());

            return "details";
        } else {
            throw new ResourceNotFoundException(); 
        }
    }
}
