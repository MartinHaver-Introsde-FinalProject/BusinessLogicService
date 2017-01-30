package utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class RandomData {
	
	private String[] firstname = {"Nam", "Bac", "Thu", "Phuong", "Tien", "Tho", "Diep", "Long", "Linh", "Hung", "Thai", "Ba", "Giang"}; 
	private String[] lastname = {"Nguyen", "Pham", "Phan", "Vu", "Dinh", "Le", "Dao", "Ly", "Trinh"};
	
	private String[] meats = {"salmon", "tuna", "beef", "chicken", "duck", "goose", "ham", "hare", "lamb", "mutton", "pork", "rabbit", "turkey", "veal"};
	private String[] breakfasts = {"blueberries", "grapefruit", "strawberries", "peaches", "apples", "aranges", "bananas", "pineapple", "eggs", "yogurt", "coffee", "oatmeal", "chia seeds", "berries", "nuts", "green tea", "whey", "soy", "flaxseed"};
	private String[] vegetables = {"broccoli", "tomatoes", "cucumbers", "mushrooms", "onions", "peppers", "spinach", "asparagus", "bean", "potatoes", "rice", "carrot"};
	
	public String getRandomUsername(String firstname, String lastname) {
		String username = firstname.toLowerCase() + "." + lastname.toLowerCase();
		return username;
	}
	
	public String getRandomEmail(String username) {
		return username + "@gmail.com";
	}
	
	public double getRandomHeight() {
		double minX = 1.60;
		double maxX = 2.20;
		Random rand = new Random();
		double finalX = rand.nextDouble() * (maxX - minX) + minX;
		finalX = Double.parseDouble(new DecimalFormat("##.##").format(finalX));
		return finalX;
	}
	
	public double getRandomWeight() {
		double minX = 50;
		double maxX = 150;
		Random rand = new Random();
		double finalX = rand.nextDouble() * (maxX - minX) + minX;
		finalX = Double.parseDouble(new DecimalFormat("##.##").format(finalX));
		return finalX;
	}
	
	public String getRandomBreakfast() {
		int fnNumber = randBetween(0, (breakfasts.length - 1));
		return breakfasts[fnNumber];
	}
	public String getRandomMeat() {
		int fnNumber = randBetween(0, (meats.length - 1));
		return meats[fnNumber];
	}
	public String getRandomVegetable() {
		int fnNumber = randBetween(0, (vegetables.length - 1));
		return vegetables[fnNumber];
	}
	
	public String getRandomFirstName() {
		int fnNumber = randBetween(0, (firstname.length - 1));
		return firstname[fnNumber];
	}
	
	public String getRandomLastName() {
		int lnNumber = randBetween(0, (lastname.length - 1));
		return lastname[lnNumber];
	}
	
	public String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String date = dateFormat.format(new Date());
		return date;
	}
	
	public String getRandomTimestamp() {
        GregorianCalendar gc = new GregorianCalendar();
        
        int year = randBetween(2000, 2015);
        gc.set(gc.YEAR, year);
        
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        
        return gc.get(gc.DAY_OF_MONTH) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.YEAR);
    }
	
	public String getRandomBirthdate() {
        GregorianCalendar gc = new GregorianCalendar();
        
        int year = randBetween(1960, 2000);
        gc.set(gc.YEAR, year);
        
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        
        return gc.get(gc.DAY_OF_MONTH) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.YEAR);
    }

    public int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
