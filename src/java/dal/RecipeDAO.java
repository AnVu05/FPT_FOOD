/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Recipe;

/**
 *
 * @author AN
 */
public class RecipeDAO extends DBContext {

    public ArrayList<Recipe> getAllByFoodID(int foodID) {
        ArrayList<Recipe> listRecipe = new ArrayList<>();
        connection = getConnection();
        String sql = "SELECT *\n"
                + "  FROM [dbo].[Recipe]\n"
                + "  Where foodID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, foodID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int recipeID = resultSet.getInt("recipeID");
                int ingredientID = resultSet.getInt("ingredientID");
                Double amountNeeded = resultSet.getDouble("amountNeeded");
                Recipe r = Recipe.builder()
                        .recipeID(recipeID)
                        .ingredientID(ingredientID)
                        .foodID(foodID)
                        .amountNeeded(amountNeeded).build();
                listRecipe.add(r);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listRecipe;
    }

    public int InsertNewRecipe(Recipe r) {
        int resultSet = 0;
        connection = getConnection();
        String sql = "INSERT INTO [dbo].[Recipe]\n"
                + "           ([foodID]\n"
                + "           ,[ingredientID]\n"
                + "           ,[amountNeeded])\n"
                + "     VALUES\n"
                + "           (?,?,?)";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, r.getFoodID());
            statement.setInt(2, r.getIngredientID());
            statement.setDouble(3, r.getAmountNeeded());
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    public int delete(Recipe r) {
        int resultSet = 0;
        connection = getConnection();
        String sql = "DELETE FROM [dbo].[Recipe]\n"
                + "      WHERE foodID = ? and ingredientID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, r.getFoodID());
            statement.setInt(2, r.getIngredientID());
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

    public int update(Recipe r) {
        int resultSet = 0;
        connection = getConnection();
        String sql = "UPDATE [dbo].[Recipe]\n"
                + "   SET \n"
                + "      [amountNeeded] = ?\n"
                + " WHERE foodID = ? and ingredientID = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, r.getAmountNeeded());
            statement.setInt(2, r.getFoodID());
            statement.setInt(3, r.getIngredientID());
            resultSet = statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return resultSet;
    }

}
