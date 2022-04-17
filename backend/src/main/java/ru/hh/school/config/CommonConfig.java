package ru.hh.school.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.nab.hibernate.MappingConfig;
import ru.hh.nab.starter.NabCommonConfig;
import ru.hh.school.dao.EmployerDao;
import ru.hh.school.dao.GenericDao;
import ru.hh.school.dao.VacancyDao;
import ru.hh.school.entity.Area;
import ru.hh.school.entity.Employer;
import ru.hh.school.resource.ExampleResource;
import ru.hh.school.service.EmployerService;
import ru.hh.school.service.VacancyService;

@Configuration
@Import({
  // import your beans here
  ExampleResource.class,
  NabCommonConfig.class,
  EmployerDao.class,
  GenericDao.class,
  Area.class,
  Employer.class,
  EmployerService.class,
  VacancyService.class,
  VacancyDao.class
})
public class CommonConfig {

  @Bean
  public MappingConfig mappingConfig() {
    MappingConfig mappingConfig = new MappingConfig();
    mappingConfig.addPackagesToScan("ru.hh.school.entity");
    return mappingConfig;
  }
}
