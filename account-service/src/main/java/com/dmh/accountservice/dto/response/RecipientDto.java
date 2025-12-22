package com.dmh.accountservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecipientDto {
    private String fullName;
    private String cvu;
    private String alias;
}