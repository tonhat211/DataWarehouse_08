package model;

public class Source_dim {
    private int sourceId;         // Surrogate Key (Primary Key)
    private String sourceName;    // Name of the source

    // Constructor
    public Source_dim(int sourceId, String sourceName) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
    }

    // Default Constructor
    public Source_dim() {
    }

    // Getters and Setters
    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "SourceDim{" +
                "sourceId=" + sourceId +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
