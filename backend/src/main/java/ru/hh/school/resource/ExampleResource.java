package ru.hh.school.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import ru.hh.school.dto.AddInfo;
import ru.hh.school.dto.CommentInfo;
import ru.hh.school.entity.Employer;
import ru.hh.school.service.EmployerService;
import ru.hh.school.service.VacancyService;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Singleton
@Path("/")
public class ExampleResource {

  private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);
  private final EmployerService employerService;
  private final VacancyService vacancyService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  public  ExampleResource(EmployerService employerService, VacancyService vacancyService){
    this.employerService = employerService;
    this.vacancyService = vacancyService;
  }

  @GET
  public void dummy() {
    logger.info("Do nothing!!!");
  }

  @POST
  @Path("/favorites/vacancy")
  public Response addVacancyToFavorite(AddInfo info) throws JsonProcessingException {
    System.out.println(info.getId().toString());
    System.out.println(info.getComment());
    vacancyService.addVacancyToFavorite(info.getId().toString(), info.getComment());

    return Response.ok().build();
  }

  @DELETE
  @Path("/favorites/vacancy/{vacancy_id}")
  public Response dellVacancyFromFavorite(@PathParam("vacancy_id") Integer id) {
    vacancyService.dellVacancyFromFavorite(id);
    return Response.ok().build();
  }

  @POST
  @Path("/favorites/vacancy/{vacancy_id}/refresh")
  public Response refreshVacancy(@PathParam("vacancy_id") String id) throws JsonProcessingException {
    vacancyService.refreshVacancy(id);
    return Response.ok().build();
  }

  @GET
  @Path("/favorites/vacancy")
  @Produces("application/json")
  public Response getFavoriteVacancies(@QueryParam("page")@DefaultValue("0") String page,
                                       @QueryParam("per_page")@DefaultValue("20") String per_page) throws IOException {
    return Response.ok(vacancyService.getAllVacancies(page, per_page)).build();
  }

  @GET
  @Path("/vacancy/{vacancy_id}")
  @Produces("application/json")
  public Response vacancy(@PathParam("vacancy_id") String id) throws IOException {
    return Response.ok(vacancyService.getVacancyFromHH(id)).build();
  }

  @GET
  @Path("/vacancy")
  @Produces("application/json")
  public Response vacancies(@QueryParam("query") String query,
                            @QueryParam("page")@DefaultValue("0") String page,
                            @QueryParam("per_page")@DefaultValue("20") String perPage) throws IOException {

    return Response.ok(vacancyService.getVacanciesFromHH(query, page, perPage)).build();
  }

  @POST
  @Path("/favorites/employer/{employer_id}/refresh")
  public Response refreshEmployer(@PathParam("employer_id") Integer id) throws JsonProcessingException {
    employerService.refreshEmployer(id);
    return Response.ok().build();
  }

  @PUT
  @Path("/favorites/employer/{employer_id}")
  public Response changeComment(@PathParam("employer_id") Integer id, CommentInfo info){
    employerService.changeСomment(id, info.getComment());
    return Response.ok().build();
  }

  @DELETE
  @Path("/favorites/employer/{employer_id}")
  public Response dellEmployerFromFavorite(@PathParam("employer_id") Integer id){
    employerService.dellEmployerFromFavorite(id);
    return Response.ok().build();
  }

  @GET
  @Path("/favorites/employer")
  @Produces("application/json")
  public Response getFavoriteEmployers(@QueryParam("page")@DefaultValue("0") String page,
                                       @QueryParam("per_page")@DefaultValue("20") String per_page) throws IOException {
    return Response.ok(employerService.getAllEmployersWithArea(page, per_page)).build();
  }


  @POST
  @Path("/favorites/employer")
  public Response addEmployerToFavorite(AddInfo info) throws JsonProcessingException {
    final RestTemplate restTemplate = new RestTemplate();
    String stringIn = restTemplate
            .getForObject(String.format("https://api.hh.ru/employers/%s", info.getId().toString()), String.class);

    ObjectMapper mapper = new ObjectMapper();
    Employer employer = mapper.readValue(stringIn, Employer.class);
    employer.setComment(info.getComment());
    employer.setViews_count(1);
    employerService.save(employer);
    return Response.ok().build();
  }


  @GET
  @Path("/employer")
  @Produces("application/json")
  public Response employers(@QueryParam("query") String query,
                      @QueryParam("page")@DefaultValue("0") String page,
                      @QueryParam("per_page")@DefaultValue("20") String perPage) throws IOException {
//    final RestTemplate restTemplate = new RestTemplate();
    String stringIn = restTemplate
            .getForObject(String.format("https://api.hh.ru/employers?text=%s&page=%s&per_page=%s", query, page, perPage), String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNodeIn = mapper.readValue(stringIn, JsonNode.class);

    ObjectNode rootNodeOut = mapper.createObjectNode();
    rootNodeOut.put("found", rootNodeIn.get("found"));
    rootNodeOut.put("pages", rootNodeIn.get("pages"));
    rootNodeOut.put("per_page", rootNodeIn.get("per_page"));
    rootNodeOut.put("page", rootNodeIn.get("page"));
    JsonNode inItems = rootNodeIn.get("items");
    ArrayNode outItems = mapper.createArrayNode();
    for (JsonNode node : inItems){
      String id = node.path("id").asText();
      String name = node.path("name").asText();
      ObjectNode employer = mapper.createObjectNode();
      employer.put("id", id);
      employer.put("name", name);
      outItems.add(employer);
    }

    rootNodeOut.put("items", outItems);
    OutputStream outputStream = new ByteArrayOutputStream();
    mapper.writeValue(outputStream, rootNodeOut);
    return Response.ok(outputStream.toString()).build();
  }

    @GET
    @Path("/employer/{employer_id}")
    @Produces("application/json")
    public Response employer(@PathParam("employer_id") String id) throws JsonProcessingException {
      return Response.ok(employerService.getEmployerFromHH(id)).build();
    }
}
