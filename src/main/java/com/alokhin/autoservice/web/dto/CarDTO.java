package com.alokhin.autoservice.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class CarDTO {

    private Integer id;
    private String name;
    private Integer year;
    private Integer price;
    private Boolean enabled;
    private String login;
    private String description;
    private String imageUrl;

    @Tolerate
    public CarDTO() {
    }
}
