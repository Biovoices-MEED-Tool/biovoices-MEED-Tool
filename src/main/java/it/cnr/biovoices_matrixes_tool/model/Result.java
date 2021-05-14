package it.cnr.biovoices_matrixes_tool.model;

import java.util.Map;

public class Result implements Comparable<Result> {

    private String yAxis;
    private String color;
    private double mediumScore;
    private Map<String, Double> factorScoresMap;
    private String formatPhase;
    private String mml;
    private String estimatedPlanningTime;

    public String getEstimatedPlanningTime() {
        return estimatedPlanningTime;
    }

    public void setEstimatedPlanningTime(String estimatedPlanningTime) {
        this.estimatedPlanningTime = estimatedPlanningTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public double getMediumScore() {
        return mediumScore;
    }

    public void setMediumScore(double mediumScore) {
        this.mediumScore = mediumScore;
    }

    public Map<String, Double> getFactorScoresMap() {
        return factorScoresMap;
    }

    public void setFactorScoresMap(Map<String, Double> factorScoresMap) {
        this.factorScoresMap = factorScoresMap;
    }

    public String getFormatPhase() {return formatPhase;}

    public void setFormatPhase(String formatPhase) {this.formatPhase = formatPhase;}

    public String getMml() {return mml;}

    public void setMml(String mml) {this.mml = mml;}

@Override
public int compareTo(Result o) {
        return (this.getFormatPhase()+this.getMml()).compareTo(o.getFormatPhase()+ o.getMml());
        }
}
