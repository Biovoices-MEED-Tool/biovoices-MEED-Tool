package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FactSheetService {

    FactSheet findByName(String name);

}
