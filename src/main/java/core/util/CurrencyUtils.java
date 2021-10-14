package core.util;

import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyUtils {

	public static String CURRENCYSYMBOL = "";

	private CurrencyUtils() {}


	static NumberFormat getLeagueCurrencyFormater(int leagueId) {
		Locale leagueLocale;
		DecimalFormat formatter = null;
		switch (leagueId) {
			case 1 -> leagueLocale = Locale.forLanguageTag("sv-SE");
			case 2 -> leagueLocale = Locale.forLanguageTag("en-GB");
			case 3 -> leagueLocale = Locale.forLanguageTag("de-DE");
			case 4 -> leagueLocale = Locale.forLanguageTag("it-IT");
			case 5 -> leagueLocale = Locale.forLanguageTag("fr-FR");
			case 6 -> leagueLocale = Locale.forLanguageTag("es-MX");
			case 7 -> leagueLocale = Locale.forLanguageTag("es-AR");
			case 8 -> leagueLocale = Locale.forLanguageTag("en-US");
			case 9 -> leagueLocale = Locale.forLanguageTag("no-NO");
			case 11 -> leagueLocale = Locale.forLanguageTag("da-DK");
			case 12 -> leagueLocale = Locale.forLanguageTag("fi-FI");
			case 14 -> leagueLocale = Locale.forLanguageTag("nl-NL");
			case 15 -> leagueLocale = Locale.forLanguageTag("en-AU");
			case 16 -> leagueLocale = Locale.forLanguageTag("pt-BR");
			case 17 -> leagueLocale = Locale.forLanguageTag("en-CA");
			case 18 -> leagueLocale = Locale.forLanguageTag("es-CL");
			case 19 -> leagueLocale = Locale.forLanguageTag("es-CO");
			case 20 -> {
				leagueLocale = Locale.forLanguageTag("hi-IN");
				formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
				DecimalFormatSymbols formaterSymbols = formatter.getDecimalFormatSymbols();
				formaterSymbols.setCurrencySymbol(getCurrencySymbol(leagueLocale));
				formatter.setDecimalFormatSymbols(formaterSymbols);
			}
			case 21 -> leagueLocale = Locale.forLanguageTag("en-IE");
			case 22 -> leagueLocale = Locale.forLanguageTag("ja-JP");
			case 23 -> leagueLocale = Locale.forLanguageTag("es-PE");
			case 24 -> leagueLocale = Locale.forLanguageTag("pl-PL");
			case 25 -> leagueLocale = Locale.forLanguageTag("pt-PT");
			case 26 -> leagueLocale = Locale.forLanguageTag("en-GB");
			case 27 -> leagueLocale = Locale.forLanguageTag("en-ZA");
			case 28 -> leagueLocale = Locale.forLanguageTag("es-UY");
			case 29 -> leagueLocale = Locale.forLanguageTag("es-VE");
			case 30 -> leagueLocale = Locale.forLanguageTag("ko-KR");
			case 31 -> leagueLocale = Locale.forLanguageTag("th-TH");
			case 32 -> leagueLocale = Locale.forLanguageTag("tr-TR");
			case 33 -> leagueLocale = Locale.forLanguageTag("ar-EG");
			case 34 -> leagueLocale = Locale.forLanguageTag("zh-CN");
			case 35 -> leagueLocale = Locale.forLanguageTag("ru-RU");
			case 36 -> leagueLocale = Locale.forLanguageTag("es-ES");
			case 37 -> leagueLocale = Locale.forLanguageTag("ro-RO");
			case 38 -> leagueLocale = Locale.forLanguageTag("is-IS");
			case 39 -> leagueLocale = Locale.forLanguageTag("de-AT");
			case 44 -> leagueLocale = Locale.forLanguageTag("fr-BE");
			case 45 -> leagueLocale = Locale.forLanguageTag("ms-MY");
			case 46 -> leagueLocale = Locale.forLanguageTag("de-CH");
			case 47 -> leagueLocale = Locale.forLanguageTag("zh-SG");
			case 50 -> leagueLocale = Locale.forLanguageTag("el-GR");
			case 51 -> leagueLocale = Locale.forLanguageTag("hu-HU");
			case 52 -> leagueLocale = Locale.forLanguageTag("cs-CZ");
			case 53 -> leagueLocale = Locale.forLanguageTag("lv-LV");
			case 54 -> leagueLocale = Locale.forLanguageTag("in-ID");
			case 55 -> leagueLocale = Locale.forLanguageTag("en-PH");
			case 56 -> leagueLocale = Locale.forLanguageTag("et-EE");
			case 57 -> leagueLocale = Locale.forLanguageTag("sr-RS");
			case 58 -> leagueLocale = Locale.forLanguageTag("hr-HR");
			case 59 -> leagueLocale = Locale.forLanguageTag("zh-HK");
			case 60 -> leagueLocale = Locale.forLanguageTag("zh-TW");
			case 61 -> leagueLocale = Locale.forLanguageTag("en-GB");
			case 62 -> leagueLocale = Locale.forLanguageTag("bg-BG");
			case 63 -> leagueLocale = Locale.forLanguageTag("iw-IL");
			case 64 -> leagueLocale = Locale.forLanguageTag("sl-SI");
			case 66 -> leagueLocale = Locale.forLanguageTag("lt-LT");
			case 67 -> leagueLocale = Locale.forLanguageTag("sk-SK");
			case 68 -> leagueLocale = Locale.forLanguageTag("uk-UA");
			case 69 -> leagueLocale = Locale.forLanguageTag("sr-BA");
			case 70 -> leagueLocale = Locale.forLanguageTag("vi-VN");
			case 71 -> leagueLocale = Locale.forLanguageTag("en-PK");
			case 72 -> leagueLocale = Locale.forLanguageTag("es-PY");
			case 73 -> leagueLocale = Locale.forLanguageTag("es-EC");
			case 74 -> leagueLocale = Locale.forLanguageTag("es-BO");
			case 75 -> leagueLocale = Locale.forLanguageTag("en-NG");
			case 76 -> leagueLocale = Locale.forLanguageTag("fo-FO");
			case 77 -> leagueLocale = Locale.forLanguageTag("tzm-MA");
			case 79 -> leagueLocale = Locale.forLanguageTag("ar-SA");
			case 80 -> leagueLocale = Locale.forLanguageTag("ar-TN");
			case 81 -> leagueLocale = Locale.forLanguageTag("es-CR");
			case 83 -> leagueLocale = Locale.forLanguageTag("ar-AE");
			case 84 -> leagueLocale = Locale.forLanguageTag("fr-LU");
			case 85 -> leagueLocale = Locale.forLanguageTag("fa-IR");
			case 88 -> leagueLocale = Locale.forLanguageTag("es-DO");
			case 89 -> leagueLocale = Locale.forLanguageTag("el-CY");
			case 91 -> leagueLocale = Locale.forLanguageTag("be-BY");
			case 93 -> leagueLocale = Locale.forLanguageTag("en-GB");
			case 94 -> leagueLocale = Locale.forLanguageTag("en-JM");
			case 95 -> leagueLocale = Locale.forLanguageTag("kam-KE");
			case 96 -> leagueLocale = Locale.forLanguageTag("es-PA");
			case 97 -> leagueLocale = Locale.forLanguageTag("mk-MK");
			case 98 -> leagueLocale = Locale.forLanguageTag("sq-AL");
			case 99 -> leagueLocale = Locale.forLanguageTag("es-HN");
			case 100 -> leagueLocale = Locale.forLanguageTag("es-SV");
			case 101 -> leagueLocale = Locale.forLanguageTag("en-MT");
			case 102 -> leagueLocale = Locale.forLanguageTag("ky-KG");
			case 103 -> leagueLocale = Locale.forLanguageTag("ro-MD");
			case 104 -> leagueLocale = Locale.forLanguageTag("ka-GE");
			case 105 -> leagueLocale = Locale.forLanguageTag("ca-AD");
			case 106 -> leagueLocale = Locale.forLanguageTag("ar-JO");
			case 107 -> leagueLocale = Locale.forLanguageTag("es-GT");
			case 110 -> leagueLocale = Locale.forLanguageTag("en-TT");
			case 111 -> leagueLocale = Locale.forLanguageTag("es-NI");
			case 112 -> leagueLocale = Locale.forLanguageTag("kk-KZ");
			case 113 -> leagueLocale = Locale.forLanguageTag("nl-SR");
			case 117 -> leagueLocale = Locale.forLanguageTag("de-LI");
			case 118 -> leagueLocale = Locale.forLanguageTag("ar-DZ");
			case 119 -> leagueLocale = Locale.forLanguageTag("mn-MN");
			case 120 -> leagueLocale = Locale.forLanguageTag("ar-LB");
			case 121 -> leagueLocale = Locale.forLanguageTag("fr-SN");
			case 122 -> leagueLocale = Locale.forLanguageTag("hy-AM");
			case 123 -> leagueLocale = Locale.forLanguageTag("ar-BH");
			case 124 -> leagueLocale = Locale.forLanguageTag("en-BB");
			case 125 -> leagueLocale = Locale.forLanguageTag("kea-CV");
			case 126 -> leagueLocale = Locale.forLanguageTag("fr-CI");
			case 127 -> leagueLocale = Locale.forLanguageTag("ar-KW");
			case 128 -> leagueLocale = Locale.forLanguageTag("ar-IQ");
			case 129 -> leagueLocale = Locale.forLanguageTag("az-Cyrl-AZ");
			case 130 -> leagueLocale = Locale.forLanguageTag("ln-AO");
			case 131 -> leagueLocale = Locale.forLanguageTag("sr-ME");
			case 132 -> leagueLocale = Locale.forLanguageTag("bn-BD");
			case 133 -> leagueLocale = Locale.forLanguageTag("ar-YE");
			case 134 -> leagueLocale = Locale.forLanguageTag("ar-OM");
			case 135 -> leagueLocale = Locale.forLanguageTag("mgh-MZ");
			case 136 -> leagueLocale = Locale.forLanguageTag("ms-BN");
			case 137 -> leagueLocale = Locale.forLanguageTag("ha-GH");
			case 138 -> leagueLocale = Locale.forLanguageTag("km-KH");
			case 139 -> leagueLocale = Locale.forLanguageTag("yo-BJ");
			case 140 -> leagueLocale = Locale.forLanguageTag("ar-SY");
			case 141 -> leagueLocale = Locale.forLanguageTag("ar-QA");
			case 142 -> leagueLocale = Locale.forLanguageTag("vun-TZ");
			case 143 -> leagueLocale = Locale.forLanguageTag("lg-UG");
			case 144 -> leagueLocale = Locale.forLanguageTag("dv-MV");
			case 145 -> leagueLocale = Locale.forLanguageTag("uz-Arab-AF");
			case 146 -> leagueLocale = Locale.forLanguageTag("mgo-CM");
			case 147 -> leagueLocale = Locale.forLanguageTag("es-CU");
			case 148 -> leagueLocale = Locale.forLanguageTag("ar-PS");
			case 149 -> leagueLocale = Locale.forLanguageTag("pt-ST");
			case 151 -> leagueLocale = Locale.forLanguageTag("fr-KM");
			case 152 -> leagueLocale = Locale.forLanguageTag("si-LK");
			case 153 -> leagueLocale = Locale.forLanguageTag("nl-CW");
			case 154 -> leagueLocale = Locale.forLanguageTag("en-GU");
			default -> leagueLocale = Locale.getDefault();
		}

		CURRENCYSYMBOL = getCurrencySymbol(leagueLocale);

		String systemCountryName = Locale.getDefault().getDisplayCountry(Locale.US);
		String leagueCountryName = leagueLocale.getDisplayCountry(Locale.US);
		if(leagueCountryName.equals(systemCountryName)) formatter = (DecimalFormat)DecimalFormat.getCurrencyInstance();

		if(formatter == null) formatter = (DecimalFormat)DecimalFormat.getCurrencyInstance(leagueLocale);

		return formatter;
	}
	
	static String getCurrencySymbol(Locale locale) {
		return DecimalFormat.getCurrencyInstance(locale).getCurrency().getSymbol(locale);
	}
}
