package com.example.driveproject.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleService {

    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private String CALLBACK_URI;
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private GoogleAuthorizationCodeFlow flow;

    @Value("${server.host}")
    private String HOST;

    @Value("${server.port}")
    private int PORT;

    private String CALLBACK_PATH = "/oauth";

    @PostConstruct
    public void init() throws IOException {
        CALLBACK_URI = "http://" + HOST + ":" + PORT + CALLBACK_PATH;
        System.out.println(CALLBACK_URI);
        InputStream in = GoogleService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();
    }

    public Drive getServerDrive(String serverName) throws IOException, GeneralSecurityException {
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY,
                getServerCredentials(serverName))
                .build();
    }

    private Credential getServerCredentials(String serverName)
            throws IOException {
        InputStream in = GoogleService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setHost(HOST)
                .setPort(PORT)
                .setCallbackPath(CALLBACK_PATH)
                .build();
        AuthorizationCodeInstalledApp a = new AuthorizationCodeInstalledApp(flow, receiver);
        return a.authorize(serverName);
    }

    public Drive getDriveByUsername(String username) throws IOException {
        Credential credential = flow.loadCredential(username);
        if (credential != null) {
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
        }
        return null;
    }

    public String getRedirectUrl() {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri(CALLBACK_URI)
                .setAccessType("offline")
                .build();
    }

    public void saveCode(String code, String username) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(CALLBACK_URI)
                .execute();
        flow.createAndStoreCredential(response, username);
    }

}