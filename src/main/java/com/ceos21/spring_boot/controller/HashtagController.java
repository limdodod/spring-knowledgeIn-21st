package com.ceos21.spring_boot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/hashtag")
public class HashtagController {
    // 특정 hashtag를 포함한 글들을 보기 위함
    // post에 포함시킬지 고민

}
