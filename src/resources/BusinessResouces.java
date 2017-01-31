package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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
import model.Motivation;
import model.Person;
import utils.RandomData;

@Stateless
@LocalBean
@Path("/api")
public class BusinessResouces {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	private RandomData rd = new RandomData();

	private static URI getExBaseURI() {
		return UriBuilder.fromUri("https://shrouded-refuge-42685.herokuapp.com/api").build();
	}
	
	/*
	 * Getting information of the business logic service.
	 * 
	 * http://localhost:8080/introsde.business-logic-service/api
	 * 
	 * GET: OK
	 */

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getInfo() {
		System.out.println("Getting api information...");
		return "Hello! This is Business Logic Service.";
	}
	
	
	@GET
	@Path("/motivation")
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
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
	 * Getting motivation quote for a person.
	 * 
	 * URL: http://localhost:8080/introsde.business-logic-service/api/person/1/motivation
	 * 
	 * GET: OK
	 */

	@GET
	@Path("/person/{idPerson}/motivation/original")
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Motivation getMotivationByRandomId(@PathParam("idPerson") int idPerson) {
		System.out.println("Getting a motivation quote ...");
		int idMotivation = new RandomData().randBetween(1, 469);
		return Motivation.getMotivationById(idMotivation);
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
	
	@GET
	@Path("/database")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String generateDatabase() {
	    Connection c = null;
	    Statement stmt = null;
	    try {
	    	File file = new File("motivation.sqlite");
	    	if (file.exists()) {
	    		file.delete();
	    	}
	    	
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:motivation.sqlite");
	      System.out.println("Opened database successfully");
			stmt = c.createStatement();
			String sql = "DROP TABLE IF EXISTS \"Motivation\";"
					+ "CREATE TABLE Motivation (     idMotivation  INTEGER PRIMARY KEY AUTOINCREMENT,    text      TEXT  DEFAULT 'NULL');"
					+ "INSERT INTO \"Motivation\" VALUES(1,'Attitude: It is our best friend or our worst enemy.');"
					+ "INSERT INTO \"Motivation\" VALUES(2,'Your attitude determines your altitude!');"
					+ "INSERT INTO \"Motivation\" VALUES(3,'A strong positive attitude will create more miracles than any wonder drug.');"
					+ "INSERT INTO \"Motivation\" VALUES(4,'The greatest revolution of our generation is the discovery that human beings, by changing the inner attitudes of their minds, can change the outer aspects of their lives.');"
					+ "INSERT INTO \"Motivation\" VALUES(5,'The world of achievement has always belonged to the optimist.');"
					+ "INSERT INTO \"Motivation\" VALUES(6,'If you change the way you look at things, the things you look at change.');"
					+ "INSERT INTO \"Motivation\" VALUES(7,'Nothing can stop the man with the right mental attitude from achieving his goal; nothing on earth can help the man with the wrong mental attitude.');"
					+ "INSERT INTO \"Motivation\" VALUES(8,'You can complain that roses have thorns, or rejoice that thorns have roses.');"
					+ "INSERT INTO \"Motivation\" VALUES(9,'It is our attitude at the beginning of a difficult task which, more than anything else, will affect its successful outcome.');"
					+ "INSERT INTO \"Motivation\" VALUES(10,'Life is 10% what happens to us and 90% how we react to it.');"
					+ "INSERT INTO \"Motivation\" VALUES(11,'The last of the human freedoms is to choose one’s attitude in any given set of circumstances.');"
					+ "INSERT INTO \"Motivation\" VALUES(12,'Everyone faces defeat. It may be a stepping-stone or a stumbling block, depending on the mental attitude with which it is faced.');"
					+ "INSERT INTO \"Motivation\" VALUES(13,'Life is a grindstone. Whether it grinds us down or polishes us up depends on us.');"
					+ "INSERT INTO \"Motivation\" VALUES(14,'If you don’t like something, change it. If you can’t change it, change your attitude. Don’t complain.');"
					+ "INSERT INTO \"Motivation\" VALUES(15,'Progress is impossible without change and those who cannot change their minds cannot change anything.');"
					+ "INSERT INTO \"Motivation\" VALUES(16,'If you change the way you look at things, the things you look at change.');"
					+ "INSERT INTO \"Motivation\" VALUES(17,'The difference between a mountain and a molehill is your perspective.');"
					+ "INSERT INTO \"Motivation\" VALUES(18,'It is better to take many small steps in the right direction than to make a great leap forward only to stumble backward.');"
					+ "INSERT INTO \"Motivation\" VALUES(19,'Every choice you make has an end result.');"
					+ "INSERT INTO \"Motivation\" VALUES(20,'Little by little does the trick.');"
					+ "INSERT INTO \"Motivation\" VALUES(21,'One may walk over the highest mountain one step at a time.');"
					+ "INSERT INTO \"Motivation\" VALUES(22,'The man who removes a mountain begins by carrying away small stones.');"
					+ "INSERT INTO \"Motivation\" VALUES(23,'If you chase two rabbits, both will escape.');"
					+ "INSERT INTO \"Motivation\" VALUES(24,'Don’t judge each day by the harvest you reap, but by the seeds you plant.');"
					+ "INSERT INTO \"Motivation\" VALUES(25,'Every day do something that will inch you closer to a better tomorrow.');"
					+ "INSERT INTO \"Motivation\" VALUES(26,'Great things are not done by impulse, but by a series of small things brought together.');"
					+ "INSERT INTO \"Motivation\" VALUES(27,'Every day, in every way, I am getting better and better.');"
					+ "INSERT INTO \"Motivation\" VALUES(28,'The best thing about the future is that it only comes one day at a time.');"
					+ "INSERT INTO \"Motivation\" VALUES(29,'The elevator to success is out of order. You’ll have to use the stairs…one step at a time.');"
					+ "INSERT INTO \"Motivation\" VALUES(30,'To climb steep hills requires slow pace at first.');"
					+ "INSERT INTO \"Motivation\" VALUES(31,'Success is a staircase, not a doorway.');"
					+ "INSERT INTO \"Motivation\" VALUES(32,'Success is the sum of small efforts, repeated day in and day out.');"
					+ "INSERT INTO \"Motivation\" VALUES(33,'You will never change your life until you change something you do daily.');"
					+ "INSERT INTO \"Motivation\" VALUES(34,'Habit is habit and not to be flung out of the window by any man, but coaxed downstairs a step at a time.');"
					+ "INSERT INTO \"Motivation\" VALUES(35,'Be not afraid of growing slowly, be afraid only of standing still.');"
					+ "INSERT INTO \"Motivation\" VALUES(36,'It’s the little details that are vital. Little things make big things happen.');"
					+ "INSERT INTO \"Motivation\" VALUES(37,'Whether you think you can or canâ€™t, youâ€™re right.');"
					+ "INSERT INTO \"Motivation\" VALUES(38,'First thing every morning before you arise, say out loud, ‘I believe’, three times.');"
					+ "INSERT INTO \"Motivation\" VALUES(39,'Faith is taking the first step, even when you don’t see the whole staircase.');"
					+ "INSERT INTO \"Motivation\" VALUES(40,'Believe and act as if it were impossible to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(41,'I’ve come to believe that all my past failure and frustration were actually laying the foundation for the understandings that have created the new level of living I now enjoy.');"
					+ "INSERT INTO \"Motivation\" VALUES(42,'Believe while others are doubting.');"
					+ "INSERT INTO \"Motivation\" VALUES(43,'Itâ€™s not who you think you are that holds you back; itâ€™s who you think youâ€™re not.');"
					+ "INSERT INTO \"Motivation\" VALUES(44,'I would rather have a mind opened by wonder than closed by belief.');"
					+ "INSERT INTO \"Motivation\" VALUES(45,'Stumbling is not falling.');"
					+ "INSERT INTO \"Motivation\" VALUES(46,'As long as a man stands in his own way, everything seems to be in his way.');"
					+ "INSERT INTO \"Motivation\" VALUES(47,'If you find a path with no obstacles, it probably doesnâ€™t lead anywhere.');"
					+ "INSERT INTO \"Motivation\" VALUES(48,'Obstacles donâ€™t have to stop you. If you run into a wall, donâ€™t turn around and give up. Figure out how to climb it, go through it, or work around it.');"
					+ "INSERT INTO \"Motivation\" VALUES(49,'A stumble may prevent a fall.');"
					+ "INSERT INTO \"Motivation\" VALUES(50,'Itâ€™s not the mistakes in life that are important; itâ€™s what we learn from them.');"
					+ "INSERT INTO \"Motivation\" VALUES(51,'The significant problems we face cannot be solved by the same level of thinking that created them.');"
					+ "INSERT INTO \"Motivation\" VALUES(52,'If doubt is challenging you and you do not act, doubts will grow. Challenge the doubts with action and you will grow. Doubt and action are incompatible.');"
					+ "INSERT INTO \"Motivation\" VALUES(53,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(54,'Whatever course you decide upon, there is always someone to tell you that you are wrong. There are always difficulties arising which tempt you to believe that your critics are right. To map out a course of action and follow it to an end requires courage.');"
					+ "INSERT INTO \"Motivation\" VALUES(55,'In three words I can sum up everything I’ve learned about life. It goes on.');"
					+ "INSERT INTO \"Motivation\" VALUES(56,'Failure is not fatal. Only failure to get back up is.');"
					+ "INSERT INTO \"Motivation\" VALUES(57,'Don’t dwell on what went wrong. Instead, focus on what to do next. Spend your energies on moving forward toward finding the answer.');"
					+ "INSERT INTO \"Motivation\" VALUES(58,'You don’t drown by falling in the water. You drown by staying there.');"
					+ "INSERT INTO \"Motivation\" VALUES(59,'Smooth seas do not make skillful sailors.');"
					+ "INSERT INTO \"Motivation\" VALUES(60,'Stand up to your obstacles and do something about them. You will find that they haven’t half the strength you think they have.');"
					+ "INSERT INTO \"Motivation\" VALUES(61,'We must be the change we wish to see in the world.');"
					+ "INSERT INTO \"Motivation\" VALUES(62,'You cannot expect to achieve new goals or move beyond your present circumstances unless you change.');"
					+ "INSERT INTO \"Motivation\" VALUES(63,'The key to change… is to let go of fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(64,'When you blame others, you give up your power to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(65,'If you don’t like something, change it. If you can’t change it, change your attitude. Don’t complain.');"
					+ "INSERT INTO \"Motivation\" VALUES(66,'Grant me the serenity to accept the things I cannot change; courage to change the things I can; and wisdom to know the difference.');"
					+ "INSERT INTO \"Motivation\" VALUES(67,'Do not let what you cannot do interfere with what you can do.');"
					+ "INSERT INTO \"Motivation\" VALUES(68,'You will never change your life until you change something you do daily.');"
					+ "INSERT INTO \"Motivation\" VALUES(69,'Quite often we change our jobs, friends and spouses instead of changing ourselves.');"
					+ "INSERT INTO \"Motivation\" VALUES(70,'People only see what they are prepared to see.');"
					+ "INSERT INTO \"Motivation\" VALUES(71,'If you focus on results, you will never change. If you focus on change, you will get results.');"
					+ "INSERT INTO \"Motivation\" VALUES(72,'Progress is impossible without change and those who cannot change their minds cannot change anything.');"
					+ "INSERT INTO \"Motivation\" VALUES(73,'Insanity: doing the same thing over and over again and expecting different results.');"
					+ "INSERT INTO \"Motivation\" VALUES(74,'If weâ€™re growing, weâ€™re always going to be out of our comfort zone.');"
					+ "INSERT INTO \"Motivation\" VALUES(75,'You must take personal responsibility. You cannot change the circumstances, the seasons, or the wind, but you can change yourself.');"
					+ "INSERT INTO \"Motivation\" VALUES(76,'Everyone thinks of changing the world, but no one thinks of changing himself.');"
					+ "INSERT INTO \"Motivation\" VALUES(77,'Be bold. Be confident. Be alive. A gallery of possibilities awaits you when you make change your friend.');"
					+ "INSERT INTO \"Motivation\" VALUES(78,'Never be afraid to do something new. Remember, amateurs built the ark; professionals built the titanic.');"
					+ "INSERT INTO \"Motivation\" VALUES(79,'If you change the way you look at things, the things you look at change.');"
					+ "INSERT INTO \"Motivation\" VALUES(80,'The easier it is to do, the harder it is to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(81,'All blame is a waste of time. No matter how much fault you find with another, and regardless of how much you blame him, it will not change you.');"
					+ "INSERT INTO \"Motivation\" VALUES(82,'There are two primary choices in life: to accept conditions as they exist, or accept the responsibility for changing them.');"
					+ "INSERT INTO \"Motivation\" VALUES(83,'Two roads diverged in a wood, and I… I took the one less traveled by, and that has made all the difference.');"
					+ "INSERT INTO \"Motivation\" VALUES(84,'Choice, not chance, determines destiny.');"
					+ "INSERT INTO \"Motivation\" VALUES(85,'Decide what you want, decide what you are willing to exchange for it. Establish your priorities and go to work.');"
					+ "INSERT INTO \"Motivation\" VALUES(86,'It’s in your moments of decision that your destiny is shaped.');"
					+ "INSERT INTO \"Motivation\" VALUES(87,'You will never leave where you are, until you decide where you’d rather be.');"
					+ "INSERT INTO \"Motivation\" VALUES(88,'It’s not hard to make decisions when you know what your values are.');"
					+ "INSERT INTO \"Motivation\" VALUES(89,'Decide upon your major definite purpose in life and then organize all your activities around it.');"
					+ "INSERT INTO \"Motivation\" VALUES(90,'Take time to deliberate, but when the time for action has arrived, stop thinking and go in.');"
					+ "INSERT INTO \"Motivation\" VALUES(91,'Most people fail, not because of lack of desire, but, because of lack of commitment.');"
					+ "INSERT INTO \"Motivation\" VALUES(92,'There are only two options regarding commitment; you’re either in or you’re out. There’s no such thing as life in-between.');"
					+ "INSERT INTO \"Motivation\" VALUES(93,'There’s a difference between interest and commitment. When you’re interested in doing something, you do it only when circumstances permit. When you’re committed to something, you accept no excuses, only results.');"
					+ "INSERT INTO \"Motivation\" VALUES(94,'Stay committed to your decisions; but stay flexible in your approach.');"
					+ "INSERT INTO \"Motivation\" VALUES(95,'Whenever you do a thing, act as if all the world were watching.');"
					+ "INSERT INTO \"Motivation\" VALUES(96,'Experience tells you what to do; confidence allows you to do it.');"
					+ "INSERT INTO \"Motivation\" VALUES(97,'Courage is fear that has said its prayers.');"
					+ "INSERT INTO \"Motivation\" VALUES(98,'Courage is a resistance to fear, mastery of fear â€@ not absence of fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(99,'Courage doesn’t always roar. Sometimes courage is the quiet voice at the end of the day saying, â€˜I will try again tomorrow’.');"
					+ "INSERT INTO \"Motivation\" VALUES(100,'You are braver than you believe, stronger than you seem, and smarter than you think.');"
					+ "INSERT INTO \"Motivation\" VALUES(101,'No amount of security is worth the suffering of a life chained to a routine that has killed your dreams.');"
					+ "INSERT INTO \"Motivation\" VALUES(102,'Courage is not the absence of fear, but rather the judgment that something else is more important than fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(103,'Courage is simply the willingness to be afraid and act anyway.');"
					+ "INSERT INTO \"Motivation\" VALUES(104,'I learned that courage was not the absence of fear, but the triumph over it. The brave man is not he who does not feel afraid, but he who conquers that fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(105,'Courage is the power to let go of the familiar.');"
					+ "INSERT INTO \"Motivation\" VALUES(106,'You have to have confidence in your ability, and then be tough enough to follow through.');"
					+ "INSERT INTO \"Motivation\" VALUES(107,'The way to develop self-confidence is to do the thing you fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(108,'Confidence, like art, never comes from having all the answers; it comes from being open to all the questions.');"
					+ "INSERT INTO \"Motivation\" VALUES(109,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(110,'Whatever course you decide upon, there is always someone to tell you that you are wrong. There are always difficulties arising which tempt you to believe that your critics are right. To map out a course of action and follow it to an end requires courage.');"
					+ "INSERT INTO \"Motivation\" VALUES(111,'No matter who you are, no matter what you do, you absolutely, positively do have the power to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(112,'Put all excuses aside and remember this: YOU are capable.');"
					+ "INSERT INTO \"Motivation\" VALUES(113,'We can do anything we want as long as we stick to it long enough.');"
					+ "INSERT INTO \"Motivation\" VALUES(114,'When you reach the end of your rope, tie a knot in it and hang on.');"
					+ "INSERT INTO \"Motivation\" VALUES(115,'Opportunity may knock only once, but temptation leans on the doorbell.');"
					+ "INSERT INTO \"Motivation\" VALUES(116,'The road to success is dotted with many tempting parking places.');"
					+ "INSERT INTO \"Motivation\" VALUES(117,'We are torn between a craving to know and the despair of having known.');"
					+ "INSERT INTO \"Motivation\" VALUES(118,'Most people want to be delivered from temptation but would like it to keep in touch.');"
					+ "INSERT INTO \"Motivation\" VALUES(119,'I’ve been on a diet for two weeks and all I’ve lost is fourteen days.');"
					+ "INSERT INTO \"Motivation\" VALUES(120,'The second day of a diet is always easier than the first. By the second day you’re off it.');"
					+ "INSERT INTO \"Motivation\" VALUES(121,'At the end of every diet, the path curves back to the trough.');"
					+ "INSERT INTO \"Motivation\" VALUES(122,'I tried every diet in the book. I tried some that weren’t in the book. I tried eating the book. It tasted better than most of the diets.');"
					+ "INSERT INTO \"Motivation\" VALUES(123,'Probably nothing in the world arouses more false hopes than the first four hours of a diet.');"
					+ "INSERT INTO \"Motivation\" VALUES(124,'A diet is the penalty we pay for exceeding the feed limit.');"
					+ "INSERT INTO \"Motivation\" VALUES(125,'The cardiologist’s diet: If it tastes good, spit it out.');"
					+ "INSERT INTO \"Motivation\" VALUES(126,'The biggest seller is cookbooks and the second is diet books – how not to eat what you’ve just learned how to cook.');"
					+ "INSERT INTO \"Motivation\" VALUES(127,'The first thing you lose on a diet is your sense of humor.');"
					+ "INSERT INTO \"Motivation\" VALUES(128,'I never worry about diets. The only carrots that interest me are the number you get in a diamond.');"
					+ "INSERT INTO \"Motivation\" VALUES(129,'I’ve been on a constant diet for the last two decades. I’ve lost a total of 789 pounds. By all accounts, I should be hanging from a charm bracelet.');"
					+ "INSERT INTO \"Motivation\" VALUES(130,'If you have formed the habit of checking on every new diet that comes along, you will find that, mercifully, they all blur together, leaving you with only one definite piece of information: french-fried potatoes are out.');"
					+ "INSERT INTO \"Motivation\" VALUES(131,'I’m on a 90-day wonder diet. Thus far, I’ve lost 45 days.');"
					+ "INSERT INTO \"Motivation\" VALUES(132,'My advice if you insist on slimming: Eat as much as you like – just don’t swallow it.');"
					+ "INSERT INTO \"Motivation\" VALUES(133,'I have a great diet. You’re allowed to eat anything you want, but you must eat it with naked fat people.');"
					+ "INSERT INTO \"Motivation\" VALUES(134,'A diet is a plan, generally hopeless, for reducing your weight, which tests your will power but does little for your waistline.');"
					+ "INSERT INTO \"Motivation\" VALUES(135,'We’re the country that has more food to eat than any other country in the world, and with more diets to keep us from eating it.');"
					+ "INSERT INTO \"Motivation\" VALUES(136,'If you hear a voice within you say â€˜you cannot paint’, then by all means paint and that voice will be silenced.');"
					+ "INSERT INTO \"Motivation\" VALUES(137,'People may doubt what you say, but they will believe what you do.');"
					+ "INSERT INTO \"Motivation\" VALUES(138,'Believe while others are doubting.');"
					+ "INSERT INTO \"Motivation\" VALUES(139,'If doubt is challenging you and you do not act, doubts will grow. Challenge the doubts with action and you will grow. Doubt and action are incompatible.');"
					+ "INSERT INTO \"Motivation\" VALUES(140,'Never let the fear of striking out get in your way.@ ~George Herman @Babe');"
					+ "INSERT INTO \"Motivation\" VALUES(141,'It’s not who you think you are that holds you back; it’s who you think you’re not.');"
					+ "INSERT INTO \"Motivation\" VALUES(142,'What would you attempt to do if you knew you could not fail?');"
					+ "INSERT INTO \"Motivation\" VALUES(143,'The greatest barrier to success is the fear of failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(144,'Don’t let the fear of the time it will take to accomplish something stand in the way of your doing it. The time will pass anyway; we might as well put that passing time to the best possible use.');"
					+ "INSERT INTO \"Motivation\" VALUES(145,'Your goals, minus your doubts, equal your reality.');"
					+ "INSERT INTO \"Motivation\" VALUES(146,'Never be afraid to do something new. Remember, amateurs built the ark; professionals built the titanic.');"
					+ "INSERT INTO \"Motivation\" VALUES(147,'The key to change… is to let go of fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(148,'Act as if it were impossible to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(149,'Courage is fear that has said its prayers.');"
					+ "INSERT INTO \"Motivation\" VALUES(150,'Courage is a resistance to fear, mastery of fear â€@ not absence of fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(151,'Courage is not the absence of fear, but rather the judgment that something else is more important than fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(152,'Courage is simply the willingness to be afraid and act anyway.');"
					+ "INSERT INTO \"Motivation\" VALUES(153,'I learned that courage was not the absence of fear, but the triumph over it. The brave man is not he who does not feel afraid, but he who conquers that fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(154,'The way to develop self-confidence is to do the thing you fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(155,'Fears over tomorrow and regrets over yesterday are twin thieves that rob us of the moment.');"
					+ "INSERT INTO \"Motivation\" VALUES(156,'It is better to know some of the questions than all of the answers.');"
					+ "INSERT INTO \"Motivation\" VALUES(157,'The key to change… is to let go of fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(158,'Be not afraid of growing slowly, be afraid only of standing still.');"
					+ "INSERT INTO \"Motivation\" VALUES(159,'Try a thing you haven’t done three times. Once to get over the fear of doing it. Twice to learn how to do it. And a third time to figure out whether you like it or not.');"
					+ "INSERT INTO \"Motivation\" VALUES(160,'Those who think they have not time for bodily exercise will sooner or later have to find time for illness.');"
					+ "INSERT INTO \"Motivation\" VALUES(161,'Movement is a medicine for creating change in a person’s physical, emotional, and mental states.');"
					+ "INSERT INTO \"Motivation\" VALUES(162,'If it weren’t for the fact that the TV set and the refrigerator are so far apart, some of us wouldn’t get any exercise at all.');"
					+ "INSERT INTO \"Motivation\" VALUES(163,'Too many people confine their exercise to jumping to conclusions, running up bills, stretching the truth, bending over backwards, lying down on the job, sidestepping responsibility and pushing their luck.');"
					+ "INSERT INTO \"Motivation\" VALUES(164,'Fitness – If it came in a bottle, everybody would have a great body.');"
					+ "INSERT INTO \"Motivation\" VALUES(165,'Walking is the best possible exercise. Habituate yourself to walk very far.');"
					+ "INSERT INTO \"Motivation\" VALUES(166,'Walking: the most ancient exercise and still the best modern exercise.');"
					+ "INSERT INTO \"Motivation\" VALUES(167,'I have not failed. I’ve just found 10,000 ways that won’t work.');"
					+ "INSERT INTO \"Motivation\" VALUES(168,'To succeed you must first improve, to improve you must first practice, to practice you must first learn, to learn you must first fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(169,'The next best thing to winning is losing! At least you’ve been in the race.');"
					+ "INSERT INTO \"Motivation\" VALUES(170,'Negative results are just what I want. They’re just as valuable to me as positive results. I can never find the thing that does the job best until I find the ones that don’t.');"
					+ "INSERT INTO \"Motivation\" VALUES(171,'Our greatest glory consists not in never falling, but in rising every time we fall.');"
					+ "INSERT INTO \"Motivation\" VALUES(172,'I don’t know the key to success but the key to failure is trying to please everybody.');"
					+ "INSERT INTO \"Motivation\" VALUES(173,'Everyone faces defeat. It may be a stepping-stone or a stumbling block, depending on the mental attitude with which it is faced.');"
					+ "INSERT INTO \"Motivation\" VALUES(174,'Success is never certain. Failure is never final.');"
					+ "INSERT INTO \"Motivation\" VALUES(175,'Act as if it were impossible to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(176,'If at first you don’t succeed, you are running about average.');"
					+ "INSERT INTO \"Motivation\" VALUES(177,'Fall seven times, stand up eight.');"
					+ "INSERT INTO \"Motivation\" VALUES(178,'There is no failure. Only feedback.');"
					+ "INSERT INTO \"Motivation\" VALUES(179,'Ninety-nine percent of the failures come from people who have the habit of making excuses.');"
					+ "INSERT INTO \"Motivation\" VALUES(180,'The harder you fall, the higher you bounce.');"
					+ "INSERT INTO \"Motivation\" VALUES(181,'The only real failure in life is one not learned from.');"
					+ "INSERT INTO \"Motivation\" VALUES(182,'Stumbling is not falling.');"
					+ "INSERT INTO \"Motivation\" VALUES(183,'I would rather attempt something great and fail than attempt to do nothing and succeed.');"
					+ "INSERT INTO \"Motivation\" VALUES(184,'Failure is not fatal. Only failure to get back up is.');"
					+ "INSERT INTO \"Motivation\" VALUES(185,'Failures are like skinned knees, painful but superficial.');"
					+ "INSERT INTO \"Motivation\" VALUES(186,'When defeat comes, accept it as a signal that your plans are not sound, rebuild those plans, and set sail once more toward your coveted goal.');"
					+ "INSERT INTO \"Motivation\" VALUES(187,'In order to succeed, you must first be willing to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(188,'Even if you fall on your face, you’re still moving forward.');"
					+ "INSERT INTO \"Motivation\" VALUES(189,'Failure is an event, never a person; an attitude, not an outcome.');"
					+ "INSERT INTO \"Motivation\" VALUES(190,'In order to succeed you must fail, so that you know what not to do the next time.');"
					+ "INSERT INTO \"Motivation\" VALUES(191,'You don’t drown by falling in the water. You drown by staying there.');"
					+ "INSERT INTO \"Motivation\" VALUES(192,'What would you attempt to do if you knew you could not fail?');"
					+ "INSERT INTO \"Motivation\" VALUES(193,'The greatest barrier to success is the fear of failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(194,'Most people fail, not because of lack of desire, but, because of lack of commitment.');"
					+ "INSERT INTO \"Motivation\" VALUES(195,'You must have long-range goals to keep you from being frustrated by short-range failures.');"
					+ "INSERT INTO \"Motivation\" VALUES(196,'You may have to fight a battle more than once to win it.');"
					+ "INSERT INTO \"Motivation\" VALUES(197,'If you chase two rabbits, both will escape.');"
					+ "INSERT INTO \"Motivation\" VALUES(198,'It’s not the load that breaks you down; it’s the way you carry it.');"
					+ "INSERT INTO \"Motivation\" VALUES(199,'A goal without a plan is just a wish.');"
					+ "INSERT INTO \"Motivation\" VALUES(200,'Nothing can stop the man with the right mental attitude from achieving his goal; nothing on earth can help the man with the wrong mental attitude.');"
					+ "INSERT INTO \"Motivation\" VALUES(201,'If you want to accomplish anything in life, you can’t just sit back and hope it will happen. You’ve got to make it happen.');"
					+ "INSERT INTO \"Motivation\" VALUES(202,'You must have long-range goals to keep you from being frustrated by short-range failures.');"
					+ "INSERT INTO \"Motivation\" VALUES(203,'If what you are doing is not moving you towards your goals, then it’s moving you away from your goals.');"
					+ "INSERT INTO \"Motivation\" VALUES(204,'People with clear, written goals, accomplish far more in a shorter period of time than people without them could ever imagine.');"
					+ "INSERT INTO \"Motivation\" VALUES(205,'You cannot expect to achieve new goals or move beyond your present circumstances unless you change.');"
					+ "INSERT INTO \"Motivation\" VALUES(206,'This one step – choosing a goal and sticking to it – changes everything.');"
					+ "INSERT INTO \"Motivation\" VALUES(207,'Your goals, minus your doubts, equal your reality.');"
					+ "INSERT INTO \"Motivation\" VALUES(208,'Habit is habit and not to be flung out of the window by any man, but coaxed downstairs a step at a time.');"
					+ "INSERT INTO \"Motivation\" VALUES(209,'Motivation is what gets you started. Habit is what keeps you going.');"
					+ "INSERT INTO \"Motivation\" VALUES(210,'A habit is something you can do without thinking – which is why most of us have so many of them.');"
					+ "INSERT INTO \"Motivation\" VALUES(211,'The unfortunate thing about this world is that good habits are so much easier to give up than bad ones.');"
					+ "INSERT INTO \"Motivation\" VALUES(212,'The easier it is to do, the harder it is to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(213,'The chains of habit are generally too small to be felt until they are too strong to be broken.');"
					+ "INSERT INTO \"Motivation\" VALUES(214,'Bad habits are like a comfortable bed, easy to get into, but hard to get out of.');"
					+ "INSERT INTO \"Motivation\" VALUES(215,'Bad habits are like chains that are too light to feel until they are too heavy to carry.');"
					+ "INSERT INTO \"Motivation\" VALUES(216,'First we form habits; then they form us. Conquer your bad habits or they will conquer you.');"
					+ "INSERT INTO \"Motivation\" VALUES(217,'Habit is second nature, or rather, ten times nature.');"
					+ "INSERT INTO \"Motivation\" VALUES(218,'The successful person has the habit of doing the things failures don’t like to do. They don’t like doing them either necessarily. But their disliking is subordinated to the strength of their purpose.');"
					+ "INSERT INTO \"Motivation\" VALUES(219,'Success is the sum of small efforts, repeated day in and day out.');"
					+ "INSERT INTO \"Motivation\" VALUES(220,'You will never change your life until you change something you do daily.');"
					+ "INSERT INTO \"Motivation\" VALUES(221,'Nothing worthwhile comes easily. Work, continuous work and hard work, is the only way to accomplish results that last.');"
					+ "INSERT INTO \"Motivation\" VALUES(222,'You can’t cross a sea merely by standing and staring at the water.');"
					+ "INSERT INTO \"Motivation\" VALUES(223,'The starting point of all achievement is desire. Keep this constantly in mind. Weak desires bring weak results, just as a small amount of fire makes a small amount of heat.');"
					+ "INSERT INTO \"Motivation\" VALUES(224,'Nothing will work unless you do.');"
					+ "INSERT INTO \"Motivation\" VALUES(225,'You have to put in many, many, many tiny efforts that nobody sees or appreciates before you achieve anything worthwhile.');"
					+ "INSERT INTO \"Motivation\" VALUES(226,'The only place where success comes before work is in the dictionary.');"
					+ "INSERT INTO \"Motivation\" VALUES(227,'The only thing that ever sat its way to success was a hen.');"
					+ "INSERT INTO \"Motivation\" VALUES(228,'After climbing a great hill, one only finds that there are many more hills to climb.');"
					+ "INSERT INTO \"Motivation\" VALUES(229,'You may have to fight a battle more than once to win it.');"
					+ "INSERT INTO \"Motivation\" VALUES(230,'Most of the important things in the world have been accomplished by people who have kept on trying when there seemed to be no hope at all.');"
					+ "INSERT INTO \"Motivation\" VALUES(231,'All great achievements require time.');"
					+ "INSERT INTO \"Motivation\" VALUES(232,'I know the price of success: dedication, hard work and an unremitting devotion to the things you want to see happen.');"
					+ "INSERT INTO \"Motivation\" VALUES(233,'There are no secrets to success. It is the result of preparation, hard work, and learning from failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(234,'Success is dependent upon the glands – sweat glands.');"
					+ "INSERT INTO \"Motivation\" VALUES(235,'We tend to forget that happiness doesn’t come as a result of getting something we don’t have, but rather of recognizing and appreciating what we do have.');"
					+ "INSERT INTO \"Motivation\" VALUES(236,'To be happy, drop the words ‘if only’ and substitute instead the words ‘next time’.');"
					+ "INSERT INTO \"Motivation\" VALUES(237,'Searching for that big happy moment in life, how many special little moments will we let pass us by?');"
					+ "INSERT INTO \"Motivation\" VALUES(238,'Our health always seems much more valuable after we lose it.');"
					+ "INSERT INTO \"Motivation\" VALUES(239,'A man’s health can be judged by which he takes two at a time â€@ pills or stairs.');"
					+ "INSERT INTO \"Motivation\" VALUES(240,'Living a healthy lifestyle will only deprive you of poor health, lethargy, and fat.');"
					+ "INSERT INTO \"Motivation\" VALUES(241,'Health is a state of complete physical, mental and social well-being, and not merely the absence of disease or infirmity.');"
					+ "INSERT INTO \"Motivation\" VALUES(242,'Life expectancy would grow by leaps and bounds if green vegetables smelled as good as bacon.');"
					+ "INSERT INTO \"Motivation\" VALUES(243,'Everyone has problems; some are just better at hiding them.');"
					+ "INSERT INTO \"Motivation\" VALUES(244,'The leading cause of death among fashion models is falling through street grates.');"
					+ "INSERT INTO \"Motivation\" VALUES(245,'Even if you fall on your face, you’re still moving forward.');"
					+ "INSERT INTO \"Motivation\" VALUES(246,'Worry is as useless as a handle on a snowball.');"
					+ "INSERT INTO \"Motivation\" VALUES(247,'If it weren’t for the fact that the TV set and the refrigerator are so far apart, some of us wouldn’t get any exercise at all.');"
					+ "INSERT INTO \"Motivation\" VALUES(248,'Too many people confine their exercise to jumping to conclusions, running up bills, stretching the truth, bending over backwards, lying down on the job, sidestepping responsibility and pushing their luck.');"
					+ "INSERT INTO \"Motivation\" VALUES(249,'People often say that motivation doesn’t last. Well, neither does bathing – that’s why we recommend it daily.');"
					+ "INSERT INTO \"Motivation\" VALUES(250,'If hunger is not the problem, then eating is not the solution.');"
					+ "INSERT INTO \"Motivation\" VALUES(251,'Bigger snacks mean bigger slacks.');"
					+ "INSERT INTO \"Motivation\" VALUES(252,'Can it be a mistake that ‘STRESSED’ is ‘DESSERTS’ spelled backwards?');"
					+ "INSERT INTO \"Motivation\" VALUES(253,'A moment on the lips, forever on the hips.');"
					+ "INSERT INTO \"Motivation\" VALUES(254,'What you eat in private will show up in public.');"
					+ "INSERT INTO \"Motivation\" VALUES(255,'Don’t dig your grave with your own knife and fork.');"
					+ "INSERT INTO \"Motivation\" VALUES(256,'Don’t eat anything your great-great grandmother wouldn’t recognize as food.');"
					+ "INSERT INTO \"Motivation\" VALUES(257,'I’ve been on a diet for two weeks and all I’ve lost is fourteen days.');"
					+ "INSERT INTO \"Motivation\" VALUES(258,'The second day of a diet is always easier than the first. By the second day you’re off it.');"
					+ "INSERT INTO \"Motivation\" VALUES(259,'At the end of every diet, the path curves back to the trough.');"
					+ "INSERT INTO \"Motivation\" VALUES(260,'I tried every diet in the book. I tried some that weren’t in the book. I tried eating the book. It tasted better than most of the diets.');"
					+ "INSERT INTO \"Motivation\" VALUES(261,'Probably nothing in the world arouses more false hopes than the first four hours of a diet.');"
					+ "INSERT INTO \"Motivation\" VALUES(262,'The road to success is dotted with many tempting parking places.');"
					+ "INSERT INTO \"Motivation\" VALUES(263,'We are torn between a craving to know and the despair of having known.');"
					+ "INSERT INTO \"Motivation\" VALUES(264,'Most people want to be delivered from temptation but would like it to keep in touch.');"
					+ "INSERT INTO \"Motivation\" VALUES(265,'If you believe everything you read, you better not read.');"
					+ "INSERT INTO \"Motivation\" VALUES(266,'It’s rough to go through life with your contents looking as if they settled during shipping.');"
					+ "INSERT INTO \"Motivation\" VALUES(267,'The older you get, the tougher it is to lose weight because by then, your body and your fat are really good friends.');"
					+ "INSERT INTO \"Motivation\" VALUES(268,'I am realistic – I expect miracles.');"
					+ "INSERT INTO \"Motivation\" VALUES(269,'Time is a circus, always packing up and moving away.');"
					+ "INSERT INTO \"Motivation\" VALUES(270,'I don’t jog. If I die I want to be sick.');"
					+ "INSERT INTO \"Motivation\" VALUES(271,'My doctor recently told me that jogging could add years to my life. I think he was right. I feel ten years older already.');"
					+ "INSERT INTO \"Motivation\" VALUES(272,'A pedestrian is someone who thought there were a couple of gallons left in the tank.');"
					+ "INSERT INTO \"Motivation\" VALUES(273,'A dog is one of the remaining reasons why some people can be persuaded to go for a walk.');"
					+ "INSERT INTO \"Motivation\" VALUES(274,'The Americans never walk. In winter too cold and in summer too hot.');"
					+ "INSERT INTO \"Motivation\" VALUES(275,'I have to exercise in the morning before my brain figures out what I’m doing.');"
					+ "INSERT INTO \"Motivation\" VALUES(276,'If your dog is fat, you’re not getting enough exercise.');"
					+ "INSERT INTO \"Motivation\" VALUES(277,'Another good reducing exercise consists in placing both hands against the table edge and pushing back.');"
					+ "INSERT INTO \"Motivation\" VALUES(278,'Whenever I feel like exercise, I lie down until the feeling passes.');"
					+ "INSERT INTO \"Motivation\" VALUES(279,'I consider exercise vulgar. It makes people smell.');"
					+ "INSERT INTO \"Motivation\" VALUES(280,'I am pushing sixty. That is enough exercise for me.');"
					+ "INSERT INTO \"Motivation\" VALUES(281,'Exercise is a dirty word. Every time I hear it, I wash my mouth out with chocolate.');"
					+ "INSERT INTO \"Motivation\" VALUES(282,'When I buy cookies I eat just four and throw the rest away. But first I spray them with Raid so I won’t dig them out of the garbage later. Be careful, though, because that Raid really doesn’t taste that bad.');"
					+ "INSERT INTO \"Motivation\" VALUES(283,'Aerobics: a series of strenuous exercises which help convert fats, sugars, and starches into aches, pains, and cramps.');"
					+ "INSERT INTO \"Motivation\" VALUES(284,'I bought a talking refrigerator that said @Oink@ every time I opened the door. It made me hungry for pork chops.');"
					+ "INSERT INTO \"Motivation\" VALUES(285,'Obesity is really widespread.');"
					+ "INSERT INTO \"Motivation\" VALUES(286,'If I can’t have too many truffles I’ll do without.');"
					+ "INSERT INTO \"Motivation\" VALUES(287,'The only way to lose weight is to check it as airline baggage.');"
					+ "INSERT INTO \"Motivation\" VALUES(288,'Fat is not a moral problem. It’s an oral problem.');"
					+ "INSERT INTO \"Motivation\" VALUES(289,'A waist is a terrible thing to mind.');"
					+ "INSERT INTO \"Motivation\" VALUES(290,'I am not a glutton – I am an explorer of food.');"
					+ "INSERT INTO \"Motivation\" VALUES(291,'Flabbergasted, adj. Appalled over how much weight you have gained.');"
					+ "INSERT INTO \"Motivation\" VALUES(292,'I’m in shape. Round is a shape… isn’t it?');"
					+ "INSERT INTO \"Motivation\" VALUES(293,'A balanced diet is a cookie in each hand.');"
					+ "INSERT INTO \"Motivation\" VALUES(294,'I’m not overweight. I’m just nine inches too short.');"
					+ "INSERT INTO \"Motivation\" VALUES(295,'Forget love… I’d rather fall in chocolate!');"
					+ "INSERT INTO \"Motivation\" VALUES(296,'I’ve been on a constant diet for the last two decades. I’ve lost a total of 789 pounds. By all accounts, I should be hanging from a charm bracelet.');"
					+ "INSERT INTO \"Motivation\" VALUES(297,'My advice if you insist on slimming: Eat as much as you like – just don’t swallow it.');"
					+ "INSERT INTO \"Motivation\" VALUES(298,'I have a great diet. You’re allowed to eat anything you want, but you must eat it with naked fat people.');"
					+ "INSERT INTO \"Motivation\" VALUES(299,'If hunger is not the problem, then eating is not the solution.');"
					+ "INSERT INTO \"Motivation\" VALUES(300,'Bigger snacks mean bigger slacks.');"
					+ "INSERT INTO \"Motivation\" VALUES(301,'If food is your best friend, it’s also your worst enemy.@ ~Edward @Grandpa');"
					+ "INSERT INTO \"Motivation\" VALUES(302,'Can it be a mistake that â€˜STRESSED’ is â€˜DESSERTS’ spelled backwards?');"
					+ "INSERT INTO \"Motivation\" VALUES(303,'A moment on the lips, forever on the hips.');"
					+ "INSERT INTO \"Motivation\" VALUES(304,'What you eat in private will show up in public.');"
					+ "INSERT INTO \"Motivation\" VALUES(305,'Processed foods not only extend the shelf life, but they extend the waistline as well.');"
					+ "INSERT INTO \"Motivation\" VALUES(306,'Nothing tastes as good as feeling thin feels.');"
					+ "INSERT INTO \"Motivation\" VALUES(307,'Don’t dig your grave with your own knife and fork.');"
					+ "INSERT INTO \"Motivation\" VALUES(308,'Don’t eat anything your great-great grandmother wouldn’t recognize as food.');"
					+ "INSERT INTO \"Motivation\" VALUES(309,'A man too busy to take care of his health is like a mechanic too busy to take care of his tools.');"
					+ "INSERT INTO \"Motivation\" VALUES(310,'Our food should be our medicine and our medicine should be our food.');"
					+ "INSERT INTO \"Motivation\" VALUES(311,'No matter who you are, no matter what you do, you absolutely, positively do have the power to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(312,'Put all excuses aside and remember this: YOU are capable.');"
					+ "INSERT INTO \"Motivation\" VALUES(313,'We can do anything we want as long as we stick to it long enough.');"
					+ "INSERT INTO \"Motivation\" VALUES(314,'Do what you can, with what you have, where you are.');"
					+ "INSERT INTO \"Motivation\" VALUES(315,'Look within!… The secret is inside you.');"
					+ "INSERT INTO \"Motivation\" VALUES(316,'Act as if it were impossible to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(317,'Use your imagination not to scare yourself to death, but to inspire yourself to life.');"
					+ "INSERT INTO \"Motivation\" VALUES(318,'Within you right now is the power to do things you never dreamed possible. This power becomes available to you just as soon as you can change your beliefs.');"
					+ "INSERT INTO \"Motivation\" VALUES(319,'Where you start is not as important as where you finish.');"
					+ "INSERT INTO \"Motivation\" VALUES(320,'Act as if everything you do makes a difference. It does.');"
					+ "INSERT INTO \"Motivation\" VALUES(321,'When we stop judging ourselves, we stop judging other people. When we start loving ourselves, we start loving other people.');"
					+ "INSERT INTO \"Motivation\" VALUES(322,'Observe everything. Judge nothing.');"
					+ "INSERT INTO \"Motivation\" VALUES(323,'Minds are like parachutes. They only function properly when open.');"
					+ "INSERT INTO \"Motivation\" VALUES(324,'I would rather have a mind opened by wonder than closed by belief.');"
					+ "INSERT INTO \"Motivation\" VALUES(325,'Don’t let what others think decide who you are.');"
					+ "INSERT INTO \"Motivation\" VALUES(326,'People are very open-minded about new things – as long as they’re exactly like the old ones.');"
					+ "INSERT INTO \"Motivation\" VALUES(327,'Courage is not the absence of fear, but rather the judgment that something else is more important than fear.');"
					+ "INSERT INTO \"Motivation\" VALUES(328,'When the only tool you own is a hammer, every problem begins to resemble a nail.');"
					+ "INSERT INTO \"Motivation\" VALUES(329,'What we achieve inwardly will change outer reality.');"
					+ "INSERT INTO \"Motivation\" VALUES(330,'If you’re coasting, you’re either losing momentum or else you’re headed downhill.');"
					+ "INSERT INTO \"Motivation\" VALUES(331,'I hated every minute of the training, but I said, @Don’t quit. Suffer now and live the rest of your life as a champion.');"
					+ "INSERT INTO \"Motivation\" VALUES(332,'How different our lives are when we really know what is deeply important to us, and keeping that picture in mind, we manage ourselves each day to be and to do what really matters most.');"
					+ "INSERT INTO \"Motivation\" VALUES(333,'You have to have the bad days to appreciate the good ones.');"
					+ "INSERT INTO \"Motivation\" VALUES(334,'The best way to predict the future is to invent it.');"
					+ "INSERT INTO \"Motivation\" VALUES(335,'I am not discouraged because every wrong attempt discarded is another step forward.');"
					+ "INSERT INTO \"Motivation\" VALUES(336,'It’s not the load that breaks you down, it’s the way you carry it.');"
					+ "INSERT INTO \"Motivation\" VALUES(337,'The longest journey of any person is the journey inward.');"
					+ "INSERT INTO \"Motivation\" VALUES(338,'Follow effective action with quiet reflection. From the quiet reflection will come even more effective action.');"
					+ "INSERT INTO \"Motivation\" VALUES(339,'Too many people overvalue what they are not and undervalue what they are.');"
					+ "INSERT INTO \"Motivation\" VALUES(340,'Don’t compromise yourself. You are all you’ve got.');"
					+ "INSERT INTO \"Motivation\" VALUES(341,'You are the only problem you will ever have and you are the only solution.');"
					+ "INSERT INTO \"Motivation\" VALUES(342,'People often say that motivation doesn’t last. Well, neither does bathing – that’s why we recommend it daily.');"
					+ "INSERT INTO \"Motivation\" VALUES(343,'Motivation is a fire from within. If someone else tries to light that fire under you, chances are it will burn very briefly.');"
					+ "INSERT INTO \"Motivation\" VALUES(344,'Motivation is like food for the brain. You cannot get enough in one sitting. It needs continual and regular top up’s.');"
					+ "INSERT INTO \"Motivation\" VALUES(345,'Motivation is what gets you started. Habit is what keeps you going.');"
					+ "INSERT INTO \"Motivation\" VALUES(346,'In order to change we must be sick and tired of being sick and tired.');"
					+ "INSERT INTO \"Motivation\" VALUES(347,'The past is history. The future, a mystery. The here and now is a gift. That is why it’s called the present.');"
					+ "INSERT INTO \"Motivation\" VALUES(348,'Let go of what’s gone.');"
					+ "INSERT INTO \"Motivation\" VALUES(349,'None of us can change our yesterdays but all of us can change our tomorrows.');"
					+ "INSERT INTO \"Motivation\" VALUES(350,'Never look back unless you are planning to go that way.');"
					+ "INSERT INTO \"Motivation\" VALUES(351,'Never limit your view of life by any past experience.');"
					+ "INSERT INTO \"Motivation\" VALUES(352,'The past is a foreign country; they do things differently there.');"
					+ "INSERT INTO \"Motivation\" VALUES(353,'The past is behind, learn from it. The future is ahead, prepare for it. The present is here, live it.');"
					+ "INSERT INTO \"Motivation\" VALUES(354,'It’s never too late—in fiction or in life—to revise.');"
					+ "INSERT INTO \"Motivation\" VALUES(355,'Don’t dwell on what went wrong. Instead, focus on what to do next. Spend your energies on moving forward toward finding the answer.');"
					+ "INSERT INTO \"Motivation\" VALUES(356,'In three words I can sum up everything I’ve learned about life. It goes on.');"
					+ "INSERT INTO \"Motivation\" VALUES(357,'You don’t drown by falling in the water. You drown by staying there.');"
					+ "INSERT INTO \"Motivation\" VALUES(358,'It’s not whether you get knocked down; it’s whether you get up.');"
					+ "INSERT INTO \"Motivation\" VALUES(359,'Twenty years from now, you’ll be more disappointed by the things you didn’t do, than the ones you did.');"
					+ "INSERT INTO \"Motivation\" VALUES(360,'The future is always beginning now.');"
					+ "INSERT INTO \"Motivation\" VALUES(361,'Fears over tomorrow and regrets over yesterday are twin thieves that rob us of the moment.');"
					+ "INSERT INTO \"Motivation\" VALUES(362,'You can clutch the past so tightly to your chest that it leaves your arms too full to embrace the present.');"
					+ "INSERT INTO \"Motivation\" VALUES(363,'You can’t have a better tomorrow if you are thinking about yesterday all the time.');"
					+ "INSERT INTO \"Motivation\" VALUES(364,'Perfectionism doesn’t make you feel perfect. It makes you feel inadequate.');"
					+ "INSERT INTO \"Motivation\" VALUES(365,'You don’t have to be perfect in order to be successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(366,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(367,'Strive for excellence, not perfection, because we don’t live in a perfect world.');"
					+ "INSERT INTO \"Motivation\" VALUES(368,'Take time to deliberate, but when the time for action has arrived, stop thinking and go in.');"
					+ "INSERT INTO \"Motivation\" VALUES(369,'Plans are nothing; planning is everything.');"
					+ "INSERT INTO \"Motivation\" VALUES(370,'A good plan is like a road map: it shows the final destination and usually the best way to get there.');"
					+ "INSERT INTO \"Motivation\" VALUES(371,'It takes as much energy to wish as it does to plan.');"
					+ "INSERT INTO \"Motivation\" VALUES(372,'When defeat comes, accept it as a signal that your plans are not sound, rebuild those plans, and set sail once more toward your coveted goal.');"
					+ "INSERT INTO \"Motivation\" VALUES(373,'Proper preparation prevents poor performance.');"
					+ "INSERT INTO \"Motivation\" VALUES(374,'A goal without a plan is just a wish.');"
					+ "INSERT INTO \"Motivation\" VALUES(375,'There are no secrets to success. It is the result of preparation, hard work, and learning from failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(376,'Planning is bringing the future into the present so that you can do something about it now');"
					+ "INSERT INTO \"Motivation\" VALUES(377,'When defeat comes, accept it as a signal that your plans are not sound, rebuild those plans, and set sail once more toward your coveted goal.');"
					+ "INSERT INTO \"Motivation\" VALUES(378,'Let our advance worrying become advance thinking and planning');"
					+ "INSERT INTO \"Motivation\" VALUES(379,'Tomorrow is often the busiest day of the week.');"
					+ "INSERT INTO \"Motivation\" VALUES(380,'Now is the operative word. Everything you put in your way is just a method of putting off the hour when you could actually be doing your dream. You don’t need endless time and perfect conditions. Do it now. Do it today. Do it for twenty minutes and watch your heart start beating.');"
					+ "INSERT INTO \"Motivation\" VALUES(381,'The future is always beginning now.');"
					+ "INSERT INTO \"Motivation\" VALUES(382,'It’s never too late â€@ in fiction or in life â€@ to revise.');"
					+ "INSERT INTO \"Motivation\" VALUES(383,'The past is behind, learn from it. The future is ahead, prepare for it. The present is here, live it.');"
					+ "INSERT INTO \"Motivation\" VALUES(384,'Fears over tomorrow and regrets over yesterday are twin thieves that rob us of the moment.');"
					+ "INSERT INTO \"Motivation\" VALUES(385,'Procrastination is the fear of success. People procrastinate because they are afraid of the success that they know will result if they move ahead now. Because success is heavy, carries a responsibility with it, it is much easier to procrastinate and live on the ‘someday I’ll’ philosophy.');"
					+ "INSERT INTO \"Motivation\" VALUES(386,'Someday is not a day of the week.');"
					+ "INSERT INTO \"Motivation\" VALUES(387,'Tomorrow is often the busiest day of the week.');"
					+ "INSERT INTO \"Motivation\" VALUES(388,'Procrastination is the thief of time.');"
					+ "INSERT INTO \"Motivation\" VALUES(389,'You don’t have to be great to start, but you have to start to be great.');"
					+ "INSERT INTO \"Motivation\" VALUES(390,'Now is the operative word. Everything you put in your way is just a method of putting off the hour when you could actually be doing your dream. You don’t need endless time and perfect conditions. Do it now. Do it today. Do it for twenty minutes and watch your heart start beating.');"
					+ "INSERT INTO \"Motivation\" VALUES(391,'The way to get started is to quit talking and begin doing.');"
					+ "INSERT INTO \"Motivation\" VALUES(392,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(393,'It takes as much energy to wish as it does to plan.');"
					+ "INSERT INTO \"Motivation\" VALUES(394,'Take time to deliberate, but when the time for action has arrived, stop thinking and go in.');"
					+ "INSERT INTO \"Motivation\" VALUES(395,'Move out of your comfort zone. You can only grow if you are willing to feel awkward and uncomfortable when you try something new.');"
					+ "INSERT INTO \"Motivation\" VALUES(396,'The truth is that our finest moments are most likely to occur when we are feeling deeply uncomfortable, unhappy, or unfulfilled. For it is only in such moments, propelled by our discomfort, that we are likely to step out of our ruts and start searching for different ways or truer answers.');"
					+ "INSERT INTO \"Motivation\" VALUES(397,'No amount of security is worth the suffering of a life chained to a routine that has killed your dreams.');"
					+ "INSERT INTO \"Motivation\" VALUES(398,'We must be the change we wish to see in the world.');"
					+ "INSERT INTO \"Motivation\" VALUES(399,'When you blame others, you give up your power to change.');"
					+ "INSERT INTO \"Motivation\" VALUES(400,'Quite often we change our jobs, friends and spouses instead of changing ourselves.');"
					+ "INSERT INTO \"Motivation\" VALUES(401,'You must take personal responsibility. You cannot change the circumstances, the seasons, or the wind, but you can change yourself.');"
					+ "INSERT INTO \"Motivation\" VALUES(402,'Everyone thinks of changing the world, but no one thinks of changing himself.');"
					+ "INSERT INTO \"Motivation\" VALUES(403,'It is not only what we do, but what we do not do, that we are held responsible.');"
					+ "INSERT INTO \"Motivation\" VALUES(404,'The greatest lie you can make is to lie to yourself.');"
					+ "INSERT INTO \"Motivation\" VALUES(405,'If I am not good to myself, how can I expect anyone else to be?');"
					+ "INSERT INTO \"Motivation\" VALUES(406,'There are two primary choices in life: to accept conditions as they exist, or accept the responsibility for changing them.');"
					+ "INSERT INTO \"Motivation\" VALUES(407,'If I’d known I was going to live this long, I’d have taken better care of myself.');"
					+ "INSERT INTO \"Motivation\" VALUES(408,'Learn to relax. Your body is precious, as it houses your mind and spirit. Inner peace begins with a relaxed body.');"
					+ "INSERT INTO \"Motivation\" VALUES(409,'Take care of your body. It’s the only place you have to live.');"
					+ "INSERT INTO \"Motivation\" VALUES(410,'All blame is a waste of time. No matter how much fault you find with another, and regardless of how much you blame him, it will not change you.');"
					+ "INSERT INTO \"Motivation\" VALUES(411,'Every human being is the author of his own health or disease.');"
					+ "INSERT INTO \"Motivation\" VALUES(412,'It is easy to dodge our responsibilities, but we cannot dodge the consequences of dodging our responsibilities.');"
					+ "INSERT INTO \"Motivation\" VALUES(413,'If it’s never our fault, we can’t take responsibility for it. If we can’t take responsibility for it, we’ll always be its victim.');"
					+ "INSERT INTO \"Motivation\" VALUES(414,'The best years of your life are the ones in which you decide your problems are your own. You do not blame them on your mother, the ecology, or the president. You realize that you control your own destiny.');"
					+ "INSERT INTO \"Motivation\" VALUES(415,'Take your life in your own hands, and what happens? A terrible thing: no one to blame.');"
					+ "INSERT INTO \"Motivation\" VALUES(416,'The strongest oak tree of the forest is not the one that is protected from the storm and hidden from the sun. It’s the one that stands in the open where it is compelled to struggle for its existence against the winds and rains and the scorching sun.');"
					+ "INSERT INTO \"Motivation\" VALUES(417,'Stand up to your obstacles and do something about them. You will find that they haven’t half the strength you think they have.');"
					+ "INSERT INTO \"Motivation\" VALUES(418,'You are braver than you believe, stronger than you seem, and smarter than you think.');"
					+ "INSERT INTO \"Motivation\" VALUES(419,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(420,'The successful person has the habit of doing the things failures don’t like to do. They don’t like doing them either necessarily. But their disliking is subordinated to the strength of their purpose.');"
					+ "INSERT INTO \"Motivation\" VALUES(421,'To succeed you must first improve, to improve you must first practice, to practice you must first learn, to learn you must first fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(422,'Success consists of getting up just one more time than you fall.');"
					+ "INSERT INTO \"Motivation\" VALUES(423,'I don’t know the key to success but the key to failure is trying to please everybody.');"
					+ "INSERT INTO \"Motivation\" VALUES(424,'Success is never certain. Failure is never final.');"
					+ "INSERT INTO \"Motivation\" VALUES(425,'If at first you don’t succeed, you are running about average.');"
					+ "INSERT INTO \"Motivation\" VALUES(426,'The most important key to achieving great success is to decide upon your goal and launch, get started, take action, move.');"
					+ "INSERT INTO \"Motivation\" VALUES(427,'Success is the sum of small efforts, repeated day in and day out.');"
					+ "INSERT INTO \"Motivation\" VALUES(428,'It is our attitude at the beginning of a difficult task which, more than anything else, will affect its successful outcome.');"
					+ "INSERT INTO \"Motivation\" VALUES(429,'I know the price of success: dedication, hard work and an unremitting devotion to the things you want to see happen.');"
					+ "INSERT INTO \"Motivation\" VALUES(430,'There are no secrets to success. It is the result of preparation, hard work, and learning from failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(431,'Success is dependent upon the glands – sweat glands.');"
					+ "INSERT INTO \"Motivation\" VALUES(432,'When you get right down to the root of the meaning of the word succeed, you find that it simply means to follow through.');"
					+ "INSERT INTO \"Motivation\" VALUES(433,'Success is how high you bounce when you hit bottom.');"
					+ "INSERT INTO \"Motivation\" VALUES(434,'I’ve come to believe that all my past failure and frustration were actually laying the foundation for the understandings that have created the new level of living I now enjoy.');"
					+ "INSERT INTO \"Motivation\" VALUES(435,'In order to succeed, you must first be willing to fail.');"
					+ "INSERT INTO \"Motivation\" VALUES(436,'Remember, you only have to succeed the last time.');"
					+ "INSERT INTO \"Motivation\" VALUES(437,'Success is a staircase, not a doorway.');"
					+ "INSERT INTO \"Motivation\" VALUES(438,'In order to succeed you must fail, so that you know what not to do the next time.');"
					+ "INSERT INTO \"Motivation\" VALUES(439,'The greatest barrier to success is the fear of failure.');"
					+ "INSERT INTO \"Motivation\" VALUES(440,'You don’t have to be perfect in order to be successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(441,'Don’t wait until everything is just right. It will never be perfect. There will always be challenges, obstacles and less than perfect conditions. So what. Get started now. With each step you take, you will grow stronger and stronger, more and more skilled, more and more self-confident and more and more successful.');"
					+ "INSERT INTO \"Motivation\" VALUES(442,'The only place where success comes before work is in the dictionary.');"
					+ "INSERT INTO \"Motivation\" VALUES(443,'The only thing that ever sat its way to success was a hen.');"
					+ "INSERT INTO \"Motivation\" VALUES(444,'You may have to fight a battle more than once to win it.');"
					+ "INSERT INTO \"Motivation\" VALUES(445,'It is not the mountain we conquer, but ourselves.');"
					+ "INSERT INTO \"Motivation\" VALUES(446,'The key is not to prioritize what’s on your schedule, but to schedule your priorities.');"
					+ "INSERT INTO \"Motivation\" VALUES(447,'There is nothing noble in being superior to someone else. The true nobility is in being superior to your previous self.');"
					+ "INSERT INTO \"Motivation\" VALUES(448,'Don’t wait for someone to take you under their wing. Find a good wing and climb up underneath it.');"
					+ "INSERT INTO \"Motivation\" VALUES(449,'Keep away from people who try to belittle your ambitions. Small people always do that. The really great people make you feel that you, too, can become great.');"
					+ "INSERT INTO \"Motivation\" VALUES(450,'Whatever course you decide upon, there is always someone to tell you that you are wrong. There are always difficulties arising which tempt you to believe that your critics are right. To map out a course of action and follow it to an end requires courage.');"
					+ "INSERT INTO \"Motivation\" VALUES(451,'You can’t do it all yourself. Don’t be afraid to rely on others to help you accomplish your goals.');"
					+ "INSERT INTO \"Motivation\" VALUES(452,'There is no such thing as a self-made man. You will reach your goals only with the help of others.');"
					+ "INSERT INTO \"Motivation\" VALUES(453,'Our greatest battles are that with our own minds.');"
					+ "INSERT INTO \"Motivation\" VALUES(454,'Change your thoughts and you change your world.');"
					+ "INSERT INTO \"Motivation\" VALUES(455,'What consumes your mind controls your life.');"
					+ "INSERT INTO \"Motivation\" VALUES(456,'While we may not be able to control all that happens to us, we can control what happens inside us.');"
					+ "INSERT INTO \"Motivation\" VALUES(457,'It is not the mountain we conquer, but ourselves.');"
					+ "INSERT INTO \"Motivation\" VALUES(458,'Never say anything about yourself you do not want to come true.');"
					+ "INSERT INTO \"Motivation\" VALUES(459,'It’s not that some people have willpower and some don’t. It’s that some people are ready to change and others are not.');"
					+ "INSERT INTO \"Motivation\" VALUES(460,'Willpower can produce short-term change, but it creates constant internal stress because you haven’t dealt with the root cause.');"
					+ "INSERT INTO \"Motivation\" VALUES(461,'Knowing is not enough; we must apply. Willing is not enough; we must do.');"
					+ "INSERT INTO \"Motivation\" VALUES(462,'To worry is like rocking in a rocking chair. It gives you something to do, but gets you nowhere.');"
					+ "INSERT INTO \"Motivation\" VALUES(463,'Worry does not empty tomorrow of its sorrow; it empties today of its strength.');"
					+ "INSERT INTO \"Motivation\" VALUES(464,'Has worrying about tomorrow every changed it?');"
					+ "INSERT INTO \"Motivation\" VALUES(465,'Stop worrying about missed opportunities and start looking for new ones.');"
					+ "INSERT INTO \"Motivation\" VALUES(466,'You wouldn’t worry so much about what people really thought of you if you knew just how seldom they actually do.');"
					+ "INSERT INTO \"Motivation\" VALUES(467,'Worry is as useless as a handle on a snowball.');"
					+ "INSERT INTO \"Motivation\" VALUES(468,'Let our advance worrying become advance thinking and planning');"
					+ "INSERT INTO \"Motivation\" VALUES(469,'My life has been filled with terrible misfortune; most of which never happened.');";
			stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return (e.getClass().getName() + ": " + e.getMessage());
	    }
	    return ("Table created successfully");
	}
	
}
