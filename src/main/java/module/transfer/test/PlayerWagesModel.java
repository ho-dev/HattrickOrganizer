package module.transfer.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerWagesModel {

	private List<PlayerWage> list;

	private PlayerWagesModel() {
	}

	public static PlayerWagesModel create(int playerId, Date from, Date to) {
		PlayerWagesModel model = new PlayerWagesModel();
		model.list = new ArrayList<>();

		List<Date> updates = Calc.getUpdates(Calc.getEconomyDate(), from, to);
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);

		var ageWageMap = new HashMap<Integer, Wage>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(wage.getAge(), wage);
		}

		Date birthDay17 = Calc.get17thBirthday(playerId);

		for (Date date : updates) {
			PlayerWage data = new PlayerWage();
			int ageAt = Calc.getAgeAt(birthDay17, date);
			data.setAge(ageAt);
			data.setFinancialUpdateDate(date);
			data.setWeek(new HTWeek(date));
			data.setWage(ageWageMap.get(ageAt).getWage());
			model.list.add(data);
		}
		return model;
	}

}
