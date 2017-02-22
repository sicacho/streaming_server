package com.streaming.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/11/2017.
 */
public class GoogleDriveService {

  private final java.io.File DATA_STORE_DIR = new java.io.File(
      System.getProperty("user.home"), ".credentials/copydrive");

  /**
   * Global instance of the {@link FileDataStoreFactory}.
   */
  private FileDataStoreFactory DATA_STORE_FACTORY = createDataStoreFactory();

  private FileDataStoreFactory createDataStoreFactory() {
    try {
      return new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Global instance of the JSON factory.
   */
  private final JsonFactory JSON_FACTORY =
      JacksonFactory.getDefaultInstance();

  /**
   * Global instance of the HTTP transport.
   */
  private  HttpTransport HTTP_TRANSPORT = createNetHttpTransport();

  private NetHttpTransport createNetHttpTransport()  {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  ;

  /**
   * Global instance of the scopes required by this quickstart.
   * <p>
   * If modifying these scopes, delete your previously saved credentials
   * at ~/.credentials/drive-java-quickstart
   */
  private  final List<String> FullScope = new ArrayList<>();
  /**
   * Creates an authorized Credential object.
   *
   * @return an authorized Credential object.
   * @throws IOException
   */
  public  Credential authorize() throws IOException, GeneralSecurityException {
    InputStream in =
            GoogleDriveService.class.getResourceAsStream("/client_secret_2.json");
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    FullScope.add(DriveScopes.DRIVE);
    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, FullScope)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline").setApprovalPrompt("auto")
            .build();
    Credential credential = new AuthorizationCodeInstalledApp(
        flow, new LocalServerReceiver()).authorize("432161060989");
    System.out.println(
        "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }


}
