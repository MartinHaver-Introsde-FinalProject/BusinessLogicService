package resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;
import calculation.Calculation;
import model.Activity;
import model.ActivitySelection;
import model.Goal;
import model.HealthMeasure;
import model.Person;
import utils.RandomData;

@Stateless
@LocalBean
@Path("/blogic")
public class BusinessResouces {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	private RandomData rd = new RandomData();

	private static URI getExBaseURI() {
		return UriBuilder.fromUri("https://shrouded-refuge-42685.herokuapp.com/storage").build();
	}
	
	//Info about the service.
	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getInfo() {
		System.out.println("Getting api information...");
		return "This Business Logic Service is part of a project by M.Haver.";
	}
	
	//Obtain motivation quote.
	@GET
	@Path("/motivation")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getQuote2() throws ClientProtocolException, IOException {
        
        String ENDPOINT = "https://shrouded-refuge-42685.herokuapp.com/api/getQuote";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ENDPOINT);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        JSONObject o = new JSONObject(result.toString());
        if(response.getStatusLine().getStatusCode() == 200){
            return Response.ok(o.toString()).build();
         }
        return Response.status(204).build();
     }
	
	/*
	 * Adding new health measure (e.g. weight, height, age)
	 * 
	 * URL: http://localhost:8080/introsde.business-logic-service/api/person/1/healthMeasure
	 * 
	 * POST: OK
	 * 
	 * { "idPerson": 1, "firstname": "Chuck", "lastname": "Norris", "birthdate":
	 * "1945-01-01 00:00:00", "username": "chuck.norris", "sex": 1,
	 * "healthMeasures": [ { "idHealthMeasure": 1, "measureDefinition": {
	 * "idMeasureDefinition": 1, "measureName": "weight", "measureType":
	 * "double", "measureRanges": [] }, "value": 88 }, { "idHealthMeasure": 2,
	 * "measureDefinition": { "idMeasureDefinition": 2, "measureName": "height",
	 * "measureType": "double", "measureRanges": [] }, "value": 1.6 }, {
	 * "idHealthMeasure": 3, "measureDefinition": { "idMeasureDefinition": 3,
	 * "measureName": "age", "measureType": "integer", "measureRanges": [] },
	 * "value": 29 } ] }
	 */

	@POST
	@Path("/person/{idPerson}/healthMeasure")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person updateHealthMeasure(@PathParam("idPerson") int idPerson, Person person) {
		System.out.println("Adding new Health Measurements for a Person...");
		return saveHealthMeasure(person);
	}
	
	/*
	 * Getting activity suggestions
	 * 
	 * http://localhost:8080/introsde.business-logic-service/api/person/1/activitySuggestion
	 * 
	 * GET: OK
	 */
	
	@GET
	@Path("/person/{idPerson}/activitySuggestion")
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<Activity> getActivitySuggestions(@PathParam("idPerson") int idPerson) {
		System.out.println("Getting activity suggestions for a person...");
		return suggestActivities();
	}
	
	
	/*
	 * Updating Activity Selection (time, usedCalories)
	 * 
	 * http://localhost:8080/introsde.local-database-service/person/1/goal/activitySelection
	 * 
	 * PUT: OK
	 * 
	 * { "time": 2}
	 * 
	 */

	@PUT
	@Path("/person/{idPerson}/activitySelection")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person saveGoalForPerson(@PathParam("idPerson") int idPerson, ActivitySelection activitySelection)
			throws IOException {
		System.out.println("Updating a current Activity Selection of a current Goal of a Person with id = " + idPerson
				+ " and with Activity Selection id = " + activitySelection.getIdActivitySelection() + "...");
		
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		
		WebTarget service = client.target(getExBaseURI()).path("person").path(String.valueOf(idPerson)).path("goal").path("activitySelection");
		Response response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
		int httpStatus = response.getStatus();
		if (httpStatus == 200) {
			ActivitySelection tmpActivitySelection = response.readEntity(ActivitySelection.class);
			Activity activity = tmpActivitySelection.getActivity();
			activitySelection.setUsedCalories(activitySelection.getTime() * activity.getCaloriesPerHour());
		}
		
		service = client.target(getExBaseURI()).path("person").path(String.valueOf(idPerson)).path("goal").path("activitySelection");
		response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(Entity.json(activitySelection));
		httpStatus = response.getStatus();
		Person person = null;
		if (httpStatus == 200) {
			person = response.readEntity(Person.class);
		}
		
		for (Goal goal : person.getGoals()) {
			if (goal.getCurrent() == 1) {
				goal.setShavedCalories(goal.getShavedCalories() + activitySelection.getUsedCalories());
				service = client.target(getExBaseURI()).path("person").path(String.valueOf(idPerson)).path("goal");
				response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(Entity.json(goal));
				if (httpStatus == 200) {
					person = response.readEntity(Person.class);
				}
			}
		}
		return person;
	}
		
	/*
	 * Create a new goal of a specific person.
	 */
	
	@POST
	@Path("/person/{idPerson}/goal")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person createNewGoal(@PathParam("idPerson") int idPerson, Person person) {
		return createNewGoal(person);
	}
	
	/*
	 * Supporting functions.
	 */
	
	public Person createNewGoal(Person person) {
		Calculation calc = new Calculation();
		
		double bmi = 0;
		double height = 0;
		double weight = 0;
		double bmr = 0;
		for (HealthMeasure healthMeasure : person.getHealthMeasures()) {
			if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("bmi")) {
				bmi = healthMeasure.getValue();
			} else if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("height")) {
				height = healthMeasure.getValue();
			} else if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("weight")) {
				weight = healthMeasure.getValue();
			} else if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("bmr")) {
				bmr = healthMeasure.getValue();
			}
		}
		int sex = person.getSex();
		
		Goal goal = new Goal();
		goal.setActivitySelections(null);
		goal.setCurrent(1);
		goal.setDate(rd.getDateTime());
		goal.setGoalName(calc.getGoalName(bmi));
		goal.setIdealBmi(calc.getBMI(height, weight));
		goal.setIdealWeight(calc.getIdealWeight(height, sex));
		goal.setIdGoal(0);
		goal.setShavedCalories(calc.getCaloriesNeedForDailyActivity(bmr));


		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget service = client.target(getExBaseURI()).path("person").path(String.valueOf(person.getIdPerson())).path("goal");
		Response response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.json(goal));
		int httpStatus = response.getStatus();
		if (httpStatus != 200) {
			System.err.println(response.readEntity(String.class));
			return null;
		}

		return response.readEntity(Person.class);
	}
	
	
	public List<Activity> suggestActivities() {
		List<Activity> activitySuggestions = new ArrayList<Activity>();
		for (int i = 0; i < 3; i ++) {
			int idActivity = rd.randBetween(1, 84);
			ClientConfig clientConfig = new ClientConfig();
			Client client = ClientBuilder.newClient(clientConfig);
			WebTarget service = client.target(getExBaseURI()).path("activity").path(String.valueOf(idActivity));
			Response response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
			int httpStatus = response.getStatus();
			if (httpStatus == 200) {
				Activity activity = response.readEntity(Activity.class);
				activitySuggestions.add(activity);
			}
		}
		return activitySuggestions;
	}	
	
	public Person saveHealthMeasure(Person person) {
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		Response response = null;
		WebTarget service = null;
		double height = 0;
		double weight = 0;
		int age = 0;
		int sex = person.getSex();
		int httpStatus = 0;
		for (HealthMeasure healthMeasure : person.getHealthMeasures()) {
			if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("height")) {
				height = healthMeasure.getValue();
			} else if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("weight")) {
				weight = healthMeasure.getValue();
			} else if (healthMeasure.getMeasureDefinition().getMeasureName().equalsIgnoreCase("age")) {
				age = (int) healthMeasure.getValue();
			}
			service = client.target(getExBaseURI()).path("person").path(String.valueOf(person.getIdPerson()))
					.path("healthMeasure").path(healthMeasure.getMeasureDefinition().getMeasureName());
			response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(healthMeasure));
			httpStatus = response.getStatus();
			if (httpStatus != 200) {
				System.err.println(response.readEntity(String.class));
				return null;
			}
		}

		Calculation calc = new Calculation();

		double bmi = calc.getBMI(height, weight);

		HealthMeasure tempHealthMeasure = new HealthMeasure();
		tempHealthMeasure.setIdHealthMeasure(4);
		tempHealthMeasure.setMeasureDefinition(null);
		tempHealthMeasure.setValue(bmi);

		service = client.target(getExBaseURI()).path("person").path(String.valueOf(person.getIdPerson()))
				.path("healthMeasure").path("bmi");
		response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(tempHealthMeasure));
		httpStatus = response.getStatus();
		if (httpStatus != 200) {
			System.err.println(response.readEntity(String.class));
			return null;
		}

		double bmr = calc.getBMR(height, weight, age, sex);

		tempHealthMeasure = new HealthMeasure();
		tempHealthMeasure.setIdHealthMeasure(5);
		tempHealthMeasure.setMeasureDefinition(null);
		tempHealthMeasure.setValue(bmr);

		service = client.target(getExBaseURI()).path("person").path(String.valueOf(person.getIdPerson()))
				.path("healthMeasure").path("bmr");
		response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(tempHealthMeasure));
		httpStatus = response.getStatus();
		if (httpStatus != 200) {
			System.err.println(response.readEntity(String.class));
			return null;
		}

		Goal goal = new Goal();
		goal.setActivitySelections(null);
		goal.setCurrent(1);
		goal.setDate(rd.getDateTime());
		goal.setGoalName(calc.getGoalName(bmi));
		goal.setIdealBmi(calc.getBMI(height, weight));
		goal.setIdealWeight(calc.getIdealWeight(height, sex));
		goal.setIdGoal(0);
		goal.setShavedCalories(calc.getCaloriesNeedForDailyActivity(bmr));

		service = client.target(getExBaseURI()).path("person").path(String.valueOf(person.getIdPerson())).path("goal");
		response = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(goal));
		httpStatus = response.getStatus();
		if (httpStatus != 200) {
			System.err.println(response.readEntity(String.class));
			return null;
		}
		return response.readEntity(Person.class);
	}	
}
