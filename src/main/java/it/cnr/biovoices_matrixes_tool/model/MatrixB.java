package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MatrixB implements Comparable<MatrixB> {

    @Id
    @GeneratedValue
    private Long id;

    private String type, mmlFormatFace, activity, value, color;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMmlFormatFace() {
        return mmlFormatFace;
    }

    public void setMmlFormatFace(String mmlFormatFace) {
        this.mmlFormatFace = mmlFormatFace;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
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
    public int compareTo(MatrixB o) {
        return this.getId().compareTo(o.getId());
    }

}
