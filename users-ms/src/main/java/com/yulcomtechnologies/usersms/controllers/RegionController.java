package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.entities.Region;
import com.yulcomtechnologies.usersms.repositories.RegionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class RegionController {
    private final RegionRepository regionRepository;

    @GetMapping("/regions")
    public ResponseEntity<List<Region>> getRegions() {
        return ResponseEntity.ok(regionRepository.findAll());
    }
}
