package ca.ualberta.cs.CMPUT301.chenlei;

import java.util.ArrayList;

public class Recipe {
	private String user;
	private String name;
	private ArrayList<String> ingredients;
	private String directions;
	
	public Recipe(){
		
	}

	public Recipe(String user, String name, ArrayList<String> ingredients,
			String directions) {
		super();
		this.user = user;
		this.name = name;
		this.ingredients = ingredients;
		this.directions = directions;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	@Override
	public String toString() {
		return "Recipe [user=" + user + ", name=" + name + ", ingredients="
				+ ingredients + ", directions=" + directions + "]";
	}
	
}
