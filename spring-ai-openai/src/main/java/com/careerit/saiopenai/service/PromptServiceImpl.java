package com.careerit.saiopenai.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PromptServiceImpl {



    Map<String,Object> prompts = new HashMap<>();
    Map<String,Object> metaData = new HashMap<>();


    public PromptServiceImpl(){
        try {
            Yaml yaml = new Yaml();
            prompts = yaml.loadAs(PromptServiceImpl.class.getResourceAsStream("/java_prompt.yaml"), Map.class);
        }catch (Exception e){
            log.info("While loading prompts!", e);
            log.error(e.getMessage());
        }
    }

    public String getPromptWithReplaceMetaData(String key){
            if(prompts.containsKey(key)){
                String promptData =  prompts.get(key).toString();
                Map<String,Object> metaData= (Map<String,Object>)prompts.get("metaData");
                StringSubstitutor sub = new StringSubstitutor(metaData);
                log.debug("Before replace tokens :{}",promptData);
                promptData = sub.replace(promptData);
                log.debug("After replace tokens :{}",promptData);
                return promptData;

            }
            throw new IllegalArgumentException("Prompt :"+key+" not found");
    }

}
