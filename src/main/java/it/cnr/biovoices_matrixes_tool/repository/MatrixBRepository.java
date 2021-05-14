package it.cnr.biovoices_matrixes_tool.repository;

import it.cnr.biovoices_matrixes_tool.model.MatrixB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatrixBRepository extends JpaRepository<MatrixB, Long> {

    @Query("SELECT DISTINCT type FROM MatrixB")
    List<String> findDistinctByType();

    @Query("SELECT DISTINCT value FROM MatrixB where type = ?1")
    List<String> findDistinctValueByType(String type);

    List<MatrixB> findByValue(String value);

    @Query("SELECT DISTINCT mmlFormatFace, color FROM MatrixB")
    List<String> findDistinctByMmlFormatFace();

    List<MatrixB> findByActivityAndValueIn(String activity, List<String> values);

    @Query("SELECT DISTINCT activity, mmlFormatFace, color FROM MatrixB")
    List<String> findDistinctByActivity();

    List<MatrixB> findByActivityAndValueAndMmlFormatFace(String activity, String value, String phase);

}
