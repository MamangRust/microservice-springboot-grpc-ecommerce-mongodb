package com.sanedge.ecommerce.slider.models;

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
@Document(collection = "sliders")
public class Slider extends BaseModel {
    @Id
    @Field("slider_id")
    private String sliderId;

    @Field("name")
    private String name;

    @Field("image")
    private String image;
}