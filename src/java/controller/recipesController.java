/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.FoodDAO;
import dal.IngredientDAO;
import dal.RecipeDAO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Food;
import model.Ingredient;
import model.Recipe;

/**
 *
 * @author AN
 */
public class recipesController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        action = action == null ? "" : request.getParameter("action");
        switch (action) {
            case "updateStatusFood":
                displayRecipesDetail(request, response);
                break;
            case "viewRecipesDetail":
                displayRecipesDetail(request, response);
                break;
            case "addRecipe":
                addRecipes(request, response);
                break;
            case "deletetRecipe":
                deletetRecipes(request, response);
                break;
           case "updateRecipe":
                updateRecipe(request, response);
                break;

        }
        displayMenu(request, response);
        request.getRequestDispatcher("kitchen.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void displayMenu(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String activeSection = "recipes";
        String mesg = "";
        FoodDAO fDAO = new FoodDAO();
        ArrayList<Food> listFood = new ArrayList<>();
        listFood.addAll(fDAO.getAll());
        IngredientDAO iDAO = new IngredientDAO();
        ArrayList<Ingredient> listIngredient = new ArrayList<>();
        listIngredient.addAll(iDAO.getAll());
        if (listFood == null) {
            mesg += "Không có món nào trong menu";
            request.setAttribute("activeSection", activeSection);
            request.setAttribute("mesg", mesg);
        } else {
            request.setAttribute("listFood", listFood);
            request.setAttribute("activeSection", activeSection);
            request.setAttribute("listIngredient", listIngredient);
        }
    }

    

    private void displayRecipesDetail(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        FoodDAO fDAO = new FoodDAO();
        int foodID = Integer.parseInt(request.getParameter("foodId"));
        RecipeDAO rDAO = new RecipeDAO();
        IngredientDAO iDAO = new IngredientDAO();
        Food selectedFood = fDAO.getById(foodID);
        ArrayList<Recipe> listRecipe = rDAO.getAllByFoodID(foodID);
        ArrayList<Ingredient> listIngredient = iDAO.getAll();
        request.setAttribute("selectedFood", selectedFood);
        request.setAttribute("listRecipe", listRecipe);
        request.setAttribute("listIngredient", listIngredient);
    }

    private void addRecipes(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int foodID = Integer.parseInt(request.getParameter("foodId"));
        int ingId = Integer.parseInt(request.getParameter("ingId"));
        Double amountNeeded = Double.parseDouble(request.getParameter("amountNeeded"));
        Recipe r = Recipe.builder()
                .foodID(foodID)
                .ingredientID(ingId)
                .amountNeeded(amountNeeded).build();
        RecipeDAO rDAO = new RecipeDAO();
        int check = rDAO.InsertNewRecipe(r);
        if (check > 0) {
            System.out.println("So dong da thay doi la: " + check);
        } else {
            System.out.println("Error");
        }
        displayRecipesDetail(request, response);
    }

    private void deletetRecipes(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int foodID = Integer.parseInt(request.getParameter("foodId"));
        int ingId = Integer.parseInt(request.getParameter("ingId"));
        Recipe r = Recipe.builder()
                .foodID(foodID)
                .ingredientID(ingId).build();
        RecipeDAO rDAO = new RecipeDAO();
        int check = rDAO.delete(r);
        if (check > 0) {
            System.out.println("So dong da thay doi la: " + check);
        } else {
            System.out.println("Error");
        }
        displayRecipesDetail(request, response);
    }

    private void updateRecipe(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        int foodID = Integer.parseInt(request.getParameter("foodId"));
        int ingId = Integer.parseInt(request.getParameter("ingId"));
        Double amountNeeded = Double.parseDouble(request.getParameter("amountNeeded"));
        Recipe r = Recipe.builder()
                .foodID(foodID)
                .ingredientID(ingId)
                .amountNeeded(amountNeeded).build();
        RecipeDAO rDAO = new RecipeDAO();
        int check = rDAO.update(r);
        if (check > 0) {
            System.out.println("So dong da thay doi la: " + check);
        } else {
            System.out.println("Error");
        }
        displayRecipesDetail(request, response);
    }
}
