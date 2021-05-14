package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.Activity;
import org.springframework.stereotype.Service;

@Service
public interface ActivityService {

    Activity findByName(String name);

}
