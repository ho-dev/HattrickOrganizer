package core.util;

import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyUtils {

	private CurrencyUtils() {}

	static NumberFormat getLeagueCurrencyFormater(int leagueID) {
		Locale leagueLocale = null;
		DecimalFormat formatter = null;
		switch(leagueID) {
			case 1:
				leagueLocale = Locale.forLanguageTag("sv-SE");
				break;
			case 2:
				leagueLocale = Locale.forLanguageTag("en-GB");
				break;
			case 3:
				leagueLocale = Locale.forLanguageTag("de-DE");
				break;
			case 4:
				leagueLocale = Locale.forLanguageTag("it-IT");
				break;
			case 5:
				leagueLocale = Locale.forLanguageTag("fr-FR");
				break;
			case 6:
				leagueLocale = Locale.forLanguageTag("es-MX");
				break;
			case 7:
				leagueLocale = Locale.forLanguageTag("es-AR");
				break;
			case 8:
				leagueLocale = Locale.forLanguageTag("en-US");
				break;
			case 9:
				leagueLocale = Locale.forLanguageTag("no-NO");
				break;
			case 11:
				leagueLocale = Locale.forLanguageTag("da-DK");
				break;
			case 12:
				leagueLocale = Locale.forLanguageTag("fi-FI");
				break;
			case 14:
				leagueLocale = Locale.forLanguageTag("nl-NL");
				break;
			case 15:
				leagueLocale = Locale.forLanguageTag("en-AU");
				break;
			case 16:
				leagueLocale = Locale.forLanguageTag("pt-BR");
				break;
			case 17:
				leagueLocale = Locale.forLanguageTag("en-CA");
				break;
			case 18:
				leagueLocale = Locale.forLanguageTag("es-CL");
				break;
			case 19:
				leagueLocale = Locale.forLanguageTag("es-CO");
				break;
			case 20:
				leagueLocale = Locale.forLanguageTag("hi-IN");
				formatter = (DecimalFormat)DecimalFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
				DecimalFormatSymbols formaterSymbols = formatter.getDecimalFormatSymbols();
				formaterSymbols.setCurrencySymbol(getCurrencySymbol(leagueLocale));
				formatter.setDecimalFormatSymbols(formaterSymbols);
				break;
			case 21:
				leagueLocale = Locale.forLanguageTag("en-IE");
				break;
			case 22:
				leagueLocale = Locale.forLanguageTag("ja-JP");
				break;
			case 23:
				leagueLocale = Locale.forLanguageTag("es-PE");
				break;
			case 24:
				leagueLocale = Locale.forLanguageTag("pl-PL");
				break;
			case 25:
				leagueLocale = Locale.forLanguageTag("pt-PT");
				break;
			case 26:
				leagueLocale = Locale.forLanguageTag("en-GB");
				break;
			case 27:
				leagueLocale = Locale.forLanguageTag("en-ZA");
				break;
			case 28:
				leagueLocale = Locale.forLanguageTag("es-UY");
				break;
			case 29:
				leagueLocale = Locale.forLanguageTag("es-VE");
				break;
			case 30:
				leagueLocale = Locale.forLanguageTag("ko-KR");
				break;
			case 31:
				leagueLocale = Locale.forLanguageTag("th-TH");
				break;
			case 32:
				leagueLocale = Locale.forLanguageTag("tr-TR");
				break;
			case 33:
				leagueLocale = Locale.forLanguageTag("ar-EG");
				break;
			case 34:
				leagueLocale = Locale.forLanguageTag("zh-CN");
				break;
			case 35:
				leagueLocale = Locale.forLanguageTag("ru-RU");
				break;
			case 36:
				leagueLocale = Locale.forLanguageTag("es-ES");
				break;
			case 37:
				leagueLocale = Locale.forLanguageTag("ro-RO");
				break;
			case 38:
				leagueLocale = Locale.forLanguageTag("is-IS");
				break;
			case 39:
				leagueLocale = Locale.forLanguageTag("de-AT");
				break;
			case 44:
				leagueLocale = Locale.forLanguageTag("fr-BE");
				break;
			case 45:
				leagueLocale = Locale.forLanguageTag("ms-MY");
				break;
			case 46:
				leagueLocale = Locale.forLanguageTag("de-CH");
				break;
			case 47:
				leagueLocale = Locale.forLanguageTag("zh-SG");
				break;
			case 50:
				leagueLocale = Locale.forLanguageTag("el-GR");
				break;
			case 51:
				leagueLocale = Locale.forLanguageTag("hu-HU");
				break;
			case 52:
				leagueLocale = Locale.forLanguageTag("cs-CZ");
				break;
			case 53:
				leagueLocale = Locale.forLanguageTag("lv-LV");
				break;
			case 54:
				leagueLocale = Locale.forLanguageTag("in-ID");
				break;
			case 55:
				leagueLocale = Locale.forLanguageTag("en-PH");
				break;
			case 56:
				leagueLocale = Locale.forLanguageTag("et-EE");
				break;
			case 57:
				leagueLocale = Locale.forLanguageTag("sr-RS");
				break;
			case 58:
				leagueLocale = Locale.forLanguageTag("hr-HR");
				break;
			case 59:
				leagueLocale = Locale.forLanguageTag("zh-HK");
				break;
			case 60:
				leagueLocale = Locale.forLanguageTag("zh-TW");
				break;
			case 61:
				leagueLocale = Locale.forLanguageTag("en-GB");
				break;
			case 62:
				leagueLocale = Locale.forLanguageTag("bg-BG");
				break;
			case 63:
				leagueLocale = Locale.forLanguageTag("iw-IL");
				break;
			case 64:
				leagueLocale = Locale.forLanguageTag("sl-SI");
				break;
			case 66:
				leagueLocale = Locale.forLanguageTag("lt-LT");
				break;
			case 67:
				leagueLocale = Locale.forLanguageTag("sk-SK");
				break;
			case 68:
				leagueLocale = Locale.forLanguageTag("uk-UA");
				break;
			case 69:
				leagueLocale = Locale.forLanguageTag("sr-BA");
				break;
			case 70:
				leagueLocale = Locale.forLanguageTag("vi-VN");
				break;
			case 71:
				leagueLocale = Locale.forLanguageTag("en-PK");
				break;
			case 72:
				leagueLocale = Locale.forLanguageTag("es-PY");
				break;
			case 73:
				leagueLocale = Locale.forLanguageTag("es-EC");
				break;
			case 74:
				leagueLocale = Locale.forLanguageTag("es-BO");
				break;
			case 75:
				leagueLocale = Locale.forLanguageTag("en-NG");
				break;
			case 76:
				leagueLocale = Locale.forLanguageTag("fo-FO");
				break;
			case 77:
				leagueLocale = Locale.forLanguageTag("tzm-MA");
				break;
			case 79:
				leagueLocale = Locale.forLanguageTag("ar-SA");
				break;
			case 80:
				leagueLocale = Locale.forLanguageTag("ar-TN");
				break;
			case 81:
				leagueLocale = Locale.forLanguageTag("es-CR");
				break;
			case 83:
				leagueLocale = Locale.forLanguageTag("ar-AE");
				break;
			case 84:
				leagueLocale = Locale.forLanguageTag("fr-LU");
				break;
			case 85:
				leagueLocale = Locale.forLanguageTag("fa-IR");
				break;
			case 88:
				leagueLocale = Locale.forLanguageTag("es-DO");
				break;
			case 89:
				leagueLocale = Locale.forLanguageTag("el-CY");
				break;
			case 91:
				leagueLocale = Locale.forLanguageTag("be-BY");
				break;
			case 93:
				leagueLocale = Locale.forLanguageTag("en-GB");
				break;
			case 94:
				leagueLocale = Locale.forLanguageTag("en-JM");
				break;
			case 95:
				leagueLocale = Locale.forLanguageTag("kam-KE");
				break;
			case 96:
				leagueLocale = Locale.forLanguageTag("es-PA");
				break;
			case 97:
				leagueLocale = Locale.forLanguageTag("mk-MK");
				break;
			case 98:
				leagueLocale = Locale.forLanguageTag("sq-AL");
				break;
			case 99:
				leagueLocale = Locale.forLanguageTag("es-HN");
				break;
			case 100:
				leagueLocale = Locale.forLanguageTag("es-SV");
				break;
			case 101:
				leagueLocale = Locale.forLanguageTag("en-MT");
				break;
			case 102:
				leagueLocale = Locale.forLanguageTag("ky-KG");
				break;
			case 103:
				leagueLocale = Locale.forLanguageTag("ro-MD");
				break;
			case 104:
				leagueLocale = Locale.forLanguageTag("ka-GE");
				break;
			case 105:
				leagueLocale = Locale.forLanguageTag("ca-AD");
				break;
			case 106:
				leagueLocale = Locale.forLanguageTag("ar-JO");
				break;
			case 107:
				leagueLocale = Locale.forLanguageTag("es-GT");
				break;
			case 110:
				leagueLocale = Locale.forLanguageTag("en-TT");
				break;
			case 111:
				leagueLocale = Locale.forLanguageTag("es-NI");
				break;
			case 112:
				leagueLocale = Locale.forLanguageTag("kk-KZ");
				break;
			case 113:
				leagueLocale = Locale.forLanguageTag("nl-SR");
				break;
			case 117:
				leagueLocale = Locale.forLanguageTag("de-LI");
				break;
			case 118:
				leagueLocale = Locale.forLanguageTag("ar-DZ");
				break;
			case 119:
				leagueLocale = Locale.forLanguageTag("mn-MN");
				break;
			case 120:
				leagueLocale = Locale.forLanguageTag("ar-LB");
				break;
			case 121:
				leagueLocale = Locale.forLanguageTag("fr-SN");
				break;
			case 122:
				leagueLocale = Locale.forLanguageTag("hy-AM");
				break;
			case 123:
				leagueLocale = Locale.forLanguageTag("ar-BH");
				break;
			case 124:
				leagueLocale = Locale.forLanguageTag("en-BB");
				break;
			case 125:
				leagueLocale = Locale.forLanguageTag("kea-CV");
				break;
			case 126:
				leagueLocale = Locale.forLanguageTag("fr-CI");
				break;
			case 127:
				leagueLocale = Locale.forLanguageTag("ar-KW");
				break;
			case 128:
				leagueLocale = Locale.forLanguageTag("ar-IQ");
				break;
			case 129:
				leagueLocale = Locale.forLanguageTag("az-Cyrl-AZ");
				break;
			case 130:
				leagueLocale = Locale.forLanguageTag("ln-AO");
				break;
			case 131:
				leagueLocale = Locale.forLanguageTag("sr-ME");
				break;
			case 132:
				leagueLocale = Locale.forLanguageTag("bn-BD");
				break;
			case 133:
				leagueLocale = Locale.forLanguageTag("ar-YE");
				break;
			case 134:
				leagueLocale = Locale.forLanguageTag("ar-OM");
				break;
			case 135:
				leagueLocale = Locale.forLanguageTag("mgh-MZ");
				break;
			case 136:
				leagueLocale = Locale.forLanguageTag("ms-BN");
				break;
			case 137:
				leagueLocale = Locale.forLanguageTag("ha-GH");
				break;
			case 138:
				leagueLocale = Locale.forLanguageTag("km-KH");
				break;
			case 139:
				leagueLocale = Locale.forLanguageTag("yo-BJ");
				break;
			case 140:
				leagueLocale = Locale.forLanguageTag("ar-SY");
				break;
			case 141:
				leagueLocale = Locale.forLanguageTag("ar-QA");
				break;
			case 142:
				leagueLocale = Locale.forLanguageTag("vun-TZ");
				break;
			case 143:
				leagueLocale = Locale.forLanguageTag("lg-UG");
				break;
			case 144:
				leagueLocale = Locale.forLanguageTag("dv-MV");
				break;
			case 145:
				leagueLocale = Locale.forLanguageTag("uz-Arab-AF");
				break;
			case 146:
				leagueLocale = Locale.forLanguageTag("mgo-CM");
				break;
			case 147:
				leagueLocale = Locale.forLanguageTag("es-CU");
				break;
			case 148:
				leagueLocale = Locale.forLanguageTag("ar-PS");
				break;
			case 149:
				leagueLocale = Locale.forLanguageTag("pt-ST");
				break;
			case 151:
				leagueLocale = Locale.forLanguageTag("fr-KM");
				break;
			case 152:
				leagueLocale = Locale.forLanguageTag("si-LK");
				break;
			case 153:
				leagueLocale = Locale.forLanguageTag("nl-CW");
				break;
			case 154:
				leagueLocale = Locale.forLanguageTag("en-GU");
				break;
			default:
				leagueLocale = Locale.getDefault();
		}

		if(formatter == null) formatter = (DecimalFormat)DecimalFormat.getCurrencyInstance(leagueLocale);

		return formatter;
	}
	
	static String getCurrencySymbol(Locale locale) {
		return DecimalFormat.getCurrencyInstance(locale).getCurrency().getSymbol(locale);
	}
}
