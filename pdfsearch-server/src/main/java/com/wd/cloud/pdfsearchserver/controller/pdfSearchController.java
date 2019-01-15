package com.wd.cloud.pdfsearchserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.service.pdfSearchServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class pdfSearchController {
    @Autowired
    private pdfSearchServiceI pdfSearchService;

    @RequestMapping(value = "/searchpdf",method = RequestMethod.POST)
    public ResponseModel<byte[]> getpdf(HttpServletRequest request, HttpServletResponse response){
        String title = request.getParameter("title");
        Map<String,Object> map =new HashMap<>();
        map.put("title",title);
        if(request.getParameter("journal")!=null){
            map.put("journal",request.getParameter("journal"));
        }
        if(request.getParameter("year")!=null){
            map.put("year",request.getParameter("year"));
        }
        if(request.getParameter("volume")!=null){
            map.put("volume",request.getParameter("volume"));
        }
        if(request.getParameter("issue")!=null){
            map.put("issue",request.getParameter("issue"));
        }
        if(request.getParameter("doi")!=null){
            map.put("doi",request.getParameter("doi"));
        }
        if(request.getParameter("issn")!=null){
            map.put("issn",request.getParameter("issn"));
        }
        return pdfSearchService.getpdf(map);
    }
}
