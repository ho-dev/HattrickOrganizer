package hattrickdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Arena {

    private int id;
    private String name;

    private Team team;
    private League league;
    private Region region;
    private String arenaImage;
    private String arenaFallbackImage;

    private CurrentCapacity currentCapacity;
    private ExpandedCapacity expandedCapacity;

    public Optional<ExpandedCapacity> getExpandedCapacity() {
        return Optional.ofNullable(expandedCapacity);
    }
}
