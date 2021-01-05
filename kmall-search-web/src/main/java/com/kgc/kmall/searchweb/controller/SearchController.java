package com.kgc.kmall.searchweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author shkstart
 * @create 2021-01-04 16:35
 */
@Controller
public class SearchController {
    @RequestMapping("/index.html")
    public String index(){        return "index";}

    @RequestMapping("/list.html")
    public String list(String keyword){
        return "list";
    }

}
