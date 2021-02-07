package Models;

import java.io.Serializable;

public class Users implements Serializable {
    private String Token;
    private String Username;
    private Boolean Admin;
    private Boolean Upload;
    private Boolean Download;
    private Boolean Visualization;

    public String getToken() {
        return Token;
    }

    public void setName(String token) {
        Token = token;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) { Username = username; }

    public Boolean getUpload () {
        return Upload;
    }

    public void setUpload(Boolean upload) {
        Upload = upload;
    }

    public Boolean getDownload () {
        return Download;
    }

    public void setDownload(Boolean download) {
        Download = download;
    }

    public Boolean getVisualization () {
        return Visualization;
    }

    public void setVisualization(Boolean visualization) {
        Visualization = visualization;
    }

    public Users(String Token, String Username, Boolean Admin, Boolean Upload, Boolean Download, Boolean Visualization) {
        this.Token = Token;
        this.Username = Username;
        this.Admin = Admin;
        this.Upload = Upload;
        this.Download = Download;
        this.Visualization = Visualization;
    }

}
