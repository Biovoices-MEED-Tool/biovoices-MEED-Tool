package it.cnr.biovoices_matrixes_tool.repository;

import it.cnr.biovoices_matrixes_tool.model.Agenda;
import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    Agenda findByFactSheet(FactSheet factSheet);

}
