package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import it.cnr.biovoices_matrixes_tool.model.MatrixB;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MatrixBService {

    List<String> findDistinctValueByType(String factor);

    List<MatrixB> findByValue(String value);

    List<String> findDistinctByMmlFormatFace();

    List<MatrixB> findByActivityAndValueIn(String activity, List<String> values);

    List<String> findDistinctByActivity();

    List<String> findDistinctByType();

    List<MatrixB> findAll();

    MatrixB save(MatrixB matrixB);

    List<MatrixB> findByActivityAndValueAndMmlFormatFace(String activity, String values, String phase);


}
