package com.nhsbsa.productpricealerttracker.notificationservice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.exception.InvalidDataException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

  @Value("${api.token}")
  private String sendGridApiKey;

  @Value("${notification.from.email}")
  private String fromEmail;

  /**
   * @param request
   * @param message
   */
  @Override
  public void sendNotification(UserPriceTracker request, String message) {
    try {
      String name = request.getUserName();
      String product = request.getProductUrl();
      double price = request.getDroppedPrice();
      String toEmail = request.getEmailId();
      log.info("sendGridApiKey" + sendGridApiKey);
      String jsonPayload = """
          {
            "from": {
              "email": "%s",
              "name": "Price Tracker Bot"
            },
            "to": [{
              "email": "%s",
              "name": "%s"
            }],
            "subject": "📉 Price Drop Alert!",
            "text": "Hello {{name}},\\nThe price for {{product}} has dropped to {{price}}!",
            "variables": [{
              "email": "%s",
              "substitutions": [
                { "var": "name", "value": "%s" },
                { "var": "product", "value": "%s" },
                { "var": "price", "value": "%s" }
              ]
            }]
          }
          """.formatted(
          fromEmail, toEmail, name,
          toEmail, name, product, price);

      HttpRequest httprequest = HttpRequest.newBuilder()
          .uri(URI.create("https://api.mailersend.com/v1/email"))
          .header("Authorization", "Bearer " + sendGridApiKey.trim())
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(httprequest, HttpResponse.BodyHandlers.ofString());

      log.info("Status Code: " + response.statusCode());
      log.info("Response Body: " + response.body());
      if (response.statusCode() != 200 && response.statusCode() != 202) {
        log.info("Error");
        throw new InvalidDataException(response.body(), "104");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to send MailerSend email", e);
    }
  }
}
