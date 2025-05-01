# Product Price Tracker

This is a Spring Boot application developed in Java that allows users to track product prices and receive email notifications when the price drops to their desired value. The application exposes RESTful APIs for creating price tracking records and monitoring price changes.

## ✨ Key Features

1. **Product Validation:** When a tracking request is created, the product URL is validated against a predefined static JSON file. If the product does not exist, an error is returned.
2. **Immediate Price Check:** During creation, the application checks whether the current price is less than or equal to the desired price. If so, a notification is sent immediately.
3. **Scheduled Price Checks:** A scheduler runs at three intervals—morning, midnight, and every 24 hours. It simulates price drops with random values and triggers email alerts if the desired price condition is met. Only unsent alerts are considered.
4. **Pluggable Notification Service:** The notification service is built using an interface to allow future support for other channels like SMS or push notifications.

---

## 🚀 How to Run the Application

1. **Clone the Repository:**  
   Clone the project from GitHub.

2. **Install Prerequisites:**  
   Ensure Java 21 and Maven are installed and configured in your environment.

3. **Navigate to Project Directory:**  
   Open a terminal and change the directory to the cloned project location.

4. **Run the Application:**  
   Use the following command to start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the Application:**  
   Once started, the server will run on port `8080`.

6. **Swagger UI:**  
   API documentation is available at:
   ```
   http://localhost:8080/swagger-ui/
   ```

---

## 🛠️ Technical Details

- **Technology Stack:** Java 21, Spring Boot 3.2.5
- **Data Source:** Static JSON file loaded once at startup using `@PostConstruct`
- **API Documentation:** OpenAPI (Swagger) for all REST endpoints
- **Database:** In-memory H2 database for simplicity and ease of testing
- **Security:** Basic Authentication (Note: suitable only for development/demo environments)
- **Project Repository:** Available on GitHub (add your repository link here)

---

## ✅ Testing

- **JUnit Tests:** Includes both integration tests and unit tests for key service methods to ensure reliability and correctness.

- productpricealerttracker
  - src
    - main
      - java
        - com
          - nhsbsa
            - productpricealerttracker
              - config
                - LoadJsonConfig.java
                - OpenApiConfig.java
                - SecurityConfig.java
              - controller
                - PriceTrackingController.java
              - converter
                - PriceTrackingConverter.java
              - entity
                - UserPriceTracker.java
              - enums
                - Frequency.java
              - exception
                - GlobalExceptionHandler.java
                - InvalidDataException.java
              - model
                - CustomErrorResponse.java
                - PriceTrackingRequest.java
                - PriceTrackingResponse.java
                - ProductPrice.java
              - notificationservice
                - EmailNotificationService.java
                - NotificationService.java
              - ProductPriceAlertTrackerApplication.java
              - repository
                - PriceTrackingRepository.java
              - scheduler
                - PriceCheckScheduler.java
              - service
                - PriceTrackingService.java
              - util
                - PriceAlertUtil.java
      - resources
        - application.properties
        - product-prices.json
    - test
      - java
        - com
          - nhsbsa
            - productpricealerttracker
              - PriceTrackingControllerTest.java
              - PriceTrackingServiceTest.java
              - ProductpricealerttrackerApplicationTests.java
      - resources
        - application-test.properties
        - product-prices-test.json
  - target
    - classes
      - application.properties
      - com
        - nhsbsa
          - productpricealerttracker
            - config
              - LoadJsonConfig$1.class
              - LoadJsonConfig.class
              - OpenApiConfig.class
              - SecurityConfig.class
            - controller
              - PriceTrackingController.class
            - converter
              - PriceTrackingConverter.class
            - entity
              - UserPriceTracker$UserPriceTrackerBuilder.class
              - UserPriceTracker.class
            - enums
              - Frequency.class
            - exception
              - GlobalExceptionHandler.class
              - InvalidDataException.class
            - model
              - CustomErrorResponse.class
              - PriceTrackingRequest$PriceTrackingRequestBuilder.class
              - PriceTrackingRequest.class
              - PriceTrackingResponse.class
              - ProductPrice.class
            - notificationservice
              - EmailNotificationService.class
              - NotificationService.class
            - ProductPriceAlertTrackerApplication.class
            - repository
              - PriceTrackingRepository.class
            - scheduler
              - PriceCheckScheduler.class
            - service
              - PriceTrackingService.class
            - util
              - PriceAlertUtil.class
      - product-prices.json
    - generated-sources
      - annotations
    - generated-test-sources
      - test-annotations
    - test-classes
      - application-test.properties
      - com
        - nhsbsa
          - productpricealerttracker
            - PriceTrackingControllerTest.class
            - PriceTrackingServiceTest.class
            - ProductpricealerttrackerApplicationTests.class
      - product-prices-test.json
