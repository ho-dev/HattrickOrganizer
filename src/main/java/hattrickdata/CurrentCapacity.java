package hattrickdata;

import core.util.HODateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@SuperBuilder()
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CurrentCapacity extends Capacity {

    private HODateTime rebuildDate;

    public Optional<HODateTime> getRebuiltDate() {
        return Optional.ofNullable(rebuildDate);
    }
}
