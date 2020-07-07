package com.fintech.soapclient.controller;

import com.fintech.soapclient.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value = "/")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping
    public ModelAndView index() {
        ModelAndView view = new ModelAndView();
        view.addObject("tasks", taskService.getAllTasks());
        view.setViewName("index");
        return view;
    }
}
