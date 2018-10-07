package module.ifa.config;

public enum Config {

	VISITED_FLAG_WIDTH("IFA_VisitedFlagWidth"), 
	HOSTED_FLAG_WIDTH("IFA_HostedFlagWidth"),
	SHOW_VISITED_HEADER("IFA_ShowVisitedHeader"),
	SHOW_HOSTED_HEADER("IFA_ShowHostedHeader"),
	VISITED_ROUNDLY("IFA_VisitedRoundly"),
	HOSTED_ROUNDLY("IFA_HostedRoundly"),
	VISITED_GREY("IFA_VisitedGrey"),
	HOSTED_GREY("IFA_HostedGrey"),
	VISITED_BRIGHTNESS("IFA_VisitedBrightness"),
	HOSTED_BRIGHTNESS("IFA_HostedBrightness"),
	VISITED_HEADER_TEXT("IFA_VisitedHeaderText"),
	HOSTED_HEADER_TEXT("IFA_HostedHeaderText"),
	ANIMATED_GIF("IFA_AnimatedGif"),
	ANIMATED_GIF_DELAY("IFA_AnimatedGifDelay"),
	VISITED_EMBLEM_PATH("IFA_VisitedEmblemPath"),
	HOSTED_EMBLEM_PATH("IFA_HostedEmblemPath"),
	STATS_TABLES_DIVIDER_LOCATION("IFA_StatsTableDividerLocation");

	private String txt;

	private Config(String text) {
		this.txt = text;
	}

	@Override
	public String toString() {
		return txt;
	}
}
