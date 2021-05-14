package it.cnr.biovoices_matrixes_tool.service;

import it.cnr.biovoices_matrixes_tool.model.Activity;
import it.cnr.biovoices_matrixes_tool.model.FactSheet;
import it.cnr.biovoices_matrixes_tool.repository.ActivityRepository;
import it.cnr.biovoices_matrixes_tool.repository.FactSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Activity findByName(String name) {
        return activityRepository.findByName(name);
    }
}
