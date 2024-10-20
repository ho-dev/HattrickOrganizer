package hattrickdata;

import lombok.Builder;

@Builder
public record Team(int id, String name) {
}
