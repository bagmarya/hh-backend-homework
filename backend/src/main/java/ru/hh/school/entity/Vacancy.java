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
@Table(name = "vacancy")
public class Vacancy {

    @JsonProperty
    @Id
    @Column(name = "vacancy_id")
    public Integer id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String created_at;

    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Area area;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id")
//    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Employer employer;

    @JsonIgnore
    @Column(name = "salary_gross")
    private boolean gross;

    @JsonIgnore
    @Column(name = "salary_to")
    private Integer to;

    @JsonIgnore
    @Column(name = "salary_from")
    private Integer from;

    @JsonIgnore
    @Column(name = "salary_currency")
    private String currency;



    @JsonIgnore
    public String comment;
    @JsonIgnore
    public Integer views_count;
    @JsonIgnore
    @CreationTimestamp
    public Date date_create;

    public Vacancy(){}

    public Vacancy(Integer id,
                   String name,
                   String created_at,
                   Area area,
                   Employer employer,
                   boolean gross,
                   Integer to,
                   Integer from,
                   String currency,
                   String comment,
                   Integer views_count,
                   Date date_create){
        this.name = name;
        this.id = id;
        this.created_at = created_at;
        this.area = area;
        this.employer = employer;
        this.gross = gross;
        this.to = to;
        this.from = from;
        this.currency = currency;
        this.comment = comment;
        this.views_count = views_count;
        this.date_create = date_create;
    }

    public Vacancy(Integer id,
                   String name,
                   String created_at,
                   Area area,
                   Employer employer){
        this.name = name;
        this.id = id;
        this.created_at = created_at;
        this.area = area;
        this.employer = employer;
    }

    public Vacancy incrementCount(){
        views_count++;
        this.employer.incrementCount();
        return this;
    }

    public String toString() {
        return String.format("\n!!! name: %s \n create_at: %s \n id: %s \n area: %s \n comment: %s \n counter: %s_%s \n employer: --- \n",
                name, created_at, id, area.toString(), comment, views_count, date_create);
    }



}
