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
    @Pattern(regexp = "^[a-zA-Z0-9]{3,}\\.[a-zA-Z0-9]{3,}\\.[a-zA-Z0-9]{3,}$",
            message = "Alias must follow the pattern: word.word.word (e.g., BeanRx.JavRex.SrvCore)")
    @Schema(description = "Account alias in format word.word.word (auto-generated)", example = "BeanRx.JavRex.SrvCore")
    private String alias;
}
