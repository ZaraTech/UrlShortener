package urlshortener.zaratech.domain;

public class UserAgentDetails {

    private String browserName;
    private String browserVersion;
    private String osName;
    
    public UserAgentDetails(String browserName, String browserVersion, String osName) {
        super();
        this.browserName = browserName;
        this.browserVersion = browserVersion;
        this.osName = osName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public String getOsName() {
        return osName;
    }
}
