package module.transfer.test;

import core.model.HOVerwaltung;
import core.util.HODateTime;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerWagesModel {

	private List<PlayerWage> list;

	private PlayerWagesModel() {
	}

	public static PlayerWagesModel create(int playerId, HODateTime from, HODateTime to) {
		PlayerWagesModel model = new PlayerWagesModel();
		model.list = new ArrayList<>();
		var economyDates = Calc.getUpdates(HOVerwaltung.instance().getModel().getXtraDaten().getEconomyDate(), from, to);
		List<Wage> wagesByAge = Wage.getWagesByAge(playerId);
		var ageWageMap = new HashMap<Integer, Wage>();
		for (Wage wage : wagesByAge) {
			ageWageMap.put(wage.getAge(), wage);
		}

		HODateTime birthDay17 = Calc.get17thBirthday(playerId);

		for (var date : economyDates) {
			PlayerWage data = new PlayerWage();
			int ageAt = Calc.getAgeAt(birthDay17, date);
			data.setAge(ageAt);
			data.setFinancialUpdateDate(date);
			data.setWage(ageWageMap.get(ageAt).getWage());
			model.list.add(data);
		}
		return model;
	}

}
