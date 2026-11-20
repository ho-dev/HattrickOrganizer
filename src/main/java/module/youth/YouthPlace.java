package module.youth;

/** Country and region of a youth team or of one of its scouts (youthteamdetails). */
public interface YouthPlace {
    Integer getCountryId();

    void setCountryId(Integer countryId);

    String getCountryName();

    void setCountryName(String countryName);

    Integer getRegionId();

    void setRegionId(Integer regionId);

    String getRegionName();

    void setRegionName(String regionName);
}
