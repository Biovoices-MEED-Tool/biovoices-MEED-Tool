package it.cnr.biovoices_matrixes_tool.repository;

import it.cnr.biovoices_matrixes_tool.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findByName(String name);

}
