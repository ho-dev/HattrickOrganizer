package hattrickdata;

import core.util.HODateTime;
import lombok.Builder;

@Builder
public record HattrickDataInfo(String fileName,
                               String version,
                               int userId,
                               HODateTime fetchedDate) {
}
