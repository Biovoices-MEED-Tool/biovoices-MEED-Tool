package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import it.cnr.biovoices_matrixes_tool.repository.MatrixARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatrixAServiceImpl implements MatrixAService{

    @Autowired
    private MatrixARepository matrixARepository;

    @Override
    public List<String> findDistinctByDeterminingFactor() {

        return matrixARepository.findDistinctByDeterminingFactor();

    }

    @Override
    public List<String> findDistinctByDeterminingFactor(String determiningFactor) {
        return matrixARepository.findDistinctByDeterminingFactor(determiningFactor);
    }

    @Override
    public List<String> findDistinctValueByDeterminingFactor(String determiningFactor) {
        return matrixARepository.findDistinctValueByDeterminingFactor(determiningFactor);
    }

    @Override
    public List<MatrixA> findByValue(String value) {
        return matrixARepository.findByValue(value);
    }

    @Override
    public List<String> findDistinctByMmlFormat() {
        return matrixARepository.findDistinctByMmlFormat();
    }

    @Override
    public List<MatrixA> findByMmlFormatAndValueIn(String mmlFormat, List<String> values) {
        return matrixARepository.findByMmlFormatAndValueIn(mmlFormat, values);
    }

    @Override
    public List<MatrixA> findAll() {
        return matrixARepository.findAll();
    }

    @Override
    public MatrixA save(MatrixA matrixA) {
        return matrixARepository.save(matrixA);
    }
}
