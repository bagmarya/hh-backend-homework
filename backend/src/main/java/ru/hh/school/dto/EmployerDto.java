package ru.hh.school.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.hh.school.config.ProdConfig;
import ru.hh.school.entity.Area;

import java.util.Date;

@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployerDto {
    @JsonProperty
    public Integer id;

    @JsonProperty
    public String description;

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


    public EmployerDto(){}

    public EmployerDto(Integer id, String name,
                       String description,
                       Area area,
                       String comment,
                       Integer counter,
                       Date date_create,
                       Popularity popularity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
        this.comment = comment;
        this.views_count = counter;
        this.date_create = date_create;
//        this.popularity = counter > 50 ? Popularity.POPULAR : Popularity.REGULAR;
        this.popularity = counter > ProdConfig.greatPopularityValue ? Popularity.POPULAR : Popularity.REGULAR;
    }
    public EmployerDto(Integer id, String name,
                       String description,
                       Area area,
                       String comment,
                       Integer counter,
                       Date date_create) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
        this.comment = comment;
        this.views_count = counter;
        this.date_create = date_create;
//        this.popularity = counter > 50 ? Popularity.POPULAR : Popularity.REGULAR;
        this.popularity = counter > ProdConfig.greatPopularityValue ? Popularity.POPULAR : Popularity.REGULAR;

    }

    public String toString() {
        return String.format("\n!!! name: %s \n description: %s \n id: %s \n area: %s \n comment: %s \n counter: %s_%s_%s \n",
                name, description, id, area.toString(), comment, views_count, popularity, date_create);
    }
}