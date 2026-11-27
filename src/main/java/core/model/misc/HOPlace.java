package core.model.misc;

/** Country and region of a team, e.g. a youth team, one of its scouts or a senior team. */
public interface HOPlace {
    Integer getCountryId();

    void setCountryId(Integer countryId);

    String getCountryName();

    void setCountryName(String countryName);

    Integer getRegionId();

    void setRegionId(Integer regionId);

    String getRegionName();

    void setRegionName(String regionName);
}
