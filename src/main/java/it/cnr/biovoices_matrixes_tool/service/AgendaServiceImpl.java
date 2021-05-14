package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.Agenda;
import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import it.cnr.biovoices_matrixes_tool.repository.AgendaRepository;
import it.cnr.biovoices_matrixes_tool.repository.FactSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Override
    public Agenda findByFactSheet(FactSheet factSheet) {
        return agendaRepository.findByFactSheet(factSheet);
    }
}
