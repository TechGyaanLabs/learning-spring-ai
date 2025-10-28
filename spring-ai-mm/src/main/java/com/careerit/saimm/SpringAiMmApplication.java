package com.careerit.saimm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class SpringAiMmApplication implements CommandLineRunner {



    private  final ChatClient openAiChatClient;
    private  final ChatClient ollamaChatClient;

    public SpringAiMmApplication(@Qualifier("openAiChatClient") ChatClient openAiChatClient,@Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
       this.openAiChatClient = openAiChatClient;
       this.ollamaChatClient = ollamaChatClient;
   }

	public static void main(String[] args) {
		SpringApplication.run(SpringAiMmApplication.class, args);
	}


    @Override
    public void run(String... args) throws Exception {

        System.out.println("Open Ai Chat Client "+openAiChatClient.prompt().user("who are you ?").call().content());
        System.out.println("Ollam ai chat client "+ollamaChatClient.prompt().user("who are you ?").call().content());

    }
}
