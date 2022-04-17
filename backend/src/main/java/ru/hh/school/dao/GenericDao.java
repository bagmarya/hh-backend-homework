package ru.hh.school.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


import javax.transaction.Transactional;

@Transactional
public class GenericDao {
    protected final SessionFactory sessionFactory;

    public GenericDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
}

    public <T> T get(Class<T> clazz, Integer id) {
        return getSession().get(clazz, id);
    }

    public void save(Object object) {
        if (object == null) {
            return;
        }
        getSession().saveOrUpdate(object);
    }
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    public void delete(Object object) {
        if (object == null) {
            return;
        }
        getSession().delete(object);
    }
}
