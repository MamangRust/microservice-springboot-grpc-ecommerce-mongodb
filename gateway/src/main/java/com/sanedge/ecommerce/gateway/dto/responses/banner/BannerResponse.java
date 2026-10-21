package com.sanedge.ecommerce.gateway.dto.responses.banner;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
    public class BannerResponse {
    private Long id;
    private String name;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

}