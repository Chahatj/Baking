package com.chahat.baking.utils;

import android.util.Log;

import com.chahat.baking.Object.BakingObject;
import com.chahat.baking.Object.IngredientObject;
import com.chahat.baking.Object.RecipeStepObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chahat on 25/7/17.
 */

public class JsonUtils {


    public static List<BakingObject> getAllRecipeName(String response){
        try {

            JSONArray jsonArray = new JSONArray(response);

            List<BakingObject> bakingList = new ArrayList<>();

            for (int i=0;i<jsonArray.length();i++){

                BakingObject bakingObject = new BakingObject();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String id = jsonObject.getString("id");
                int servings = jsonObject.getInt("servings");
                String image = jsonObject.getString("image");
                bakingObject.setId(id);
                bakingObject.setName(name);
                bakingObject.setImage(image);
                bakingObject.setServings(servings);
                bakingList.add(bakingObject);
            }

            return bakingList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<RecipeStepObject> getRecipeSteps(String response,String id){
        try {
            JSONArray jsonArray = new JSONArray(response);

            List<RecipeStepObject> list = null;

            Log.d("InJSONUtil","before 1 for");

            for (int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String matchId = String.valueOf(jsonObject.getInt("id"));

                Log.d("InJSONUtil",matchId+"matchid");

                if (matchId.equals(id)){

                    list = new ArrayList<>();

                    JSONArray jsonArraySteps = jsonObject.getJSONArray("steps");

                    for (int j=0;j<jsonArraySteps.length();j++){

                        JSONObject jsonStep = jsonArraySteps.getJSONObject(j);

                        String stepId = String.valueOf(jsonStep.getInt("id"));
                        Log.d("InJSONUtil",stepId+"stepid");
                        String shortDescription = jsonStep.getString("shortDescription");
                        String description = jsonStep.getString("description");
                        String videoURL = jsonStep.getString("videoURL");
                        String thumbnailURL = jsonStep.getString("thumbnailURL");

                        RecipeStepObject recipeStepObject = new RecipeStepObject();
                        recipeStepObject.setRecipeId(id);
                        recipeStepObject.setId(stepId);
                        recipeStepObject.setShortDescription(shortDescription);
                        recipeStepObject.setDescription(description);
                        recipeStepObject.setVideoURL(videoURL);
                        recipeStepObject.setThumbnailURL(thumbnailURL);

                        list.add(recipeStepObject);
                    }
                }
            }

            return list;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static RecipeStepObject getNextRecipeStep(String response,String recipeId,String stepId){

        try {
            JSONArray jsonArray = new JSONArray(response);

            RecipeStepObject recipeStepObject = null;

            for (int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String matchId = jsonObject.getString("id");

                if (matchId.equals(recipeId)){

                    JSONArray jsonArraySteps = jsonObject.getJSONArray("steps");

                    for (int j=0;j<jsonArraySteps.length();j++){

                        JSONObject jsonStep = jsonArraySteps.getJSONObject(j);

                        String nextStepId = jsonStep.getString("id");

                        if (Integer.parseInt(nextStepId) == Integer.parseInt(stepId) + 1){

                            recipeStepObject = new RecipeStepObject();
                            String shortDescription = jsonStep.getString("shortDescription");
                            String description = jsonStep.getString("description");
                            String videoURL = jsonStep.getString("videoURL");
                            String thumbnailURL = jsonStep.getString("thumbnailURL");

                            recipeStepObject.setRecipeId(recipeId);
                            recipeStepObject.setId(nextStepId);
                            recipeStepObject.setShortDescription(shortDescription);
                            recipeStepObject.setDescription(description);
                            recipeStepObject.setVideoURL(videoURL);
                            recipeStepObject.setThumbnailURL(thumbnailURL);

                        }


                    }
                }
            }

            return recipeStepObject;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }


    public static List<IngredientObject> getIngredient(String response,String id){
        try {
            JSONArray jsonArray = new JSONArray(response);

            List<IngredientObject> list = null;

            for (int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String matchId = jsonObject.getString("id");

                if (matchId.equals(id)){

                    list = new ArrayList<>();

                    JSONArray jsonArraySteps = jsonObject.getJSONArray("ingredients");

                    for (int j=0;j<jsonArraySteps.length();j++){

                        JSONObject jsonStep = jsonArraySteps.getJSONObject(j);

                        String quantity = jsonStep.getString("quantity");
                        String measure = jsonStep.getString("measure");
                        String ingredient = jsonStep.getString("ingredient");

                        IngredientObject ingredientObject = new IngredientObject();
                        ingredientObject.setQuantity(quantity);
                        ingredientObject.setMeasure(measure);
                        ingredientObject.setIngredient(ingredient);

                        list.add(ingredientObject);
                    }
                }
            }

            return list;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static RecipeStepObject getPrevRecipeStep(String response,String recipeId, String id) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            RecipeStepObject recipeStepObject = null;

            for (int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String matchId = jsonObject.getString("id");

                if (matchId.equals(recipeId)){

                    JSONArray jsonArraySteps = jsonObject.getJSONArray("steps");

                    for (int j=0;j<jsonArraySteps.length();j++){

                        JSONObject jsonStep = jsonArraySteps.getJSONObject(j);

                        String nextStepId = jsonStep.getString("id");

                        if (Integer.parseInt(nextStepId) == Integer.parseInt(id) - 1){

                            recipeStepObject = new RecipeStepObject();
                            String shortDescription = jsonStep.getString("shortDescription");
                            String description = jsonStep.getString("description");
                            String videoURL = jsonStep.getString("videoURL");
                            String thumbnailURL = jsonStep.getString("thumbnailURL");

                            recipeStepObject.setRecipeId(recipeId);
                            recipeStepObject.setId(nextStepId);
                            recipeStepObject.setShortDescription(shortDescription);
                            recipeStepObject.setDescription(description);
                            recipeStepObject.setVideoURL(videoURL);
                            recipeStepObject.setThumbnailURL(thumbnailURL);

                        }


                    }
                }
            }

            return recipeStepObject;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
