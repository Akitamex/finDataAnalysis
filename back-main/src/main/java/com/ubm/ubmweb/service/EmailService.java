package com.ubm.ubmweb.service;

import org.springframework.stereotype.Service;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.exception.PostmarkException;

import lombok.RequiredArgsConstructor;

import com.postmarkapp.postmark.client.data.model.message.Message;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {


    @Value("${spring.mail.apiToken}")
    private String apiToken;

    public void sendEmail(String to, String subject, String text) throws IOException, PostmarkException {
        ApiClient client = Postmark.getApiClient(apiToken);
        Message message = new Message("admin@udirect.kz", to, subject, text);
        message.setMessageStream("outbound");
        MessageResponse response = client.deliverMessage(message);
    }
}
