package com.example.proyectogrupo4_gtics.Service;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.cx.v3beta1.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class Dialogflow {
    @Value("${dialogflow.project.id}")
    private String projectId;
    @Value("${dialogflow.agent.id}")
    private String agentId;
    @Value("${dialogflow.location.id}")
    private String locationId;
    @Value("${dialogflow.language.code}")
    private String languageCode;
     final ResourceLoader resourceLoader;
    public Dialogflow(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    public String detectIntent(String text, String sessionId) throws IOException {
            Resource resource = resourceLoader.getResource("classpath:bot-nuevo-440622-d782e4fb7e98.json");
            GoogleCredentials credentials;
            try (InputStream credentialsStream = resource.getInputStream()) {
                credentials = GoogleCredentials.fromStream(credentialsStream)
                        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));}
            SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
                SessionName session = SessionName.of(projectId, locationId, agentId, sessionId);
                // Construir la consulta de texto
                TextInput.Builder textInput = TextInput.newBuilder().setText(text);
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).setLanguageCode(languageCode).build();
                // Detectar intent
                DetectIntentRequest request = DetectIntentRequest.newBuilder()
                        .setSession(session.toString())
                        .setQueryInput(queryInput)
                        .build();
                DetectIntentResponse response = sessionsClient.detectIntent(request);
                QueryResult queryResult = response.getQueryResult();
                String fulfillmentText = queryResult.getResponseMessages(0).getText().getText(0);
                return fulfillmentText;
            }
    }
}
