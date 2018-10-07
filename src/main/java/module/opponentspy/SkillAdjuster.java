package module.opponentspy;

import core.model.player.ISpielerPosition;
import module.opponentspy.CalcVariables.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SkillAdjuster {

	double SKILL_DELTA = 0.01; // used for skill + and - in calculation loops
	double TSI_ERROR_LIMIT = 0.1; // Accepted percentage deviation in tsi
	double WAGE_ERROR_LIMIT = 0.1; // Accepted percentage deviation in wage
	double MAX_REPEAT_COUNT = 10;
	
	private enum Direction {
		Unknown, Up, Down,
	}
	
	
	public void AdjustPlayer(CalcVariables calcPlayer) {
		
		// First, increase/decrease all skills until calculated TSI or Wage exceeds the real value.
	
		adjustUntilLimit(calcPlayer);
		
		// Check if we have spare TSI or Wage. Repeat until satisfied or we reach max count.
		
		int repeatCount = 0;
		while(!areWeSatisfied(calcPlayer) && repeatCount < MAX_REPEAT_COUNT) {
			repeatCount++;
			
			if (needMoreMainSkill(calcPlayer)) {
				// Reduce all subskills by 1. Then increase all skills until reaching wage or tsi limit.
			
				System.out.println("Need main");
				
				for (Skill skill : calcPlayer.getSkills()) {
					if (!skill.isMainSkill)
						skill.skillValue = Math.max(2, skill.skillValue - 1 * skill.priority);
				}
	
				adjustUntilLimit(calcPlayer);
			} 
			else if (needMoreSecondarySkills(calcPlayer)) {
				// Reduce mainskill by 1 and adjust all skills until limit
	
				System.out.println("Need Subs");
				
				for (Skill skill : calcPlayer.getSkills()) {
					if (skill.isMainSkill)
						skill.skillValue = Math.max(2, skill.skillValue - 1);
				}
			
				adjustUntilLimit(calcPlayer);
			}
			else {
				// We need neither? 
				System.out.println("Need neither?!");
				break;
			}
		}
		
		// TODO TEMP REMOVE
		System.out.println("RepeatCount: " + repeatCount);
	}
	
	private boolean areWeSatisfied(CalcVariables calcPlayer) {
		
		double tsiError = (0.0 + calcPlayer.tsi - calcPlayer.calculatedTSI)/calcPlayer.tsi;
		double wageError = (0.0 + calcPlayer.wage - calcPlayer.calculatedWage)/calcPlayer.wage;
		
		return (Math.abs(tsiError) < TSI_ERROR_LIMIT && Math.abs(wageError) < WAGE_ERROR_LIMIT);
	}
	
	private boolean needMoreMainSkill(CalcVariables calcPlayer) {
	
		double tsiError = (0.0 + calcPlayer.tsi - calcPlayer.calculatedTSI)/calcPlayer.tsi;
		double wageError = (0.0 + calcPlayer.wage - calcPlayer.calculatedWage)/calcPlayer.wage;
		
		
		// Only if we need more wage and Tsi is at the limit
		 
		return (wageError > WAGE_ERROR_LIMIT && Math.abs(tsiError) < TSI_ERROR_LIMIT);
	}
	
	private boolean needMoreSecondarySkills(CalcVariables calcPlayer) {
		double tsiError = (0.0 + calcPlayer.tsi - calcPlayer.calculatedTSI)/calcPlayer.tsi;
		double wageError = (0.0 + calcPlayer.wage - calcPlayer.calculatedWage)/calcPlayer.wage;
		
		
		// Only if we need more TSI and Wage is at the limit
		 
		return (tsiError > TSI_ERROR_LIMIT && Math.abs(wageError) < WAGE_ERROR_LIMIT);
	
	}
	
	private void adjustUntilLimit(CalcVariables calcPlayer) {

		Direction direction = Direction.Unknown;
	
		calculateWageAndTSI(calcPlayer);
		
		if ((calcPlayer.calculatedTSI < calcPlayer.tsi)
				&& (calcPlayer.calculatedWage < calcPlayer.wage))
			direction = Direction.Up;
		else if ((calcPlayer.calculatedTSI > calcPlayer.tsi)
				&& (calcPlayer.calculatedWage > calcPlayer.wage))
			direction = Direction.Down;
	
			
		while (!isDoneCalculating(calcPlayer, direction)) {

			adjustAllSkillsOnce(calcPlayer, direction);
			calculateWageAndTSI(calcPlayer);
		}
		
	}
	
	
	private void adjustAllSkillsOnce(CalcVariables calcPlayer, Direction direction) {
		double delta = (direction == Direction.Up) ? SKILL_DELTA
				: (0 - SKILL_DELTA);

		for (Skill skill : calcPlayer.getSkills()) {
			adjustSkillOnce(skill, delta);
		}
	}
	
	private void adjustMainSkillsOnce(CalcVariables calcPlayer, Direction direction) {
		double delta = (direction == Direction.Up) ? SKILL_DELTA
				: (0 - SKILL_DELTA);

		for (Skill skill : calcPlayer.getSkills()) {
			if (skill.isMainSkill)
				adjustSkillOnce(skill, delta);
		}
	}
	
	private void adjustSecondarySkillsOnce(CalcVariables calcPlayer, Direction direction) {
		double delta = (direction == Direction.Up) ? SKILL_DELTA
				: (0 - SKILL_DELTA);

		for (Skill skill : calcPlayer.getSkills()) {
			if (!skill.isMainSkill)
				adjustSkillOnce(skill, delta);
		}
	}

	private void adjustSkillOnce(Skill skill, double delta) {
		if (skill.skillValue >= 2) {
			double priority = skill.isMainSkill ? 1 : skill.priority;
			skill.skillValue += delta * priority;
		}
		
		skill.skillValue = Math.max(skill.skillValue, 2);
	}

	private boolean isDoneCalculating(CalcVariables calcPlayer,
			Direction direction) {
		boolean result = false;

		
		if (direction == Direction.Up) {
			if ((calcPlayer.calculatedTSI >= calcPlayer.tsi)
					|| (calcPlayer.calculatedWage >= calcPlayer.wage))
				result = true;
		} else {
			if ((calcPlayer.calculatedTSI <= calcPlayer.tsi)
					|| (calcPlayer.calculatedWage <= calcPlayer.wage))
				result = true;
		}

		return result;
	}
	
	protected void calculateWageAndTSI(CalcVariables calcPlayer) {
		
    	calculateTSI(calcPlayer);
		calculateWage(calcPlayer);	
	}
	
	private double getAgeMultiplier(int age) {

	  double yearlyDrop = 0.125;

	// 0 to 8 years of drop
	   int yearsOfDrop = Math.min(Math.max(0, age - 27), 8);
	   
	   return 1 - (yearlyDrop * yearsOfDrop);
	}
	
	private double getInjuryTSIMultiplier (int injuryStatus){
		
		 double bruisedBase = 0.05;
		 double injuryMultiplier;
		 if (injuryStatus != 0){
			 
			 injuryMultiplier = Math.min((bruisedBase + (0.1 * injuryStatus)),1);
			 
		 }
		 else injuryMultiplier = bruisedBase;
		 
		 return 1 - injuryMultiplier;
		 
	}
	
	protected int calculateTSI(CalcVariables calcPlayer) {
		return calculateTSI(calcPlayer, 0);
	}
	
	protected int calculateTSI(CalcVariables calcPlayer, double skillDelta) {
		// Factor for TSI calculation depending on skill
		double FACTOR_A = 0.985; // Playmaking,Scoring,Defending
		double FACTOR_B = 0.700; // Wing
		double FACTOR_C = 1.000; // Passing
		
		double sqrtForm = Math.sqrt(calcPlayer.form);
		double sqrtStamina = Math.sqrt(calcPlayer.stamina);
		double sqrtMultiplier = sqrtForm * sqrtStamina;
		
		double formfactorGK = (0.025 * calcPlayer.form) + 0.1;
		double scaledSkillGK = 10 * (calcPlayer.getGoalkeeping() + skillDelta - 1);
		
		double scaledSkillPowerGK = Math.pow(scaledSkillGK, 3.4);
		double powerPlaymaking = Math.pow((calcPlayer.getPlaymaking() + skillDelta - 1), 3) * FACTOR_A;
		double powerWing = Math.pow((calcPlayer.getWing() + skillDelta - 1), 3) * FACTOR_B;
		double powerScoring = Math.pow((calcPlayer.getScoring() + skillDelta - 1), 3) * FACTOR_A;
		double powerPassing = Math.pow((calcPlayer.getPassing() + skillDelta - 1), 3) * FACTOR_C;
		double powerDefending = Math.pow((calcPlayer.getDefending() + skillDelta - 1), 3) * FACTOR_A;
		double powerTotalCalc = (powerPlaymaking + powerWing + powerScoring + powerPassing + powerDefending);
		double powerTotal = Math.pow(powerTotalCalc,2)/1000;
		
		int gkTsi  = (int) ((formfactorGK * scaledSkillPowerGK) / 100);
		calcPlayer.calculatedTSI = (int) (sqrtMultiplier * powerTotal);
		if (calcPlayer.role == ISpielerPosition.KEEPER){
			
			calcPlayer.calculatedTSI = gkTsi;
		}
		
		calcPlayer.calculatedTSI *= getAgeMultiplier(calcPlayer.age);

		if (calcPlayer.injuryStatus != -1) {
			calcPlayer.calculatedTSI *= getInjuryTSIMultiplier(calcPlayer.injuryStatus);
		}
		
		return calcPlayer.calculatedTSI;
	}

	protected int calculateWage(CalcVariables calcPlayer) {
		return calculateWage(calcPlayer, 0);
	}

	private double getAgeWageDropMultiplier (int age){
		
		double yearlyWageDropPercentage = Math.max (0.1, 0.1 * (age - 27));
		double ageWageDrop = Math.min(yearlyWageDropPercentage, 1);
		
		return 1 - ageWageDrop;
	}
	
	protected int calculateWage(CalcVariables calcPlayer, double skillDelta) {

		List<Double> wageElements = new ArrayList<Double>();
		
		wageElements.add(Math.pow((Math.max(calcPlayer.getDefending() + skillDelta, 1) - 1),6.4) * 0.000830);
		wageElements.add(Math.pow((Math.max(calcPlayer.getPlaymaking() + skillDelta, 1) - 1),6.4) * 0.00104);
		wageElements.add(Math.pow((Math.max(calcPlayer.getPassing() + skillDelta, 1) - 1),6.4) * 0.000595);
		wageElements.add(Math.pow((Math.max(calcPlayer.getWing() + skillDelta, 1) - 1), 6.4) * 0.000525);
		wageElements.add(Math.pow((Math.max(calcPlayer.getScoring() + skillDelta, 1) - 1),6.4) * 0.000935);
		
		double goalkeeping = calcPlayer.getGoalkeeping() + skillDelta;
		if (goalkeeping < 14.27697) {
			wageElements.add(Math.max((((Math.exp((Math.max(goalkeeping, 1) - 1) * 0.352008)) * 130.8) + 84.18) - 250,0));
		} else {
			wageElements.add(((Math.exp((Math.max(goalkeeping, 1) - 1) * 0.247734)) * 635) - 2068.37);
		}
		
		Double maxSkillWage = Collections.max(wageElements);	
		
		double wage = maxSkillWage;
		wageElements.remove(maxSkillWage);
		
		for (double wageElement : wageElements) {
			wage += 0.5 * wageElement;
		}
		
		wage += 250; // Base wage
		
		if (calcPlayer.isPlayingAbroad){
			wage *= 1.2;
		}
		
		wage *= 10;  // euro to SEK conversion
		
		double setPieces = calcPlayer.getSetPieces() + skillDelta;
		wage *= (1 + 0.0025*setPieces);
		
		wage *= getAgeWageDropMultiplier(calcPlayer.age);
		
		calcPlayer.calculatedWage = (int)wage;
		return calcPlayer.calculatedWage;
	}
}
