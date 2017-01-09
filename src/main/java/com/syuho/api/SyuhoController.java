package com.syuho.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syuho.api.dto.ApiResponse;
import com.syuho.api.dto.SyuhoDto;
import com.syuho.api.dto.SyuhoItemDto;
import com.syuho.domain.model.syuho.Syuho;
import com.syuho.service.SyuhoService;
import com.syuho.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@RestController
@RequestMapping("/api/v1/syuhos")
@AllArgsConstructor
public class SyuhoController {

    private final SyuhoService syuhoService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<SyuhoDto>>> getAll() {
        List<Syuho> syuhoList = syuhoService.findAll();
        List<SyuhoDto> dtoList = parse(syuhoList);
        return new ResponseEntity<ApiResponse<List<SyuhoDto>>>(new ApiResponse<>(dtoList), HttpStatus.OK);
    }

    private List<SyuhoDto> parse(List<Syuho> syuho) {
        Map<String, List<SyuhoItemDto>> collect = syuho.stream()
                .map(s -> Pair.of(s.getWeek().toString(), 
                        new SyuhoItemDto(s.getBody(),
                                userService.findById(s.getUserId()).get().getUserName())))
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));

        return collect.entrySet().stream()
                .map(e -> new SyuhoDto(e.getKey(), e.getValue()))
                .collect(toList());
    }
}