package ru.hh.school.dao;


import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hh.school.entity.Employer;

import java.util.List;


@Singleton
public class EmployerDao extends GenericDao{
    @Autowired
    protected EmployerDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public List<Employer> getAllEmployersWithArea() {
        return getSession()
                .createQuery("select e from Employer e join fetch e.area order by e.name", Employer.class)
                .getResultList();
    }

    public String CountAllFavorites(){
        return getSession()
                .createNativeQuery("select count(*) from employer;")
                .getSingleResult().toString();

    }

}


