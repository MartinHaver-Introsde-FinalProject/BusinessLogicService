package calculation;

public class Calculation {

	public double getIdealWeight(double height, int sex) {
		height = height * 100.0;
		switch (sex) {
		case 0:
			return (100.0 * 0.45359237) + ((height - 152.4) / 2.54) * (5 * 0.45359237);
		case 1:
			return (106.0 * 0.45359237) + ((height - 152.4) / 2.54) * (6 * 0.45359237);
		}
		return -1;
	}

	public double getBMI(double height, double weight) {
		return weight / (height * height);
	}

	public double getBMR(double height, double weight, int age, int sex) {
		height = height * 100.0;
		double bmr = 0;
		if (sex == 1)
			bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
		else if (sex == 0)
			bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
		return bmr;
	}
	
	public String getGoalName(double bmi){
		if (bmi <= 18.5)
			return "You should gain weight at rate 0.5kg per week.";
		else if (bmi > 18.5 && bmi <= 25)
			return "You should maintain weight.";
		else if (bmi > 25 && bmi <= 30)
			return "You should lose weight at rate 0.2kg per week.";
		else if (bmi > 25)
			return "You should lose weight at rate 0.5kg per week.";
		return null;
	}
	
	public double getCaloriesNeedForDailyActivity(double bmr) {
		return bmr * 1.2;
	}
	
	public double getFoodCaloriesNeedToMaintainTheWeight(double bmr) {
		return getCaloriesNeedForDailyActivity(bmr);
	}
	
	public double getCaloriesNeedToGainTheWeight(double bmr) {
		return getCaloriesNeedForDailyActivity(bmr) + 500;
	}
	
	public double getCaloriesNeedToLoseTheWeight(double bmr) {
		return getCaloriesNeedForDailyActivity(bmr) - 500;
	}
	
	public double suggestionCaloriesNeedFromFood(double bmr) {
		return getCaloriesNeedForDailyActivity(bmr) - 250;
	}
	
	public double getCaloriesGotToday(double foodCalories, double exerciseCalories) {
		return foodCalories - exerciseCalories;
	}
} 
