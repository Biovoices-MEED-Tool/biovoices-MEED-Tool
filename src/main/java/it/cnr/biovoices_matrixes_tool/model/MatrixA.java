package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MatrixA  implements Comparable<MatrixA> {

    @Id
    @GeneratedValue
    private Long id;

    private String determiningFactor, mmlFormat, value, color;

    private double score;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeterminingFactor() {
        return determiningFactor;
    }

    public void setDeterminingFactor(String determiningFactor) {
        this.determiningFactor = determiningFactor;
    }

    public String getMmlFormat() {
        return mmlFormat;
    }

    public void setMmlFormat(String mmlFormat) {
        this.mmlFormat = mmlFormat;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(MatrixA o) {
        return this.getId().compareTo(o.getId());
    }
}
