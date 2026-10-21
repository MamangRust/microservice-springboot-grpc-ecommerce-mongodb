package com.sanedge.ecommerce.banner.models;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.ecommerce.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "banners")
public class Banner extends BaseModel {

    @Id
    @Field("banner_id")
    private String bannerId;

    @Field("name")
    private String name;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    @Field("start_time")
    private LocalTime startTime;

    @Field("end_time")
    private LocalTime endTime;

    @Field("is_active")
    private Boolean isActive;
}
