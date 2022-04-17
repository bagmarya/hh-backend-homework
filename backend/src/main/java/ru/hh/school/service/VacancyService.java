package ru.hh.school.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import ru.hh.school.dao.EmployerDao;
import ru.hh.school.dao.VacancyDao;
import ru.hh.school.dto.EmployerDto;
import ru.hh.school.dto.SalaryDto;
import ru.hh.school.dto.VacancyDto;
import ru.hh.school.entity.Employer;
import ru.hh.school.entity.Vacancy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Singleton;


@Singleton
public class VacancyService {
    private final VacancyDao vacancyDao;
    private final EmployerDao employerDao;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public VacancyService(VacancyDao vacancyDao, EmployerDao employerDao) {
        this.vacancyDao = vacancyDao;
        this.employerDao = employerDao;
        this.mapper = new ObjectMapper();
    }


    public String getVacanciesFromHH(String query, String page, String perPage) throws IOException {
        String stringIn = restTemplate
                .getForObject(String.format("https://api.hh.ru/vacancies?text=%s&page=%s&per_page=%s", query, page, perPage), String.class);

    JsonNode rootNodeIn = mapper.readValue(stringIn, JsonNode.class);
    ObjectNode rootNodeOut = mapper.createObjectNode();

    rootNodeOut.put("found", rootNodeIn.get("found"));
    rootNodeOut.put("pages", rootNodeIn.get("pages"));
    rootNodeOut.put("per_page", rootNodeIn.get("per_page"));
    rootNodeOut.put("page", rootNodeIn.get("page"));
    JsonNode inItems = rootNodeIn.get("items");
    ArrayNode outItems = mapper.createArrayNode();

    for (JsonNode node : inItems){
        ObjectNode vacancy = mapper.createObjectNode();
        ObjectNode employer = mapper.createObjectNode();
        ObjectNode area = mapper.createObjectNode();
        vacancy.put("id", node.path("id"));
        vacancy.put("name", node.path("name"));
        vacancy.put("salary", node.path("salary"));
        vacancy.put("created_at", node.path("created_at"));
        employer.put("id", node.get("employer").path("id"));
        employer.put("name", node.get("employer").path("name"));
        area.put("id", node.get("area").path("id"));
        area.put("name", node.get("area").path("name"));
        vacancy.put("employer", employer);
        vacancy.put("area", area);
        outItems.add(vacancy);
    }

    rootNodeOut.put("items", outItems);
    OutputStream outputStream = new ByteArrayOutputStream();
    mapper.writeValue(outputStream, rootNodeOut);
    return outputStream.toString();
    }

    public Vacancy getVacancyObject(String id) throws JsonProcessingException {
        String vacancyStringIn = restTemplate
                .getForObject(String.format("https://api.hh.ru/vacancies/%s", id), String.class);
        JsonNode rootNodeIn = mapper.readValue(vacancyStringIn, JsonNode.class);
        Vacancy vacancy = mapper.readValue(vacancyStringIn, Vacancy.class);
        vacancy.setFrom(rootNodeIn.get("salary").path("from").asInt());
        vacancy.setTo(rootNodeIn.get("salary").path("to").asInt());
        vacancy.setCurrency(rootNodeIn.get("salary").path("currency").asText());
        vacancy.setGross(rootNodeIn.get("salary").path("gross").asBoolean());

        String employerStringIn = restTemplate
                .getForObject(String.format("https://api.hh.ru/employers/%s", rootNodeIn.get("employer").path("id").asInt()), String.class);

        Employer employer = mapper.readValue(employerStringIn, Employer.class);
        vacancy.setEmployer(employer);
        return vacancy;
    }

    public String getVacancyFromHH(String id) throws IOException {
        Vacancy vacancy = getVacancyObject(id);
        ObjectNode rootNodeOut = (ObjectNode) mapper.readValue(mapper.writeValueAsString(vacancy), JsonNode.class);
        JsonNode employerNode = mapper.readValue(mapper.writeValueAsString(vacancy.getEmployer()), JsonNode.class);
        rootNodeOut.put("employer", employerNode);
        ObjectNode salaryNode = mapper.createObjectNode();
        salaryNode.put("from", vacancy.getFrom());
        salaryNode.put("to", vacancy.getTo());
        salaryNode.put("currency", vacancy.getCurrency());
        salaryNode.put("gross", vacancy.isGross());
        rootNodeOut.put("salary", salaryNode);

        OutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, rootNodeOut);

    return outputStream.toString();
    }

    public void addVacancyToFavorite(String id, String comment) throws JsonProcessingException {
        Vacancy vacancy = getVacancyObject(id);
        vacancy.setComment(comment);
        vacancy.setViews_count(1);
        vacancy.getEmployer().setViews_count(1);
        employerDao.save(vacancy.getEmployer());
        vacancyDao.save(vacancy);
    }

    public void dellVacancyFromFavorite(Integer id) {
        Vacancy vacancy = vacancyDao.get(Vacancy.class, id);
        employerDao.delete(vacancy);
    }

    public void refreshVacancy(String id) throws JsonProcessingException {
        Vacancy vacancy = getVacancyObject(id);
        employerDao.save(vacancy.getEmployer());
        vacancyDao.save(vacancy);
    }

    public String getAllVacancies(String page, String per_page) throws IOException {

        for(Vacancy v : vacancyDao.getAllVacanciesEager()){
            System.out.println(v.toString());
        }
        ArrayNode outItems = mapper.createArrayNode();
        vacancyDao.getAllVacanciesEager()
                .stream()
                .skip(Long.parseLong(page) * Long.parseLong(per_page))
                .limit(Long.parseLong(per_page))
                .map(Vacancy::incrementCount)
                .map(v -> {
                    employerDao.save(v.getEmployer());
                    vacancyDao.save(v);
                    return v;})
                .map(VacancyService::vacancyToVacancyDto)
                .map(v -> {
                    try {
                        return mapper.readValue(mapper.writeValueAsString(v), JsonNode.class);
                    } catch (JsonProcessingException ex) {
                        ex.printStackTrace();{
                        };
                    }
                    return null;
                })
                .forEach(v -> outItems.add(v));

        String found = vacancyDao.CountAllVacancies();
        Integer pages = (Integer.parseInt(found) / Integer.parseInt(per_page)) +1;

        ObjectNode rootNodeOut = mapper.createObjectNode();
        rootNodeOut.put("found", found);
        rootNodeOut.put("pages", pages.toString());
        rootNodeOut.put("per_page", per_page);
        rootNodeOut.put("page", page);
        rootNodeOut.put("items", outItems);

        OutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, rootNodeOut);

        return outputStream.toString();
    }

    private static VacancyDto vacancyToVacancyDto(Vacancy vacancy) {
        EmployerDto employer = EmployerService.employerToEmployerDto(vacancy.getEmployer());
        SalaryDto salary = new SalaryDto(vacancy.getFrom(), vacancy.getTo(), vacancy.getCurrency(), vacancy.isGross());
        VacancyDto vacancyDto = new VacancyDto(
                vacancy.getId(),
                vacancy.getName(),
                vacancy.getCreated_at(),
                vacancy.getArea(),
                vacancy.getComment(),
                vacancy.getViews_count(),
                vacancy.getDate_create(),
                salary,
                employer);
        return vacancyDto;
    }
}
