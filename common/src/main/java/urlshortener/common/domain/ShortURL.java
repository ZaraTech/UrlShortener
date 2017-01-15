package urlshortener.common.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String owner;
    private Integer mode;
    private String ip;
    private URI qr;

    // redirection checks
    private boolean correct;
    private Date lastCorrectDate;
    
    @Deprecated
    public ShortURL(String hash, String target, URI uri, String sponsor,
            Date created, String owner, Integer mode, Boolean safe, String ip,
            String country) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.ip = ip;
        this.qr = null;
        this.correct = true;
        this.lastCorrectDate = null;
    }

    public ShortURL(String hash, String target, URI uri, Date created, String owner, Integer mode, String ip) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.ip = ip;
        this.qr = null;
        this.correct = true;
        this.lastCorrectDate = null;
    }

    public ShortURL(String hash, String target, URI uri, Date created, String owner, Integer mode, String ip,
            boolean correct, Date lastCorrectDate) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.ip = ip;
        this.qr = null;
        this.correct = correct;
        this.lastCorrectDate = lastCorrectDate;
    }

    public ShortURL() {
        this.hash = null;
        this.target = null;
        this.uri = null;
        this.created = null;
        this.owner = null;
        this.mode = null;
        this.ip = null;
        this.qr = null;
        this.correct = true;
        this.lastCorrectDate = null;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Date getLastCorrectDate() {
        return lastCorrectDate;
    }

    public void setLastCorrectDate(Date lastCorrectDate) {
        this.lastCorrectDate = lastCorrectDate;
    }

    @Override
    public String toString() {
        return "ShortURL [hash=" + hash + ", target=" + target + ", uri=" + uri + ", created=" + created + ", owner="
                + owner + ", mode=" + mode + ", ip=" + ip + ", qr=" + qr + ", correct=" + correct + ", lastCorrectDate="
                + lastCorrectDate + "]";
    }
}
