package com.example.sweater;

import com.example.sweater.domain.IrisDto;
import com.example.sweater.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GreetingController {
    private String info = "";
    private String flowers = "First thing, please click on the train";

    public static Map<Integer, String> mapOfIris;

    static {
        mapOfIris = new HashMap<>();
        mapOfIris.put(0, "Iris setosa");
        mapOfIris.put(1, "Iris versicolor");
        mapOfIris.put(2, "Iris virginica");
    }

    @Autowired
    private ClassificationService classificationService;


    @GetMapping
    public String greeting(Map<String, Object> model) {

        model.put("flowers", flowers);
        model.put("info", info);
        return "main";
    }

    @PostMapping
    public String add(Map<String, Object> model) {

        flowers = classificationService.classifyFlower(null);
        model.put("flowers", flowers);
        model.put("info", info);
        return "main";
    }

    @GetMapping("train")
    public String train(Map<String, Object> model) {

        info = classificationService.trainNetwork();
        model.put("info", info);
        model.put("flowers", flowers);

        return "main";
    }
}
