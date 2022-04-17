package ru.hh.school.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "area")
public class Area {

    @Id
    @Column(name = "area_id")
    @JsonProperty
    public Integer id;

    @Column(name = "name")
    @JsonProperty
    public String name;


    public Area(){}

    public Area(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return String.format("id: %s  name: %s", id, name);
    }
}