package com.nhsbsa.productpricealerttracker.model;

import com.nhsbsa.productpricealerttracker.enums.Frequency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO to track product price for a user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceTrackingRequest {

    @Schema(description = "Unique username for the new user", example = "sindhu123")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username contains invalid characters")
    private String userName;

    @Schema(description = "Product URL to be tracked", example = "https://example.com/product4")
    @NotBlank(message = "Product URL must not be blank")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid product URL format")
    private String productUrl;

    @Schema(description = "Desired price to trigger an alert", example = "89.99")
    @Positive(message = "Desired price must be a positive number")
    private double desiredPrice;

    @Schema(description = "Alert track Frequency", example = "MORNING_ONLY", allowableValues = { "MORNING_ONLY",
            "EVERY_24_HOURS", "MIDNIGHT_ONLY" })
    @NotNull(message = "Frequency is required")
    private Frequency checkFrequency;

    @Schema(description = "User's email address", example = "sindhu123@example.com")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String emailId;

}
