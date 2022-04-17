package ru.hh.school.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryDto {
    @JsonProperty
    public Integer from;

    @JsonProperty
    public Integer to;

    @JsonProperty
    public String currency;

    @JsonProperty
    public boolean gross;

    public SalaryDto(){}

    public SalaryDto(Integer from, Integer to, String currency, boolean gross){
        this.from = from;
        this.to = to;
        this.currency =currency;
        this.gross = gross;
    }


}
