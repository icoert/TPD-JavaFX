package Models;

import java.io.Serializable;

public class Files implements Serializable {
    private String FileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public Files(String FileName) {
        this.FileName = FileName;
    }

}
