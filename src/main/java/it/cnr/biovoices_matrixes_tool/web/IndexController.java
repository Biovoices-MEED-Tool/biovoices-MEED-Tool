package it.cnr.biovoices_matrixes_tool.web;

import it.cnr.biovoices_matrixes_tool.model.*;
import it.cnr.biovoices_matrixes_tool.service.*;
import it.cnr.biovoices_matrixes_tool.utils.AgendaCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private MatrixAService matrixAService;

    @Autowired
    private MatrixBService matrixBService;

    @Autowired
    private FactSheetService factSheetService;

    @Autowired
    private AgendaService agendaService;

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @GetMapping(value = "/chooseBiovoicesCluster")
    public String chooseBiovoicesCluster(Model model) {

        List<String> values = matrixAService.findDistinctByDeterminingFactor("BIOVOICES Challenge Cluster");
        Collections.sort(values);
        values.add("I don't know");
        model.addAttribute("values", values);
        return "chooseBiovoicesCluster";
    }

    @PostMapping(value = "/sessionKill")
    public String sessionKill(Model model, @RequestParam("SID") String SID, HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

    @GetMapping(value = "/step1Next/{value}")
    public String step1Next(Model model, @PathVariable("value") String value, HttpServletRequest request) {
        value = value.replace("2F", "/");
        request.getSession().setAttribute("biovoicesChallengeClusterGlobal", value);
        model.addAttribute("biovoicesChallengeCluster", value);

        return "redirect:/chooseGroupComposition";
    }

    @GetMapping(value = "/chooseGroupComposition")
    public String chooseGroupComposition(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));

        return "chooseGroupComposition";
    }

    @RequestMapping(value = "/step2Next", method = RequestMethod.POST)
    @ResponseBody
    public void step2Next(@RequestParam("results") String[] results, HttpServletRequest request) {
        request.getSession().setAttribute("groupCompositionViewGlobal", results);
        if(results.length > 1) {
            request.getSession().setAttribute("groupCompositionGlobal", "Heterogeneous");
        }
        else {
            if(!results[0].equals("I don't know")) {
                request.getSession().setAttribute("groupCompositionGlobal", "Homogeneous");
            }
        }
    }

    @GetMapping(value = "/chooseGroupSize")
    public String chooseGroupSize(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        List<String> values = matrixAService.findDistinctByDeterminingFactor("Group size");
        Collections.sort(values);
        values.add("I don't know");
        model.addAttribute("values", values);

        return "chooseGroupSize";
    }

    @GetMapping(value = "/step3Next/{value}")
    public String step3Next(Model model, @PathVariable("value") String value, HttpServletRequest request) {

        request.getSession().setAttribute("groupSizeGlobal", value);
        model.addAttribute("biovoicesChallengeCluster", value);

        return "redirect:/showEventsformats";
    }

    @GetMapping(value = "/showEventsformats")
    public String showEventsformats(Model model, HttpServletRequest request) {

        List<Result> results = new ArrayList<>();
        List<String> yAxis = matrixAService.findDistinctByMmlFormat();
        List<String> list = new ArrayList<>();
        if(!request.getSession().getAttribute("biovoicesChallengeClusterGlobal").equals("I don't know")) {
            list.add(request.getSession().getAttribute("biovoicesChallengeClusterGlobal").toString());
        }
        if(!request.getSession().getAttribute("groupCompositionGlobal").equals("")) {
            list.add(request.getSession().getAttribute("groupCompositionGlobal").toString());
        }
        if(!request.getSession().getAttribute("groupSizeGlobal").equals("I don't know")) {
            list.add(request.getSession().getAttribute("groupSizeGlobal").toString());
        }

        if(list.size() > 0) {
            for (String y : yAxis) {
                String[] split = y.split(",");
                List<MatrixA> matrixAList = matrixAService.findByMmlFormatAndValueIn(split[0], list);
                Result result = new Result();
                result.setyAxis(split[0]);

                //result.setColor(split[1]);
                Double mediumScore = 0.0;
                Map<String, Double> map = new HashMap<>();
                for (MatrixA a : matrixAList) {
                    map.put(a.getDeterminingFactor(), a.getScore());
                    mediumScore += a.getScore();
                }
                Double mediumScoreFinal = mediumScore / matrixAList.size();
                /* METODO PER TRUNCATE A 2 CIFRE DECIMALI */
                double mediumScoreTruncate = Math.round(mediumScoreFinal * 100.0) / 100.0;;
                result.setMediumScore(mediumScoreTruncate);

                if (mediumScoreFinal < 2) {
                    result.setColor("red");
                } else if (mediumScoreFinal < 3) {
                    result.setColor("yellow");

                } else {
                    result.setColor("green");

                }
                result.setFactorScoresMap(map);
                results.add(result);
            }
            results.sort(Comparator.comparing(a -> a.getMediumScore()));
            Collections.reverse(results);
            model.addAttribute("results", results);
            model.addAttribute("emptyListMessage", "");
        }
        else {
            model.addAttribute("emptyListMessage", "No suggestions. Please answer a question at least");
        }

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("factSheet", null);

        return "showEventsformats";
    }

    @GetMapping(value = "/viewFactSheet")
    public String viewFactSheet(Model model, @RequestParam("mmlName") String mmlName, HttpServletRequest request) {

        FactSheet factSheet = factSheetService.findByName(mmlName);
        model.addAttribute("factSheet", factSheet);
        return "/modals/factsheetModal :: viewMMLFactSheet";
    }

    @PostMapping(value = "/getScoresB")
    public String getScoresB(Model model, @RequestParam("selectedValues") String[] selectedValues, HttpServletRequest request) {
        List<Result> resultsB = new ArrayList<>();
        List<String> yAxis = matrixBService.findDistinctByActivity();
        List<String> list = Arrays.asList(selectedValues);
        for(String y : yAxis) {
            String[] split = y.split(",");
            List<MatrixB> matrixBList = matrixBService.findByActivityAndValueIn(split[0], list);
            Result result = new Result();
            result.setyAxis(split[0]);
            result.setFormatPhase(split[1]);
            //result.setColor(split[1]);
            Double mediumScore = 0.0;
            Map<String, Double> map = new HashMap<>();
            for (MatrixB b : matrixBList) {
                map.put(b.getType(), b.getScore());
                mediumScore += b.getScore();
            }
            Double mediumScoreFinal = mediumScore / matrixBList.size();
            result.setMediumScore(mediumScoreFinal);
            result.setFactorScoresMap(map);
            // Inizio parte set colore in base al punteggio
            if (mediumScoreFinal<2){
                result.setColor("red");
            }
            else if (mediumScoreFinal<3){
                result.setColor("yellow");

            }
            else{
                result.setColor("green");

            }
            resultsB.add(result);
            // Fine parte set colore in base al punteggio
        }

        //Inizio creazione e ordinamento liste attività in base alla fase

        List<Result> scene = new ArrayList<Result>();
        List<Result> working = new ArrayList<Result>();
        List<Result> wrap = new ArrayList<Result>();

        for (Result r : resultsB){
            if (r.getFormatPhase().equals("Setting the scene")){
                scene.add(r);
            }
            else if(r.getFormatPhase().equals("Working Phase")){
                working.add(r);
            }
            else {
                wrap.add(r);
            }

        }


        scene.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(scene);

        working.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(working);

        wrap.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(wrap);
        //Fine creazione e ordinamento liste attività in base alla fase


        model.addAttribute("sceneResultsB", scene.subList(0, 3));
        model.addAttribute("workingResultsB", working.subList(0, 3));
        model.addAttribute("wrapResultsB", wrap.subList(0, 3));

        return "index :: resultsBFragment";
    }

    @RequestMapping(value = "/chooseMml", method = RequestMethod.POST)
    @ResponseBody
    public void chooseMml(@RequestParam("mmlName") String mmlName, HttpServletRequest request) {
        request.getSession().setAttribute("mmlNameGlobal", mmlName);
        request.getSession().setAttribute("chosenOnlyActivitiesSessionFormatGlobal", null);
        request.getSession().setAttribute("chosenOnlyActivitiesWorkingPhaseGlobal", null);
        request.getSession().setAttribute("chosenOnlyActivitiesWrapUpGlobal", null);
    }

    @GetMapping(value = "/viewAgenda")
    public String viewAgenda(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));
        model.addAttribute("chosenOnlyActivitiesSessionFormatGlobal", request.getSession().getAttribute("chosenOnlyActivitiesSessionFormatGlobal"));

        FactSheet factSheet = factSheetService.findByName(request.getSession().getAttribute("mmlNameGlobal").toString());
        Agenda agenda = agendaService.findByFactSheet(factSheet);

        model.addAttribute("agenda", agenda);

        List<Result> resultsB = new ArrayList<>();
        List<String> yAxis = matrixBService.findDistinctByActivity();
        List<String> list = new ArrayList<>();
        list.add(request.getSession().getAttribute("groupCompositionGlobal").toString());
        list.add(request.getSession().getAttribute("groupSizeGlobal").toString());

        model.addAttribute("activityFactSheet", null);

        return "viewAgenda";
    }

    @GetMapping(value = "/chooseGoal")
    public String chooseGoal(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));

        List<String> values = matrixBService.findDistinctValueByType("Goal");
        Collections.sort(values);
        values.add("I don't know");
        model.addAttribute("values", values);

        return "chooseGoal";
    }

    @GetMapping(value = "/step4Next/{value}")
    public String step4Next(Model model, @PathVariable("value") String value, HttpServletRequest request) {

        request.getSession().setAttribute("biovoicesGoalGlobalSettingScene", value);
        model.addAttribute("biovoicesGoalGlobal", value);

        return "redirect:/chooseParticipantExperienceLevel";
    }

    @GetMapping(value = "/chooseParticipantExperienceLevel")
    public String chooseParticipantExperienceLevel(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));
        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));

        List<String> values = matrixBService.findDistinctValueByType("Participants' experience level");
        Collections.sort(values);
        values.add("I don't know");
        model.addAttribute("values", values);

        return "chooseParticipantExperienceLevel";
    }

    @GetMapping(value = "/step5Next/{value}")
    public String step5Next(Model model, @PathVariable("value") String value, HttpServletRequest request) {

        request.getSession().setAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", value);
        model.addAttribute("biovoicesParticipantExperienceLevelGlobal", value);

        return "redirect:/chooseSessionFormat";
    }

    @GetMapping(value = "/chooseSessionFormat")
    public String chooseSessionFormat(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));
        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene"));

        List<String> values = matrixBService.findDistinctValueByType("Session format");
        Collections.sort(values);
        values.add("I don't know");
        model.addAttribute("values", values);

        return "chooseSessionFormat";
    }

    @GetMapping(value = "/step6Next/{value}")
    public String step6Next(Model model, @PathVariable("value") String value, HttpServletRequest request) {

        request.getSession().setAttribute("biovoicesSessionFormatGlobalSettingScene", value);
        model.addAttribute("biovoicesSessionFormatGlobal", value);

        return "redirect:/customizeAgendaSettingTheScene";
    }

    @GetMapping(value = "/customizeAgendaSettingTheScene")
    public String customizeAgendaSettingTheScene(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));
        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene"));
        model.addAttribute("biovoicesSessionFormatGlobalSettingScene", request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene"));

        model.addAttribute("chosenOnlyActivitiesSessionFormatGlobal", request.getSession().getAttribute("chosenOnlyActivitiesSessionFormatGlobal"));

        FactSheet factSheet = factSheetService.findByName(request.getSession().getAttribute("mmlNameGlobal").toString());
        Agenda agenda = agendaService.findByFactSheet(factSheet);

        model.addAttribute("agenda", agenda);

        List<Result> resultsB = new ArrayList<>();
        List<String> yAxis = matrixBService.findDistinctByActivity();
        List<String> list = new ArrayList<>();
        list.add(request.getSession().getAttribute("groupCompositionGlobal").toString());
        list.add(request.getSession().getAttribute("groupSizeGlobal").toString());
        list.add(request.getSession().getAttribute("biovoicesGoalGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene").toString());

        for(String y : yAxis) {
            String[] split = y.split(",");
            List<MatrixB> matrixBList = matrixBService.findByActivityAndValueIn(split[0], list);
            Result result = new Result();
            result.setyAxis(split[0]);
            result.setFormatPhase(split[1]);
            //result.setColor(split[1]);
            Double mediumScore = 0.0;
            Map<String, Double> map = new HashMap<>();
            for (MatrixB b : matrixBList) {
                map.put(b.getType(), b.getScore());
                mediumScore += b.getScore();
            }
            Double mediumScoreFinal = mediumScore / matrixBList.size();
            /* METODO PER TRUNCATE A 2 CIFRE DECIMALI */
            double mediumScoreTruncate = Math.round(mediumScoreFinal * 100.0) / 100.0;;
            result.setMediumScore(mediumScoreTruncate);
            result.setFactorScoresMap(map);
            // Inizio parte set colore in base al punteggio
            if (mediumScoreFinal<2){
                result.setColor("red");
            }
            else if (mediumScoreFinal<3){
                result.setColor("yellow");

            }
            else{
                result.setColor("green");

            }

            // Fine parte set colore in base al punteggio
            Activity activityInfo = activityService.findByName(split[0]);
            result.setEstimatedPlanningTime(activityInfo.getTimeEstimated());
            resultsB.add(result);
        }

        //Inizio creazione e ordinamento liste attività in base alla fase

        List<Result> scene = new ArrayList<Result>();
        List<Result> working = new ArrayList<Result>();
        List<Result> wrap = new ArrayList<Result>();

        for (Result r : resultsB){
            if (r.getFormatPhase().equals("Setting the scene")){
                scene.add(r);
            }
        }


        scene.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(scene);
        //Fine creazione e ordinamento liste attività in base alla fase


        model.addAttribute("sceneResultsB", scene);
        model.addAttribute("activityFactSheet", null);

        return "customizeAgendaSettingTheScene";
    }

    @GetMapping(value = "/viewActivityFactSheet")
    public String viewActivityFactSheet(Model model, @RequestParam("activityName") String activityName) {

        Activity activityFactSheet = activityService.findByName(activityName);
        model.addAttribute("activityFactSheet", activityFactSheet);
        return "/modals/activitiesModal :: viewActivityFactSheet";
    }

    @RequestMapping(value = "/agendaCustomizationSessionFormatEnd", method = RequestMethod.POST)
    @ResponseBody
    public String agendaCustomizationSessionFormatEnd(@RequestParam("rows") String[][] rows, HttpServletRequest request) {

        request.getSession().setAttribute("chosenActivitiesSessionFormatGlobal", rows);
        List<List<String>> tempRows = new ArrayList<>();
        for(int i = 1; i < rows.length; i++) {
            if(rows[i].length == 4 && !rows[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(rows[i][0].replace("*$", ","));
                temp.add(rows[i][1].replace("*$", ","));
                temp.add(rows[i][2].replace("*$", ","));
                temp.add(rows[i][3].replace("*$", ","));
                tempRows.add(temp);
            }
            if(rows[i].length == 2 && !rows[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(rows[i][0].replace("*$", ","));
                temp.add(rows[i][1].replace("*$", ","));
                tempRows.add(temp);
            }
            if(rows[i][0].contains("Working phase")) {
                break;
            }
        }

        String[][] chosenOnlyActivitiesSessionFormatGlobal = new String[tempRows.size()][];
        String[] blankArray = new String[0];
        for(int i=0; i < tempRows.size(); i++) {
            chosenOnlyActivitiesSessionFormatGlobal[i] = tempRows.get(i).toArray(blankArray);
        }

        request.getSession().setAttribute("chosenOnlyActivitiesSessionFormatGlobal", chosenOnlyActivitiesSessionFormatGlobal);
        return "redirect:/customizeAgendaWorkingPhase";
    }

    @GetMapping(value = "/customizeAgendaWorkingPhase")
    public String customizeAgendaWorkingPhase(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));

        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene"));
        model.addAttribute("biovoicesSessionFormatGlobalSettingScene", request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene"));


        model.addAttribute("chosenOnlyActivitiesSessionFormatGlobal", request.getSession().getAttribute("chosenOnlyActivitiesSessionFormatGlobal"));
        model.addAttribute("chosenOnlyActivitiesWorkingPhaseGlobal", request.getSession().getAttribute("chosenOnlyActivitiesWorkingPhaseGlobal"));

        FactSheet factSheet = factSheetService.findByName(request.getSession().getAttribute("mmlNameGlobal").toString());
        Agenda agenda = agendaService.findByFactSheet(factSheet);

        model.addAttribute("agenda", agenda);

        List<Result> resultsB = new ArrayList<>();
        List<String> yAxis = matrixBService.findDistinctByActivity();
        List<String> list = new ArrayList<>();
        list.add(request.getSession().getAttribute("groupCompositionGlobal").toString());
        list.add(request.getSession().getAttribute("groupSizeGlobal").toString());
        list.add(request.getSession().getAttribute("biovoicesGoalGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene").toString());

        for(String y : yAxis) {
            String[] split = y.split(",");
            List<MatrixB> matrixBList = matrixBService.findByActivityAndValueIn(split[0], list);
            Result result = new Result();
            result.setyAxis(split[0]);
            result.setFormatPhase(split[1]);
            //result.setColor(split[1]);
            Double mediumScore = 0.0;
            Map<String, Double> map = new HashMap<>();
            for (MatrixB b : matrixBList) {
                map.put(b.getType(), b.getScore());
                mediumScore += b.getScore();
            }
            Double mediumScoreFinal = mediumScore / matrixBList.size();
            /* METODO PER TRUNCATE A 2 CIFRE DECIMALI */
            double mediumScoreTruncate = Math.round(mediumScoreFinal * 100.0) / 100.0;;
            result.setMediumScore(mediumScoreTruncate);
            result.setFactorScoresMap(map);
            // Inizio parte set colore in base al punteggio
            if (mediumScoreFinal<2){
                result.setColor("red");
            }
            else if (mediumScoreFinal<3){
                result.setColor("yellow");

            }
            else{
                result.setColor("green");

            }
            Activity activityInfo = activityService.findByName(split[0]);
            result.setEstimatedPlanningTime(activityInfo.getTimeEstimated());
            resultsB.add(result);
            // Fine parte set colore in base al punteggio
        }

        //Inizio creazione e ordinamento liste attività in base alla fase

        List<Result> scene = new ArrayList<Result>();
        List<Result> working = new ArrayList<Result>();
        List<Result> wrap = new ArrayList<Result>();

        for (Result r : resultsB){
            if (r.getFormatPhase().equals("Working Phase")){
                working.add(r);
            }
        }


        working.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(working);
        //Fine creazione e ordinamento liste attività in base alla fase


        model.addAttribute("workingResultsB", working);
        model.addAttribute("activityFactSheet", null);

        return "customizeAgendaWorkingPhase";
    }

    @RequestMapping(value = "/agendaCustomizationWorkingPhaseEnd", method = RequestMethod.POST)
    @ResponseBody
    public String agendaCustomizationWorkingPhaseEnd(@RequestParam("rows") String[][] rows, HttpServletRequest request) {
        request.getSession().setAttribute("chosenActivitiesWorkingPhaseGlobal", rows);
        List<List<String>> tempRows = new ArrayList<>();
        for(int i = 1; i < rows.length; i++) {
            if(rows[i].length == 4 && !rows[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(rows[i][0].replace("*$", ","));
                temp.add(rows[i][1].replace("*$", ","));
                temp.add(rows[i][2].replace("*$", ","));
                temp.add(rows[i][3].replace("*$", ","));
                tempRows.add(temp);
            }
            if(rows[i].length == 2 && !rows[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(rows[i][0].replace("*$", ","));
                temp.add(rows[i][1].replace("*$", ","));
                tempRows.add(temp);
            }
            if(rows[i][0].contains("Wrap-up")) {
                break;
            }
        }

        String[][] chosenOnlyActivitiesWorkingPhaseGlobal = new String[tempRows.size()][];
        String[] blankArray = new String[0];
        for(int i=0; i < tempRows.size(); i++) {
            chosenOnlyActivitiesWorkingPhaseGlobal[i] = tempRows.get(i).toArray(blankArray);
        }
        request.getSession().setAttribute("chosenOnlyActivitiesWorkingPhaseGlobal", chosenOnlyActivitiesWorkingPhaseGlobal);

        return "redirect:/customizeAgendaWrapUp";
    }

    @GetMapping(value = "/customizeAgendaWrapUp")
    public String customizeAgendaWrapUp(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));

        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene"));
        model.addAttribute("biovoicesSessionFormatGlobalSettingScene", request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene"));

        model.addAttribute("chosenOnlyActivitiesSessionFormatGlobal", request.getSession().getAttribute("chosenOnlyActivitiesSessionFormatGlobal"));
        model.addAttribute("chosenOnlyActivitiesWorkingPhaseGlobal", request.getSession().getAttribute("chosenOnlyActivitiesWorkingPhaseGlobal"));
        model.addAttribute("chosenOnlyActivitiesWrapUpGlobal", request.getSession().getAttribute("chosenOnlyActivitiesWrapUpGlobal"));

        FactSheet factSheet = factSheetService.findByName(request.getSession().getAttribute("mmlNameGlobal").toString());
        Agenda agenda = agendaService.findByFactSheet(factSheet);

        model.addAttribute("agenda", agenda);

        List<Result> resultsB = new ArrayList<>();
        List<String> yAxis = matrixBService.findDistinctByActivity();
        List<String> list = new ArrayList<>();
        list.add(request.getSession().getAttribute("groupCompositionGlobal").toString());
        list.add(request.getSession().getAttribute("groupSizeGlobal").toString());
        list.add(request.getSession().getAttribute("biovoicesGoalGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene").toString());
        list.add(request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene").toString());

        for(String y : yAxis) {
            String[] split = y.split(",");
            List<MatrixB> matrixBList = matrixBService.findByActivityAndValueIn(split[0], list);
            Result result = new Result();
            result.setyAxis(split[0]);
            result.setFormatPhase(split[1]);
            //result.setColor(split[1]);
            Double mediumScore = 0.0;
            Map<String, Double> map = new HashMap<>();
            for (MatrixB b : matrixBList) {
                map.put(b.getType(), b.getScore());
                mediumScore += b.getScore();
            }
            Double mediumScoreFinal = mediumScore / matrixBList.size();
            /* METODO PER TRUNCATE A 2 CIFRE DECIMALI */
            double mediumScoreTruncate = Math.round(mediumScoreFinal * 100.0) / 100.0;;
            result.setMediumScore(mediumScoreTruncate);
            result.setFactorScoresMap(map);
            // Inizio parte set colore in base al punteggio
            if (mediumScoreFinal<2){
                result.setColor("red");
            }
            else if (mediumScoreFinal<3){
                result.setColor("yellow");

            }
            else{
                result.setColor("green");

            }
            Activity activityInfo = activityService.findByName(split[0]);
            result.setEstimatedPlanningTime(activityInfo.getTimeEstimated());
            resultsB.add(result);
            // Fine parte set colore in base al punteggio
        }

        //Inizio creazione e ordinamento liste attività in base alla fase

        List<Result> scene = new ArrayList<Result>();
        List<Result> working = new ArrayList<Result>();
        List<Result> wrap = new ArrayList<Result>();

        for (Result r : resultsB){
            if (r.getFormatPhase().equals("Wrap-Up")){
                wrap.add(r);
            }
        }


        wrap.sort(Comparator.comparing(a -> a.getMediumScore()));
        Collections.reverse(wrap);
        //Fine creazione e ordinamento liste attività in base alla fase


        model.addAttribute("wrapResultsB", wrap);
        model.addAttribute("activityFactSheet", null);

        return "customizeAgendaWrapUp";
    }

    @RequestMapping(value = "/agendaCustomizationWrapUpEnd", method = RequestMethod.POST)
    @ResponseBody
    public String agendaCustomizationWrapUpEnd(@RequestParam("rows") String[][] rows, @RequestParam("newActivitiesOnly") String[][] newActivitiesOnly, HttpServletRequest request) {

        request.getSession().setAttribute("chosenActivitiesGlobal", rows);
        List<List<String>> tempRows = new ArrayList<>();
        for(int i = 0; i < newActivitiesOnly.length; i++) {
            if(newActivitiesOnly[i].length == 4 && !newActivitiesOnly[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(newActivitiesOnly[i][0].replace("*$", ","));
                temp.add(newActivitiesOnly[i][1].replace("*$", ","));
                temp.add(newActivitiesOnly[i][2].replace("*$", ","));
                temp.add(newActivitiesOnly[i][3].replace("*$", ","));
                tempRows.add(temp);
            }
            if(newActivitiesOnly[i].length == 2 && !newActivitiesOnly[i][1].contains("Drag an activity")) {
                List<String> temp = new ArrayList<>();
                temp.add(newActivitiesOnly[i][0].replace("*$", ","));
                temp.add(newActivitiesOnly[i][1].replace("*$", ","));
                tempRows.add(temp);
            }
        }

        String[][] chosenOnlyActivitiesWrapUpGlobal = new String[tempRows.size()][];
        String[] blankArray = new String[0];
        for(int i=0; i < tempRows.size(); i++) {
            chosenOnlyActivitiesWrapUpGlobal[i] = tempRows.get(i).toArray(blankArray);
        }

        request.getSession().setAttribute("chosenOnlyActivitiesWrapUpGlobal", chosenOnlyActivitiesWrapUpGlobal);

        return "redirect:/downloadSupportingDocuments";
    }


    @GetMapping(value = "/downloadSupportingDocuments")
    public String downloadSupportingDocuments(Model model, HttpServletRequest request) {

        model.addAttribute("biovoicesChallengeCluster", request.getSession().getAttribute("biovoicesChallengeClusterGlobal"));
        model.addAttribute("groupCompositionViewGlobal", request.getSession().getAttribute("groupCompositionViewGlobal"));
        model.addAttribute("groupSizeGlobal", request.getSession().getAttribute("groupSizeGlobal"));
        model.addAttribute("mmlNameGlobal", request.getSession().getAttribute("mmlNameGlobal"));

        model.addAttribute("biovoicesGoalGlobalSettingScene", request.getSession().getAttribute("biovoicesGoalGlobalSettingScene"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalSettingScene"));
        model.addAttribute("biovoicesSessionFormatGlobalSettingScene", request.getSession().getAttribute("biovoicesSessionFormatGlobalSettingScene"));

        model.addAttribute("biovoicesGoalGlobalWorkingPhase", request.getSession().getAttribute("biovoicesGoalGlobalWorkingPhase"));
        model.addAttribute("biovoicesParticipantExperienceLevelGlobalWorkingPhase", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalWorkingPhase"));
        model.addAttribute("biovoicesSessionFormatGlobalWorkingPhase", request.getSession().getAttribute("biovoicesSessionFormatGlobalWorkingPhase"));

        model.addAttribute("biovoicesGoalGlobalWrapUp", request.getSession().getAttribute("biovoicesGoalGlobalWrapUp"));
        model.addAttribute("biovoicesParticipantExperienceLevelWrapUp", request.getSession().getAttribute("biovoicesParticipantExperienceLevelGlobalWrapUp"));
        model.addAttribute("biovoicesSessionFormatGlobalWrapUp", request.getSession().getAttribute("biovoicesSessionFormatGlobalWrapUp"));
        model.addAttribute("chosenActivitiesGlobal", request.getSession().getAttribute("chosenActivitiesGlobal"));

        List<String> chosenActivities = new ArrayList<>();
        String[][] chosenActivitiesGlobal = null;

        if(request.getSession().getAttribute("chosenActivitiesGlobal") instanceof ArrayList) {
            chosenActivities = (ArrayList) request.getSession().getAttribute("chosenActivitiesGlobal");
        }
        else {
            chosenActivitiesGlobal = (String[][]) request.getSession().getAttribute("chosenActivitiesGlobal");
            for(String[] row : chosenActivitiesGlobal) {
                if(row.length > 2) {
                    if(!row[3].equals("Drag an activity") && !row[3].equals("Suitable activities") && !row[3].isEmpty()) {
                        String activity = row[3];
                        chosenActivities.add(activity);
                    }
                }
                if(row.length == 2) {
                    if(!row[0].equals("Lunch break") && !row[0].equals("Coffee break")) {
                        if (!row[1].equals("Drag an activity") && !row[1].equals("Suitable activities") && !row[1].isEmpty()) {
                            String activity = row[1];
                            chosenActivities.add(activity);
                        }
                    }
                }
            }
        }


        request.getSession().setAttribute("totalAgenda", chosenActivitiesGlobal);
        request.getSession().setAttribute("chosenActivitiesGlobal", chosenActivities);
        model.addAttribute("chosenActivities", request.getSession().getAttribute("chosenActivitiesGlobal"));
        model.addAttribute("factSheet", null);
        model.addAttribute("activityFactSheet", null);

        return "downloadSupportingDocuments";
    }

    @GetMapping(value = "/downloadAgenda")
    public void viewFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String[][] totalAgenda = (String[][]) request.getSession().getAttribute("totalAgenda");

        String docFile = AgendaCreation.createAgenda(totalAgenda);
        String pathToDownloadFile = docFile;
        File file = new File(pathToDownloadFile);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());

        InputStream inputStream = new FileInputStream(file);;
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}




