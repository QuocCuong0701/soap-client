package com.fintech.soapclient.service;

import com.fintech.soapclient.bindings.GetTasksByIdRequest;
import com.fintech.soapclient.bindings.GetTasksByIdResponse;
import com.fintech.soapclient.bindings.TaskSoap;
import com.fintech.soapclient.config.SoapConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {
    private static final String BASE_URL = "http://localhost:1998/ws";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Autowired
    SoapConnector soapConnector;

    //@Scheduled(cron = "0/10 * * ? * *")
    public List<TaskSoap> getAllTasks() {
        GetTasksByIdRequest getTasksByIdRequest = new GetTasksByIdRequest();
        getTasksByIdRequest.setPersonId(1);
        LOGGER.info("It's {}", SIMPLE_DATE_FORMAT.format(new Date()));
        return ((GetTasksByIdResponse) soapConnector.callWebService(getTasksByIdRequest)).getTask();
    }
}
