package com.dmh.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAccountRequestDto {

    @NotBlank(message = "Alias is required")
    @Pattern(
            regexp = "^[a-zA-Z]{2,}\\.[a-zA-Z]{2,}\\.[a-zA-Z]{2,}$",
            message = "Alias must contain only letters and follow pattern: word.word.word")
    @Schema(description = "Account alias in format word.word.word (auto-generated)", example = "BeanRx.JavRex.SrvCore")
    private String alias;
}
