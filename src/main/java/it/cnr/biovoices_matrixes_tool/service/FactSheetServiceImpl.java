package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import it.cnr.biovoices_matrixes_tool.repository.FactSheetRepository;
import it.cnr.biovoices_matrixes_tool.repository.MatrixARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactSheetServiceImpl implements FactSheetService {

    @Autowired
    private FactSheetRepository factSheetRepository;

    @Override
    public FactSheet findByName(String name) {
        return factSheetRepository.findByName(name);
    }
}
