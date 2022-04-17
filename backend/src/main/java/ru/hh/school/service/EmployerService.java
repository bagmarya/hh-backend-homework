package ru.hh.school.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import ru.hh.school.dao.EmployerDao;
import ru.hh.school.dto.EmployerDto;
import ru.hh.school.entity.Employer;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Singleton
public class EmployerService {
    private final EmployerDao employerDao;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public EmployerService(EmployerDao employerDao) {
        this.employerDao = employerDao;
        this.mapper = new ObjectMapper();
    }

    public void save(Employer employer){
        employerDao.save(employer);
    }

    public String getAllEmployersWithArea(String page, String per_page) throws IOException {
        for(Employer e : employerDao.getAllEmployersWithArea()){
            System.out.println(e.toString());
        }
        ArrayNode outItems = mapper.createArrayNode();
        employerDao.getAllEmployersWithArea()
                .stream()
                .skip(Long.parseLong(page) * Long.parseLong(per_page))
                .limit(Long.parseLong(per_page))
                .map(Employer::incrementCount)
                .map(e -> {
                    employerDao.save(e);
                    return e;})
                .map(EmployerService::employerToEmployerDto)
                .map(e -> {
                    try {
                        return mapper.readValue(mapper.writeValueAsString(e), JsonNode.class);
                    } catch (JsonProcessingException ex) {
                        ex.printStackTrace();{
                        };
                    }
                    return null;
                })
                .forEach(e -> outItems.add(e));

        String found = employerDao.CountAllFavorites();
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

    public static EmployerDto employerToEmployerDto(Employer employer){
        EmployerDto employerDto = new EmployerDto(employer.getId(), employer.getName(),
                employer.getDescription(),
                employer.getArea(),
                employer.getComment(),
                employer.getViews_count(),
                employer.getDate_create()
        );
        return employerDto;
    }

    public void changeСomment(Integer id, String comment) {
        Employer employer = employerDao.get(Employer.class, id);
        employer.setComment(comment);
        employerDao.save(employer);
    }

    public void dellEmployerFromFavorite(Integer id) {
        Employer employer = employerDao.get(Employer.class, id);
        employerDao.delete(employer);
    }

    public void refreshEmployer(Integer id) throws JsonProcessingException {

        final RestTemplate restTemplate = new RestTemplate();
        String stringIn = restTemplate
                .getForObject(String.format("https://api.hh.ru/employers/%s", id), String.class);
        Employer actualInfo = mapper.readValue(stringIn, Employer.class);
        Employer employer = employerDao.get(Employer.class, id);
        employer.setName(actualInfo.getName());
        employer.setArea(actualInfo.getArea());
        employer.setDescription(actualInfo.getDescription());
        employerDao.save(employer);
    }

    public String getEmployerFromHH(String id) throws JsonProcessingException {
        String stringIn = restTemplate
                .getForObject(String.format("https://api.hh.ru/employers/%s", id), String.class);

        Employer employer = mapper.readValue(stringIn, Employer.class);
        return mapper.writeValueAsString(employer);

    }
}
