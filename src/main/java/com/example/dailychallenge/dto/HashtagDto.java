package com.example.dailychallenge.dto;

import com.example.dailychallenge.exception.hashtag.HashtagDtoBlank;
import java.util.Iterator;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashtagDto implements Iterable<String> {
    private List<String> content;

    @Builder
    public HashtagDto(List<String> content) {
        validate(content);
        this.content = content;
    }

    private void validate(List<String> content) {
        content.stream()
                .filter(String::isBlank)
                .findAny()
                .ifPresent(s -> {
                    throw new HashtagDtoBlank();
                });
    }

    @Override
    public Iterator<String> iterator() {
        return content.iterator();
    }
}
