package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Activity {

    @Id
    @GeneratedValue
    private Long id;

    private String name, planningTime, timeEstimated, numberOfAudience, compositionOfAudience;

    @Column(columnDefinition = "text")
    private String briefDescription;

    @Column(columnDefinition = "text")
    private String methodology;

    @Column(columnDefinition = "text")
    private String objective;

    @Column(columnDefinition = "text")
    private String levelOfComplexityAndPossibleChallenges;

    @Column(columnDefinition = "text")
    private String picture;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "activity_join_sources",
            joinColumns = @JoinColumn(
                    name = "activity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "source_id", referencedColumnName = "id"))
    private Collection<Source> sources;

    @Column(columnDefinition = "text")
    private String relevantSources;

    @Column(columnDefinition = "text")
    private String experienceLevelRequired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanningTime() {
        return planningTime;
    }

    public void setPlanningTime(String planningTime) {
        this.planningTime = planningTime;
    }

    public String getTimeEstimated() {
        return timeEstimated;
    }

    public void setTimeEstimated(String timeEstimated) {
        this.timeEstimated = timeEstimated;
    }

    public String getNumberOfAudience() {
        return numberOfAudience;
    }

    public void setNumberOfAudience(String numberOfAudience) {
        this.numberOfAudience = numberOfAudience;
    }

    public String getCompositionOfAudience() {
        return compositionOfAudience;
    }

    public void setCompositionOfAudience(String compositionOfAudience) {
        this.compositionOfAudience = compositionOfAudience;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getLevelOfComplexityAndPossibleChallenges() {
        return levelOfComplexityAndPossibleChallenges;
    }

    public void setLevelOfComplexityAndPossibleChallenges(String levelOfComplexityAndPossibleChallenges) {
        this.levelOfComplexityAndPossibleChallenges = levelOfComplexityAndPossibleChallenges;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRelevantSources() {
        return relevantSources;
    }

    public void setRelevantSources(String relevantSources) {
        this.relevantSources = relevantSources;
    }

    public String getExperienceLevelRequired() {
        return experienceLevelRequired;
    }

    public void setExperienceLevelRequired(String experienceLevelRequired) {
        this.experienceLevelRequired = experienceLevelRequired;
    }

    public Collection<Source> getSources() {
        return sources;
    }

    public void setSources(Collection<Source> sources) {
        this.sources = sources;
    }
}
