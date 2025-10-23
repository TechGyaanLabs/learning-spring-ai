package com.careerit.saiopenai.controller;

import com.careerit.saiopenai.service.EmailHelperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailHelperController {

    @Autowired
    private EmailHelperServiceImpl  emailHelperService;


    @GetMapping("/body-content")
    public String emailBody(@RequestParam(defaultValue = "John")String name,
                            @RequestParam(defaultValue = "I lost my password, how do i reset my password")String message)
    {
        return emailHelperService.getEmailBody(name, message);

    }
}
