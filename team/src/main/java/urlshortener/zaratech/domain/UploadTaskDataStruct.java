package urlshortener.zaratech.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadTaskDataStruct implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("url")
    private String url;

    @JsonProperty("progress")
    private String progress;

    public UploadTaskDataStruct(String url, String progress) {
        this.url = url;
        this.progress = progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((progress == null) ? 0 : progress.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}