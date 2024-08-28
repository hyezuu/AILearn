package com.example.ormi5finalteam1.domain.grammar_example.dto;

import com.example.ormi5finalteam1.common.response.SingleResponseDto;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class GrammarExampleGradingDto extends SingleResponseDto<GrammarExampleDto> {
    public GrammarExampleGradingDto(GrammarExampleDto data, boolean isCorrect) {
        super(data);
        this.isCorrect = isCorrect;
    }

    private boolean isCorrect;
}
