package ca.ualberta.cs.CMPUT301.chenlei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ESClient {
	// Http Connector
	private HttpClient httpclient = new DefaultHttpClient();

	// JSON Utilities
	private Gson gson = new Gson();

	// POST/GET Request
	HttpPost httpPost = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/recipes");
	HttpGet getRequest = new HttpGet("http://cmput301.softwareprocess.es:8080/testing/recipes/S4bRPFsuSwKUDSJImbCE2g?pretty=1");//S4bRPFsuSwKUDSJImbCE2g?pretty=1

	/**
	 * create a simple recipe
	 * @return
	 */
	private Recipe initializeRecipe() {

		Recipe r = new Recipe();
		r.setUser("Jason");
		r.setName("Burger");
		ArrayList<String> ingredients = new ArrayList<String>();
		ingredients.add("egg");
		ingredients.add("cheese");
		ingredients.add("flour");
		r.setIngredients(ingredients);
		r.setDirections("mix and bake");

		return r;
	}

	String getEntityContent(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        String output;
        System.err.println("Output from Server -> ");
        String json = "";
        while ((output = br.readLine()) != null) {
            System.err.println(output);
            json += output;
        }
        System.err.println("JSON:"+json);
        return json;
	}
	
	/**
	 * Consumes the Get operation of the service
	 */
	public void getRecipe(){
		try{

			getRequest.addHeader("Accept","application/json");

			HttpResponse response = httpclient.execute(getRequest);

			String status = response.getStatusLine().toString();
			System.out.println(status);

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server -> ");
			String json = getEntityContent(response);

			// We have to tell GSON what type we expect
			Type elasticSearchResponseType = new TypeToken<ElasticSearchResponse<Recipe>>(){}.getType();
			// Now we expect to get a Recipe response
			ElasticSearchResponse<Recipe> esResponse = gson.fromJson(json, elasticSearchResponseType);
			// We get the recipe from it!
			Recipe recipe = esResponse.getSource();
			System.out.println(recipe.toString());
			getRequest.releaseConnection();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Consumes the POST/Insert operation of the service
	 */
	public void insertRecipe(Recipe recipe){

		StringEntity stringentity = null;
		try {
			stringentity = new StringEntity(gson.toJson(recipe));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpPost.setHeader("Accept","application/json");

		httpPost.setEntity(stringentity);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String status = response.getStatusLine().toString();
		HttpEntity entity = response.getEntity();

		System.out.println(status);

		try {
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpPost.releaseConnection();
	}

	public void searchRecipes(String str) throws ClientProtocolException, IOException {
	    HttpGet searchRequest = new HttpGet("http://cmput301.softwareprocess.es:8080/testing/recipes/_search?pretty=1&q=" +
	            java.net.URLEncoder.encode(str,"UTF-8"));
	    searchRequest.setHeader("Accept","application/json");
	    HttpResponse response = httpclient.execute(searchRequest);
        String status = response.getStatusLine().toString();
        String json = getEntityContent(response);
        System.err.println(json);
        Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Recipe>>(){}.getType();
        ElasticSearchSearchResponse<Recipe> esResponse = gson.fromJson(json, elasticSearchSearchResponseType);
        System.err.println(esResponse);
        for (ElasticSearchResponse<Recipe> r : esResponse.getHits()) {
            Recipe recipe = r.getSource();
            System.err.println(recipe);
        }
	}
	
	// Main Test
	public static void main(String [] args){

		ESClient client = new ESClient();
		Recipe recipe = client.initializeRecipe();
		System.out.println("Recipe is -> "+ recipe.toString());
		client.insertRecipe(recipe);
		client.getRecipe();
		try {
		    client.searchRecipes("egg");
		} catch (Exception e) {
		    e.printStackTrace();
		}	
	}
}


