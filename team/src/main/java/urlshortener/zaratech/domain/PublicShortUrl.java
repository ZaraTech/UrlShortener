package urlshortener.zaratech.domain;

import java.net.URI;
import java.sql.Date;

public class PublicShortUrl {

    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String owner;
    private Integer mode;
    private String ip;
    private String country;
    private URI qr;

    public PublicShortUrl() {
        this.hash = null;
        this.target = null;
        this.uri = null;
        this.created = null;
        this.owner = null;
        this.mode = null;
        this.ip = null;
        this.country = null;
    }

    public PublicShortUrl(String hash, String target, URI uri, Date created, String owner, Integer mode, String ip,
            String country) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.ip = ip;
        this.country = country;
    }

    public String getHash() {
        return hash;
    }

    public String getTarget() {
        return target;
    }

    public URI getUri() {
        return uri;
    }

    public Date getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getMode() {
        return mode;
    }

    public String getIP() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public URI getQR() {
        return qr;
    }

    public void setQR(URI qr) {
        this.qr = qr;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public URI getQr() {
        return qr;
    }

    public void setQr(URI qr) {
        this.qr = qr;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
