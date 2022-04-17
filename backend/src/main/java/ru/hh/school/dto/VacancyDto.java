package ru.hh.school.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.hh.school.config.ProdConfig;
import ru.hh.school.entity.Area;

import java.util.Date;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)

public class VacancyDto {
    @JsonProperty
    public Integer id;

    @JsonProperty
    public String created_at;

    @JsonProperty
    public String name;

    @JsonProperty
    public Area area;

    @JsonProperty
    public String comment;

    @JsonProperty
    public Integer views_count;

    @JsonProperty
    public Date date_create;

    @JsonProperty
    public Popularity popularity;

    @JsonProperty
    public EmployerDto employer;

    @JsonProperty
    public SalaryDto salary;


    public VacancyDto(){}

    public VacancyDto(Integer id, String name,
                      String created_at,
                      Area area,
                      String comment,
                      Integer views_count,
                      Date date_create,
                      SalaryDto salary,
                      EmployerDto employer,
                      Popularity popularity) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.area = area;
        this.comment = comment;
        this.views_count = views_count;
        this.date_create = date_create;
        this.employer = employer;
        this.salary = salary;
//        this.popularity = counter > 50 ? Popularity.POPULAR : Popularity.REGULAR;
        this.popularity = views_count > ProdConfig.greatPopularityValue ? Popularity.POPULAR : Popularity.REGULAR;
    }
    public VacancyDto(Integer id, String name,
                      String created_at,
                      Area area,
                      String comment,
                      Integer views_count,
                      Date date_create,
                      SalaryDto salary,
                      EmployerDto employer) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.area = area;
        this.comment = comment;
        this.views_count = views_count;
        this.date_create = date_create;
        this.employer = employer;
        this.salary = salary;

//        this.popularity = counter > 50 ? Popularity.POPULAR : Popularity.REGULAR;
        this.popularity = views_count > ProdConfig.greatPopularityValue ? Popularity.POPULAR : Popularity.REGULAR;

    }

    public String toString() {
        return String.format("\n!!! name: %s \n created_at: %s \n id: %s \n area: %s \n comment: %s \n counter: %s_%s_%s \n",
                name, created_at, id, area.toString(), comment, views_count, popularity, date_create);
    }
}