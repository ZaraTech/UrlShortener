package urlshortener.common.domain;

import java.sql.Date;

public class Click {

    private Long id;
    private String hash;
    private Date created;
    private String referrer;
    private String browser;
    private String version;
    private String os;
    private String platform;
    private String ip;
    private String country;

    public Click(Long id, String hash, Date created, String referrer,
                 String browser,String version,String os, String platform, String ip, String country) {
        this.id = id;
        this.hash = hash;
        this.created = created;
        this.referrer = referrer;
        this.browser = browser;
        this.version = version;
        this.os = os;
        this.platform = platform;
        this.ip = ip;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public Date getCreated() {
        return created;
    }

    public String getReferrer() {
        return referrer;
    }

    public String getBrowser() {
        return browser;
    }

    public String getVersion() {
        return version;
    }

    public String getOs() {
        return os;
    }

    public String getPlatform() {
        return platform;
    }

    public String getIp() {
        return ip;
    }

    public String getCountry() {
        return country;
    }
}