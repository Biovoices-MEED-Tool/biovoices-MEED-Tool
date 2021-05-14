package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import it.cnr.biovoices_matrixes_tool.model.MatrixB;
import it.cnr.biovoices_matrixes_tool.repository.MatrixBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatrixBServiceImpl implements MatrixBService{

    @Autowired
    private MatrixBRepository matrixBRepository;

    @Override
    public List<String> findDistinctByType() {
        return matrixBRepository.findDistinctByType();
    }

    @Override
    public List<String> findDistinctValueByType(String type) {
        return matrixBRepository.findDistinctValueByType(type);
    }

    @Override
    public List<MatrixB> findByValue(String value) {
        return matrixBRepository.findByValue(value);
    }

    @Override
    public List<String> findDistinctByMmlFormatFace() {
        return matrixBRepository.findDistinctByMmlFormatFace();
    }

    @Override
    public List<MatrixB> findByActivityAndValueIn(String activity, List<String> values) {
        return matrixBRepository.findByActivityAndValueIn(activity, values);
    }

    @Override
    public List<String> findDistinctByActivity() {
        return matrixBRepository.findDistinctByActivity();
    }

    @Override
    public List<MatrixB> findAll() {
        return matrixBRepository.findAll();
    }

    @Override
    public MatrixB save(MatrixB matrixB) {
        return matrixBRepository.save(matrixB);
    }

    @Override
    public List<MatrixB> findByActivityAndValueAndMmlFormatFace(String activity, String value, String phase) {
        return matrixBRepository.findByActivityAndValueAndMmlFormatFace(activity, value, phase);
    }
}
