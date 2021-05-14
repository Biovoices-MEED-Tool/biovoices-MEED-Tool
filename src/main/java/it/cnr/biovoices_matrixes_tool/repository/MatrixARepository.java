package it.cnr.biovoices_matrixes_tool.repository;

import it.cnr.biovoices_matrixes_tool.model.MatrixA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatrixARepository extends JpaRepository<MatrixA, Long> {

    @Query("SELECT DISTINCT determiningFactor FROM MatrixA")
    List<String> findDistinctByDeterminingFactor();

    @Query("SELECT DISTINCT value FROM MatrixA where determiningFactor = ?1")
    List<String> findDistinctByDeterminingFactor(String determiningFactor);

    @Query("SELECT DISTINCT value FROM MatrixA where determiningFactor = ?1")
    List<String> findDistinctValueByDeterminingFactor(String determiningFactor);

    List<MatrixA> findByValue(String value);

    @Query("SELECT DISTINCT mmlFormat, color FROM MatrixA")
    List<String> findDistinctByMmlFormat();

    List<MatrixA> findByMmlFormatAndValueIn(String mmlFormat, List<String> values);


}
