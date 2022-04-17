package ru.hh.school.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "employer")
public class Employer {
    @JsonProperty
    @Id
    @Column(name = "employer_id")
    public Integer id;

    @JsonProperty
    public String description;

    @JsonProperty
    public String name;

    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
//    @Column(name = "area_id")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Area area;

    @JsonIgnore
    public String comment;
    @JsonIgnore
    public Integer views_count;
    @JsonIgnore
    @CreationTimestamp
    public Date date_create;

    public Employer(){}

    public Employer(Integer id, String name, String description, Area area, String comment, Integer counter, Date date_create) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
        this.comment = comment;
        this.views_count = counter;
        this.date_create = date_create;
    }

    public Employer(Integer id, String name, String description, Area area) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
    }

    public Employer incrementCount(){
        views_count++;
        return this;
    }

    public String toString() {
        return String.format("\n!!! name: %s \n description: %s \n id: %s \n area: %s \n comment: %s \n counter: %s_%s \n",
                name, description, id, area.toString(), comment, views_count, date_create);
    }
}