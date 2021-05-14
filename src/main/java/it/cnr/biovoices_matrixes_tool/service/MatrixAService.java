package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MatrixAService {

    List<String> findDistinctByDeterminingFactor();

    List<String> findDistinctByDeterminingFactor(String determiningFactor);

    List<String> findDistinctValueByDeterminingFactor(String determiningFactor);

    List<MatrixA> findByValue(String value);

    List<String> findDistinctByMmlFormat();

    List<MatrixA> findByMmlFormatAndValueIn(String mmlFormat, List<String> values);

    List<MatrixA> findAll();

    MatrixA save(MatrixA matrixA);
}
