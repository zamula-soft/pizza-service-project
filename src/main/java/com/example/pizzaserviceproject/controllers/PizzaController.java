package com.example.pizzaserviceproject.controllers;


import com.example.pizzaserviceproject.models.pizza.Pizza;
import com.example.pizzaserviceproject.models.pizza.PizzaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizza")
@Api(value = "Pizza API", protocols = "http")
public class PizzaController {
    private static final Logger log = LoggerFactory.getLogger(PizzaController.class);

    @Autowired
    PizzaRepository pizzaRepository;

    public PizzaController() {
    }

    @ApiOperation(value = "Get list of all pizzas in database", response = Model.class, code = 200)
    @GetMapping
    public String getAllPizzas(Model model) { //model lets bind instance into template
        Iterable<Pizza> all = pizzaRepository.findAll();
        log.info(all.toString());
        model.addAttribute("pizzas", all);
        log.info(all.toString());
        return "pizza/list-pizza";
    }

    //    Search
    @GetMapping("/search")
    public String searchPizzas(@PathParam("name") String name, Model model) {
        List<Pizza> pizzaByName = pizzaRepository.findByNameContaining(name);
        List<Pizza> pizzaByDescription = pizzaRepository.findByDescriptionContaining(name);
        List<Pizza> resultList = new ArrayList<Pizza>();
        resultList.addAll(pizzaByName);
        resultList.addAll(pizzaByDescription);
        model.addAttribute("pizzas", resultList);
        log.info(pizzaByName.toString());
        return "pizza/list-pizza";

    }

    //    Add
    @GetMapping("/add")
    public String addPizza(Pizza pizza) {
        return "pizza/add-pizza";
    }


    @PostMapping
    public String addNewPizza(
            @Valid Pizza pizza,
            BindingResult result,
            Model model){
        if (result.hasErrors())
        {
            return "pizza/add-pizza";
        }
        pizzaRepository.save(pizza);
        return "redirect:/pizza";
    }


    //Delete
    @GetMapping("/delete/{id}")
    public String deletePizzaById(@PathVariable(name = "id") String id){
        log.info("Deleting pizza id: "+id);
        pizzaRepository.deleteById(id);
        return "redirect:/pizza";
    }


    //Edit
    @GetMapping("/edit/{id}")
    public String updatePizza(@PathVariable(name = "id") String id, Model model){
        log.info("Editing pizza id: "+id);
        Pizza pizza = pizzaRepository.findById(id).get();
        log.info("find: "+pizza);
        model.addAttribute("pizza", pizza);
        return "pizza/update-pizza";
    }

    @PostMapping("/update/{id}")
    public String updatePizzaModel(@PathVariable(name = "id") String id,
                                    @Valid Pizza pizza, //validate
                                    BindingResult result,
                                    Model model)
    {//result of validation
        log.info(pizza.toString());
        if (result.hasErrors())
        {
            pizza.setId(id);
            return "pizza/update-pizza";
        }
        pizzaRepository.save(pizza);
        return "redirect:/pizza";
    }

}
