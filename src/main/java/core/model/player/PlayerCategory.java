package core.model.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum PlayerCategory {

    NO_CATEGORY_SET(0, "ls.player.category.undefined"),
    KEEPER(1, "ls.player.category.Keeper"),
    WING_BACK(2, "ls.player.category.WingBack"),
    CENTRAL_DEFENDER(3, "ls.player.category.CentralDefender"),
    WINGER(4, "ls.player.category.Winger"),
    INNER_MIDFIELD(5, "ls.player.category.InnerMidfield"),
    FORWARD(6, "ls.player.category.Forward"),
    SUBSTITUTE(7, "ls.player.category.Substitute"),
    RESERVE(8, "ls.player.category.Reserve"),
    EXTRA_1(9, "ls.player.category.Extra1"),
    EXTRA_2(10, "ls.player.category.Extra2"),
    TRAINEE_1(11, "ls.player.category.Trainee1"),
    TRAINEE_2(12, "ls.player.category.Trainee2"),
    COACH_PROSPECT(13, "ls.player.category.CoachProspect");

    private final int id;
    private final String translationKey;

    private static final Map<Integer, PlayerCategory> MAP_ID_TO_PLAYER_CATEGORY =
        Arrays.stream(values()).collect(Collectors.toMap(PlayerCategory::getId, Function.identity()));

    public static PlayerCategory fromId(Integer id) {
        return Optional.ofNullable(id).map(MAP_ID_TO_PLAYER_CATEGORY::get).orElse(null);
    }

    public static int idOf(PlayerCategory playerCategory) {
        return Optional.ofNullable(playerCategory).map(PlayerCategory::getId).orElse(NO_CATEGORY_SET.getId());
    }
}
