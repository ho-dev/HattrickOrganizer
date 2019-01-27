package core.util;

import java.util.Locale;

public class LocaleResolve {

	private LocaleResolve() {}

	static Locale parse(int leagueID) {
		Locale tmp = null;
		try {
			switch(leagueID) {
				case 1:
					tmp = Locale.forLanguageTag("sv-SE");
					break;
				case 2:
					tmp = Locale.forLanguageTag("en-GB");
					break;
				case 3:
					tmp = Locale.forLanguageTag("de-DE");
					break;
				case 4:
					tmp = Locale.forLanguageTag("it-IT");
					break;
				case 5:
					tmp = Locale.forLanguageTag("fr-FR");
					break;
				case 6:
					tmp = Locale.forLanguageTag("es-MX");
					break;
				case 7:
					tmp = Locale.forLanguageTag("es-AR");
					break;
				case 8:
					tmp = Locale.forLanguageTag("en-US");
					break;
				case 9:
					tmp = Locale.forLanguageTag("no-NO");
					break;
				case 11:
					tmp = Locale.forLanguageTag("da-DK");
					break;
				case 12:
					tmp = Locale.forLanguageTag("fi-FI");
					break;
				case 14:
					tmp = Locale.forLanguageTag("nl-NL");
					break;
				case 15:
					tmp = Locale.forLanguageTag("en-AU");
					break;
				case 16:
					tmp = Locale.forLanguageTag("pt-BR");
					break;
				case 17:
					tmp = Locale.forLanguageTag("en-CA");
					break;
				case 18:
					tmp = Locale.forLanguageTag("es-CL");
					break;
				case 19:
					tmp = Locale.forLanguageTag("es-CO");
					break;
				case 20:
					tmp = Locale.forLanguageTag("hi-IN");
					break;
				case 21:
					tmp = Locale.forLanguageTag("en-IE");
					break;
				case 22:
					tmp = Locale.forLanguageTag("ja-JP");
					break;
				case 23:
					tmp = Locale.forLanguageTag("es-PE");
					break;
				case 24:
					tmp = Locale.forLanguageTag("pl-PL");
					break;
				case 25:
					tmp = Locale.forLanguageTag("pt-PT");
					break;
				case 26:
					tmp = Locale.forLanguageTag("en-GB");
					break;
				case 27:
					tmp = Locale.forLanguageTag("en-ZA");
					break;
				case 28:
					tmp = Locale.forLanguageTag("es-UY");
					break;
				case 29:
					tmp = Locale.forLanguageTag("es-VE");
					break;
				case 30:
					tmp = Locale.forLanguageTag("ko-KR");
					break;
				case 31:
					tmp = Locale.forLanguageTag("th-TH");
					break;
				case 32:
					tmp = Locale.forLanguageTag("tr-TR");
					break;
				case 33:
					tmp = Locale.forLanguageTag("ar-EG");
					break;
				case 34:
					tmp = Locale.forLanguageTag("zh-CN");
					break;
				case 35:
					tmp = Locale.forLanguageTag("ru-RU");
					break;
				case 36:
					tmp = Locale.forLanguageTag("es-ES");
					break;
				case 37:
					tmp = Locale.forLanguageTag("ro-RO");
					break;
				case 38:
					tmp = Locale.forLanguageTag("is-IS");
					break;
				case 39:
					tmp = Locale.forLanguageTag("de-AT");
					break;
				case 44:
					tmp = Locale.forLanguageTag("fr-BE");
					break;
				case 45:
					tmp = Locale.forLanguageTag("ms-MY");
					break;
				case 46:
					tmp = Locale.forLanguageTag("de-CH");
					break;
				case 47:
					tmp = Locale.forLanguageTag("zh-SG");
					break;
				case 50:
					tmp = Locale.forLanguageTag("el-GR");
					break;
				case 51:
					tmp = Locale.forLanguageTag("hu-HU");
					break;
				case 52:
					tmp = Locale.forLanguageTag("cs-CZ");
					break;
				case 53:
					tmp = Locale.forLanguageTag("lv-LV");
					break;
				case 54:
					tmp = Locale.forLanguageTag("in-ID");
					break;
				case 55:
					tmp = Locale.forLanguageTag("en-PH");
					break;
				case 56:
					tmp = Locale.forLanguageTag("et-EE");
					break;
				case 57:
					tmp = Locale.forLanguageTag("sr-RS");
					break;
				case 58:
					tmp = Locale.forLanguageTag("hr-HR");
					break;
				case 59:
					tmp = Locale.forLanguageTag("zh-HK");
					break;
				case 60:
					tmp = Locale.forLanguageTag("zh-TW");
					break;
				case 61:
					tmp = Locale.forLanguageTag("en-GB");
					break;
				case 62:
					tmp = Locale.forLanguageTag("bg-BG");
					break;
				case 63:
					tmp = Locale.forLanguageTag("iw-IL");
					break;
				case 64:
					tmp = Locale.forLanguageTag("sl-SI");
					break;
				case 66:
					tmp = Locale.forLanguageTag("lt-LT");
					break;
				case 67:
					tmp = Locale.forLanguageTag("sk-SK");
					break;
				case 68:
					tmp = Locale.forLanguageTag("uk-UA");
					break;
				case 69:
					tmp = Locale.forLanguageTag("sr-BA");
					break;
				case 70:
					tmp = Locale.forLanguageTag("vi-VN");
					break;
				case 71:
					tmp = Locale.forLanguageTag("en-PK");
					break;
				case 72:
					tmp = Locale.forLanguageTag("es-PY");
					break;
				case 73:
					tmp = Locale.forLanguageTag("es-EC");
					break;
				case 74:
					tmp = Locale.forLanguageTag("es-BO");
					break;
				case 75:
					tmp = Locale.forLanguageTag("en-NG");
					break;
				case 76:
					tmp = Locale.forLanguageTag("fo-FO");
					break;
				case 77:
					tmp = Locale.forLanguageTag("tzm-MA");
					break;
				case 79:
					tmp = Locale.forLanguageTag("ar-SA");
					break;
				case 80:
					tmp = Locale.forLanguageTag("ar-TN");
					break;
				case 81:
					tmp = Locale.forLanguageTag("es-CR");
					break;
				case 83:
					tmp = Locale.forLanguageTag("ar-AE");
					break;
				case 84:
					tmp = Locale.forLanguageTag("fr-LU");
					break;
				case 85:
					tmp = Locale.forLanguageTag("fa-IR");
					break;
				case 88:
					tmp = Locale.forLanguageTag("es-DO");
					break;
				case 89:
					tmp = Locale.forLanguageTag("el-CY");
					break;
				case 91:
					tmp = Locale.forLanguageTag("be-BY");
					break;
				case 93:
					tmp = Locale.forLanguageTag("en-GB");
					break;
				case 94:
					tmp = Locale.forLanguageTag("en-JM");
					break;
				case 95:
					tmp = Locale.forLanguageTag("kam-KE");
					break;
				case 96:
					tmp = Locale.forLanguageTag("es-PA");
					break;
				case 97:
					tmp = Locale.forLanguageTag("mk-MK");
					break;
				case 98:
					tmp = Locale.forLanguageTag("sq-AL");
					break;
				case 99:
					tmp = Locale.forLanguageTag("es-HN");
					break;
				case 100:
					tmp = Locale.forLanguageTag("es-SV");
					break;
				case 101:
					tmp = Locale.forLanguageTag("en-MT");
					break;
				case 102:
					tmp = Locale.forLanguageTag("ky-KG");
					break;
				case 103:
					tmp = Locale.forLanguageTag("ro-MD");
					break;
				case 104:
					tmp = Locale.forLanguageTag("ka-GE");
					break;
				case 105:
					tmp = Locale.forLanguageTag("ca-AD");
					break;
				case 106:
					tmp = Locale.forLanguageTag("ar-JO");
					break;
				case 107:
					tmp = Locale.forLanguageTag("es-GT");
					break;
				case 110:
					tmp = Locale.forLanguageTag("en-TT");
					break;
				case 111:
					tmp = Locale.forLanguageTag("es-NI");
					break;
				case 112:
					tmp = Locale.forLanguageTag("kk-KZ");
					break;
				case 113:
					tmp = Locale.forLanguageTag("nl-SR");
					break;
				case 117:
					tmp = Locale.forLanguageTag("de-LI");
					break;
				case 118:
					tmp = Locale.forLanguageTag("ar-DZ");
					break;
				case 119:
					tmp = Locale.forLanguageTag("mn-MN");
					break;
				case 120:
					tmp = Locale.forLanguageTag("ar-LB");
					break;
				case 121:
					tmp = Locale.forLanguageTag("fr-SN");
					break;
				case 122:
					tmp = Locale.forLanguageTag("hy-AM");
					break;
				case 123:
					tmp = Locale.forLanguageTag("ar-BH");
					break;
				case 124:
					tmp = Locale.forLanguageTag("en-BB");
					break;
				case 125:
					tmp = Locale.forLanguageTag("kea-CV");
					break;
				case 126:
					tmp = Locale.forLanguageTag("fr-CI");
					break;
				case 127:
					tmp = Locale.forLanguageTag("ar-KW");
					break;
				case 128:
					tmp = Locale.forLanguageTag("ar-IQ");
					break;
				case 129:
					tmp = Locale.forLanguageTag("az-Cyrl-AZ");
					break;
				case 130:
					tmp = Locale.forLanguageTag("ln-AO");
					break;
				case 131:
					tmp = Locale.forLanguageTag("sr-ME");
					break;
				case 132:
					tmp = Locale.forLanguageTag("bn-BD");
					break;
				case 133:
					tmp = Locale.forLanguageTag("ar-YE");
					break;
				case 134:
					tmp = Locale.forLanguageTag("ar-OM");
					break;
				case 135:
					tmp = Locale.forLanguageTag("mgh-MZ");
					break;
				case 136:
					tmp = Locale.forLanguageTag("ms-BN");
					break;
				case 137:
					tmp = Locale.forLanguageTag("ha-GH");
					break;
				case 138:
					tmp = Locale.forLanguageTag("km-KH");
					break;
				case 139:
					tmp = Locale.forLanguageTag("yo-BJ");
					break;
				case 140:
					tmp = Locale.forLanguageTag("ar-SY");
					break;
				case 141:
					tmp = Locale.forLanguageTag("ar-QA");
					break;
				case 142:
					tmp = Locale.forLanguageTag("vun-TZ");
					break;
				case 143:
					tmp = Locale.forLanguageTag("lg-UG");
					break;
				case 144:
					tmp = Locale.forLanguageTag("dv-MV");
					break;
				case 145:
					tmp = Locale.forLanguageTag("uz-Arab-AF");
					break;
				case 146:
					tmp = Locale.forLanguageTag("mgo-CM");
					break;
				case 147:
					tmp = Locale.forLanguageTag("es-CU");
					break;
				case 148:
					tmp = Locale.forLanguageTag("ar-PS");
					break;
				case 149:
					tmp = Locale.forLanguageTag("pt-ST");
					break;
				case 151:
					tmp = Locale.forLanguageTag("fr-KM");
					break;
				case 152:
					tmp = Locale.forLanguageTag("si-LK");
					break;
				case 153:
					tmp = Locale.forLanguageTag("nl-CW");
					break;
				case 154:
					tmp = Locale.forLanguageTag("en-GU");
					break;
				default:
					tmp = Locale.getDefault();
			}
		} catch(NullPointerException e) {
			tmp = Locale.getDefault();
		  }

		return tmp;
	}
}