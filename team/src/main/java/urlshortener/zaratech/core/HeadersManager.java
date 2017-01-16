package urlshortener.zaratech.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.domain.Click;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.zaratech.domain.Statistics;
import urlshortener.zaratech.domain.UserAgentDetails;
import net.sf.uadetector.*;
import net.sf.uadetector.service.UADetectorServiceFactory;

@Component
public class HeadersManager {

    private final Logger logger = LoggerFactory.getLogger(HeadersManager.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    public UrlDetails getDetails(String id) {

        ShortURL url = getUrlDetails(id);
        Long clicks = getClickDetails(id);
        Long visitors = getVisitors(id);
        return new UrlDetails(id, url.getTarget(), url.getCreated(), clicks, visitors);
    }

    public UserAgentDetails getUA(String agentStr) {

        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

        ReadableUserAgent agent = parser.parse(agentStr);

        VersionNumber browserVers = agent.getVersionNumber();
        OperatingSystem os = agent.getOperatingSystem();

        return new UserAgentDetails(agent.getName(), browserVers.toVersionString(), os.getName());
    }

    public Statistics getStatistics(List<Click> clicks) {
        Statistics st = new Statistics();
        st.addTotal(clicks.size());
        
        for(Click click : clicks){
            
            if (st.getIndexBrowser(click.getBrowser()) == -1) {
                //logger.info("NEW browser "+ click.getBrowser()); 
                st.insertBrowser(click.getBrowser());
            } else {
                //logger.info("OLD browser "+ click.getBrowser());
                st.updateclicksForBrowser(st.getIndexBrowser(click.getBrowser()));
            }

            int browserIndex = st.getIndexBrowser(click.getBrowser());
            
            if (st.getIndexVersion(browserIndex, click.getVersion()) == -1) {
                //logger.info("NEW version "+ click.getVersion());
                st.insertVersion(browserIndex, click.getVersion());
            } else {
                int versionIndex = st.getIndexVersion(browserIndex, click.getVersion());
                //logger.info("OLD version "+ click.getVersion());
                
                st.updateclicksForBrowserAndVersion(browserIndex, versionIndex);
            }

            String os = click.getOs();
            if (os.indexOf("Linux") >= 0) {
                os = "Linux";
            }

            int index = st.getIndexOs(os);
            if (index < 0) {
                st.insertOs(os);
            } else {
                st.updateclicksForOs(index);
            }
        }

        st.insertCharts();
        return st;
    }
    private ShortURL getUrlDetails(String id) {
        return shortURLRepository.findByKey(id);
    }

    private Long getClickDetails(String id) {
        return clickRepository.clicksByHash(id);
    }

    private Long getVisitors(String id) {
        return clickRepository.visitorsByHash(id);
    }
}
