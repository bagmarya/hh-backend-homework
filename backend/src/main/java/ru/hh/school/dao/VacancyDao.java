package ru.hh.school.dao;


import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hh.school.entity.Vacancy;

import java.util.List;


@Singleton
public class VacancyDao extends GenericDao{

    @Autowired
    public VacancyDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public List<Vacancy> getAllVacanciesEager() {
        return getSession()
                .createQuery(
                        "select v from Vacancy v join fetch v.area join fetch v.employer order by v.name",
                        Vacancy.class)
                .getResultList();
    }

    public String CountAllVacancies() {
        return getSession()
                .createNativeQuery("select count(*) from vacancy;")
                .getSingleResult().toString();
    }
}
