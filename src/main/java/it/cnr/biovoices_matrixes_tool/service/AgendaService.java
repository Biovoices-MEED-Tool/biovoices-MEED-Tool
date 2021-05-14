package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.Agenda;
import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import org.springframework.stereotype.Service;

@Service
public interface AgendaService {

    Agenda findByFactSheet(FactSheet factSheet);

}
