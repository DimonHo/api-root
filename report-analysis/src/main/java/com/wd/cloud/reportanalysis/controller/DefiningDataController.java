package com.wd.cloud.reportanalysis.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefiningDataController {

    @RequestMapping("/getdefiningdata")
    public String tesg(@RequestParam String act, @RequestParam String table, @RequestParam int scid, @RequestParam String compare_scids, @RequestParam String time, @RequestParam String source, @RequestParam int signature, @RequestParam String category_type, Model model) {
        model.addAttribute("act", act);
        model.addAttribute("table", table);
        model.addAttribute("scid", scid);
        model.addAttribute("compare_scids", compare_scids);
        model.addAttribute("time", time);
        model.addAttribute("source", source);
        model.addAttribute("signature", signature);
        model.addAttribute("category_type", category_type);
        if (act.equalsIgnoreCase("amount") && table.equalsIgnoreCase("amount")) {
            return "forward:/hut";
        }
        if (act.equalsIgnoreCase("partition") && table.equalsIgnoreCase("jcr")) {
            return "forward:/fenqu";
        }
        if (act.equalsIgnoreCase("cited") && table.equalsIgnoreCase("total_cited")) {
            return "forward:/frequency";
        }
        if (act.equalsIgnoreCase("esi") && table.equalsIgnoreCase("amount")) {
            model.addAttribute("category_type", category_type);
            return "forward:/esi";
        }

        return null;
    }


}
