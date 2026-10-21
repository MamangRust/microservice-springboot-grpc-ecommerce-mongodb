package com.sanedge.ecommerce.gateway.dto.responses.reviewdetail;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResponseDeleteAt {
    private int id;
    private int reviewId;
    private String type;
    private String url;
    private String caption;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

}
