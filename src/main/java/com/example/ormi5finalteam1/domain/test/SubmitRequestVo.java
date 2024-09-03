package com.example.ormi5finalteam1.domain.test;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SubmitRequestVo {
    private final List<SubmitRequestDto> dtoList = new ArrayList<>();

    public void add(SubmitRequestDto dto){
        this.dtoList.add(dto);
    }
}
