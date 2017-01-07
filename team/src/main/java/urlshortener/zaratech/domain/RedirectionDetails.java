package urlshortener.zaratech.domain;

import java.net.URI;

public class RedirectionDetails {

    private URI jobUrl;

    public RedirectionDetails(URI jobUrl) {
        this.jobUrl = jobUrl;
    }

    public URI getJobUrl() {
        return jobUrl;
    }
}
