package com.sanedge.ecommerce.gateway.dto.responses.reviews;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String name;
    private String comment;
    private Integer rating;
    private String createdAt;
    private String updatedAt;

}
