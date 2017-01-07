package urlshortener.zaratech.domain;

import java.io.Serializable;
import java.net.URI;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadTaskDataStruct implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("progress")
    private String progress;
    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String owner;
    private Integer mode;
    private String ip;
    private URI qr;

    public UploadTaskDataStruct(URI url, String progress) {
        setTarget(url.toString());
        this.progress = progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((progress == null) ? 0 : progress.hashCode());
        result = prime * result + ((getUri().toString() == null) ? 0 : getUri().toString().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UploadTaskDataStruct other = (UploadTaskDataStruct) obj;
        if (progress == null) {
            if (other.progress != null)
                return false;
        } else if (!progress.equals(other.progress))
            return false;
        if (getUri().toString() == null) {
            if (other.getUri().toString() != null)
                return false;
        } else if (!getUri().toString().equals(other.getUri().toString()))
            return false;
        return true;
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

}