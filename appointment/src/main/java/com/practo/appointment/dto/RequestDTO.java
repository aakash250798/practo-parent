package com.practo.appointment.dto;

import lombok.Getter;

@Getter
public class RequestDTO {
    String sort;
    String direction;
    String query;
    Integer size;
    Integer page;

}
