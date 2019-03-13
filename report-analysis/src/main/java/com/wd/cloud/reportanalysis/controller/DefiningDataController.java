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
        if ("amount".equalsIgnoreCase(act) && "amount".equalsIgnoreCase(table)) {
            return "forward:/hut";
        }
        if ("partition".equalsIgnoreCase(act) && "jcr".equalsIgnoreCase(table)) {
            return "forward:/fenqu";
        }
        if ("cited".equalsIgnoreCase(act) && "total_cited".equalsIgnoreCase(table)) {
            return "forward:/frequency";
        }
        if ("esi".equalsIgnoreCase(act) && "amount".equalsIgnoreCase(table)) {
            model.addAttribute("category_type", category_type);
            return "forward:/esi";
        }

        return null;
    }


}
