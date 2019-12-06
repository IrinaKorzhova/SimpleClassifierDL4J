package com.example.sweater;

import com.example.sweater.domain.Info;
import com.example.sweater.domain.IrisDto;
import com.example.sweater.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class GreetingController {
    private List<Info> info = Collections.singletonList(new Info(""));
    private List<Info> flowers = Collections.singletonList(new Info(""));

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

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String add(Map<String, Object> model,
                      @RequestParam(name = "lengthPetal") float lengthPetal,
                      @RequestParam(name = "widthPetal") float widthPetal,
                      @RequestParam(name = "lengthSepal") float lengthSepal,
                      @RequestParam(name = "widthSepal") float widthSepal) {

        flowers = classificationService.classifyFlower(new IrisDto(lengthPetal, widthPetal, lengthSepal, widthSepal));
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
