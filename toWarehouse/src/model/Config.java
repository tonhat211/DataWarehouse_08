package model;

public class Config {
    public int id;
    public String source;
    public String description;
    public String directory;
    public String fileName;
    public String ext;
    public String creatAt;
    public String createBy;
    public String queryDate;

    public Config(int id, String source, String description, String directory, String fileName, String ext, String creatAt, String createBy,String queryDate) {
        this.id = id;
        this.source = source;
        this.description = description;
        this.directory = directory;
        this.fileName = fileName;
        this.ext = ext;
        this.creatAt = creatAt;
        this.createBy = createBy;
        this.queryDate = queryDate;
    }

    public String getFilePath() {
        return this.directory+"\\\\"+this.fileName+"_"+queryDate +"."+ext;
    }
    public String getSource() {
        return source;
    }
}
