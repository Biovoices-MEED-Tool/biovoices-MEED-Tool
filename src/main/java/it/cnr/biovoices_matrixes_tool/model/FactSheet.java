package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FactSheet {

    @Id
    @GeneratedValue
    private Long id;

    private String name, planningTime, budget, timeEstimated, targetAudience, numberOfAudience, groupComposition, facilitatorSkills;

    @Column(columnDefinition = "text")
    private String briefDescription;

    @Column(columnDefinition = "text")
    private String methodology;

    @Column(columnDefinition = "text")
    private String objective;

    @Column(columnDefinition = "text")
    private String levelOfKnowledgeRequired;

    @Column(columnDefinition = "text")
    private String example;

    @Column(columnDefinition = "text")
    private String relevantSources;

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

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getTimeEstimated() {
        return timeEstimated;
    }

    public void setTimeEstimated(String timeEstimated) {
        this.timeEstimated = timeEstimated;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getNumberOfAudience() {
        return numberOfAudience;
    }

    public void setNumberOfAudience(String numberOfAudience) {
        this.numberOfAudience = numberOfAudience;
    }

    public String getGroupComposition() {
        return groupComposition;
    }

    public void setGroupComposition(String groupComposition) {
        this.groupComposition = groupComposition;
    }

    public String getFacilitatorSkills() {
        return facilitatorSkills;
    }

    public void setFacilitatorSkills(String facilitatorSkills) {
        this.facilitatorSkills = facilitatorSkills;
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

    public String getLevelOfKnowledgeRequired() {
        return levelOfKnowledgeRequired;
    }

    public void setLevelOfKnowledgeRequired(String levelOfKnowledgeRequired) {
        this.levelOfKnowledgeRequired = levelOfKnowledgeRequired;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getRelevantSources() {
        return relevantSources;
    }

    public void setRelevantSources(String relevantSources) {
        this.relevantSources = relevantSources;
    }
}
