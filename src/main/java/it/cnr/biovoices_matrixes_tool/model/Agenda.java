package it.cnr.biovoices_matrixes_tool.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class Agenda {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "agenda_join_setting_the_scene_agenda_items",
            joinColumns = @JoinColumn(
                    name = "agenda_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "agenda_item_id", referencedColumnName = "id"))
    private Collection<AgendaItem> settingTheSceneAgendaItems;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "agenda_join_working_phase_agenda_items",
            joinColumns = @JoinColumn(
                    name = "agenda_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "agenda_item_id", referencedColumnName = "id"))
    private Collection<AgendaItem> workingPhaseAgendaItems;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "agenda_join_wrap_up_agenda_items",
            joinColumns = @JoinColumn(
                    name = "agenda_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "agenda_item_id", referencedColumnName = "id"))
    private Collection<AgendaItem> wrapUpAgendaItems;

    @OneToOne
    private FactSheet factSheet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<AgendaItem> getSettingTheSceneAgendaItems() {
        return settingTheSceneAgendaItems;
    }

    public void setSettingTheSceneAgendaItems(Collection<AgendaItem> settingTheSceneAgendaItems) {
        this.settingTheSceneAgendaItems = settingTheSceneAgendaItems;
    }

    public Collection<AgendaItem> getWorkingPhaseAgendaItems() {
        return workingPhaseAgendaItems;
    }

    public void setWorkingPhaseAgendaItems(Collection<AgendaItem> workingPhaseAgendaItems) {
        this.workingPhaseAgendaItems = workingPhaseAgendaItems;
    }

    public Collection<AgendaItem> getWrapUpAgendaItems() {
        return wrapUpAgendaItems;
    }

    public void setWrapUpAgendaItems(Collection<AgendaItem> wrapUpAgendaItems) {
        this.wrapUpAgendaItems = wrapUpAgendaItems;
    }

    public FactSheet getFactSheet() {
        return factSheet;
    }

    public void setFactSheet(FactSheet factSheet) {
        this.factSheet = factSheet;
    }
}
