package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Entity
public class AgendaItem {

    @Id
    @GeneratedValue
    private Long id;

    private String name, description;

    @Transient
    private String timeline;

    @Transient
    private List<Activity> suitableActivities;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public List<Activity> getSuitableActivities() {
        return suitableActivities;
    }

    public void setSuitableActivities(List<Activity> suitableActivities) {
        this.suitableActivities = suitableActivities;
    }
}
