package com.api.undo_school.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HomeController {
@GetMapping("/timezones")
public List<String> timezomes(){

    return ZoneId.getAvailableZoneIds()
            .stream()
            .sorted()
            .collect(Collectors.toList());
}


}
