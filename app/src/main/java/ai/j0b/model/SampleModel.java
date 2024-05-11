package ai.j0b.model;

public class SampleModel {
    private Integer position;
    private Integer image;
    private String text;
    private Integer color;

    public SampleModel() {
    }

    public SampleModel(Integer position, Integer image, String text, Integer color) {
        this.position = position;
        this.image = image;
        this.text = text;
        this.color = color;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SampleModel that = (SampleModel) obj;
        return java.util.Objects.equals(position, that.position) &&
                java.util.Objects.equals(image, that.image) &&
                java.util.Objects.equals(text, that.text) &&
                java.util.Objects.equals(color, that.color);
    }

    public int hashCode() {
        return java.util.Objects.hash(position, image, text, color);
    }

    public String toString() {
        return "SampleModel{" +
                "position=" + position +
                ", image=" + image +
                ", text='" + text + '\'' +
                ", color=" + color +
                '}';
    }
}