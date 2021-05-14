package it.cnr.biovoices_matrixes_tool.repository;

import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactSheetRepository extends JpaRepository<FactSheet, Long> {

    FactSheet findByName(String name);

}
