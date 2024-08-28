package com.example.ormi5finalteam1.domain.grammar_example.dto;

import com.example.ormi5finalteam1.common.response.MultipleResponseDto;
import lombok.*;

import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
public class MultipleGrammarExampleResponseDto extends MultipleResponseDto<GrammarExampleDto> {
    public MultipleGrammarExampleResponseDto(List<GrammarExampleDto> data, int count) {
        super(data, count);
    }
}
