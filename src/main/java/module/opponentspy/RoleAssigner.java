package module.opponentspy;

import core.model.player.ISpielerPosition;
//import module.opponentspy.OpponentTeam.PlayedPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RoleAssigner {
	
	public static int getOpponentPlayerRole(OpponentPlayer player) {
		
		
		HashMap<Byte, CalcPosition> positions = new HashMap<Byte, CalcPosition>();
	
		addCalcPositions(player, positions);
		
		// Count matches played (being in the lineup)
		int playedMatches = 0;
		
//		for (PlayedPosition pos : player.getPlayedPositions()) {
//			if (pos.positionId >= ISpielerPosition.keeper && pos.positionId < ISpielerPosition.startReserves)
//				playedMatches++;
//		}
		
		
		int role = getRoleForPlayer (positions, playedMatches);		
				
			
			
		
		// Take care of set pieces?
		
		return role;
	}
	
	private static byte getRoleForPlayer(HashMap<Byte, CalcPosition> positionMap, int totalPos) {
		
		Iterator<Byte> iterator = positionMap.keySet().iterator();
		
		List<Object> tmpPositions = Arrays.asList(positionMap.values().toArray());
		
		
		
		CalcPosition position = null;
		
		while (iterator.hasNext()) {
			
			Byte role = iterator.next();
			
			CalcPosition calcPos = positionMap.get(role);
			
			if (calcPos != null) {
				
				if ((calcPos.count / totalPos) > 0.3 ) {
					position = calcPos;
					break;
				}
			}
		}
		
		if (position != null)
			return getRoleFromCalcPos(position);
		else
			return -1;
	}
	
	private static byte getRoleFromCalcPos(CalcPosition position) {
		
		switch (position.role) {
			
		case ISpielerPosition.KEEPER :
			return ISpielerPosition.KEEPER;
			
		case ISpielerPosition.BACK :
			
			if (position.defensive / position.count > 0.4 )
				return ISpielerPosition.BACK_DEF;
			
			if (position.toMiddle / position.count > 0.4)
				return ISpielerPosition.BACK_TOMID;
			
			if (position.offensive / position.count > 0.4)
				return ISpielerPosition.BACK_OFF;
			
			return ISpielerPosition.BACK;
			
		case ISpielerPosition.MIDFIELDER :
			
			if (position.defensive / position.count > 0.4 )
				return ISpielerPosition.MIDFIELDER_DEF;
			
			if (position.toWing / position.count > 0.4)
				return ISpielerPosition.MIDFIELDER_TOWING;
			
			if (position.offensive / position.count > 0.4)
				return ISpielerPosition.MIDFIELDER_OFF;
			
			return ISpielerPosition.MIDFIELDER;
			
			// TODO! Not very elegant, stupid rule?
			
		default : 
			return ISpielerPosition.CENTRAL_DEFENDER;
			
		
		}
		
	}


	private static void addCalcPositions(OpponentPlayer player, HashMap<Byte, CalcPosition> positions) {
	
		
//		int subKeeper = 0;
//		int subDefender = 0;
//		int subMidfielder = 0;
//		int subWinger = 0;
//		int subForward = 0;
		
//		for (PlayedPosition position : player.getPlayedPositions()) {
//			
//			int pos = position.positionId;
//			
//			
//			if (pos >= ISpielerPosition.startLineup && pos < ISpielerPosition.startReserves) {
//				
//				CalcPosition calcPosition = null;
//				
//				if (pos == ISpielerPosition.keeper) {
//					getCalcPosition(positions, ISpielerPosition.KEEPER);
//			
//				} else if (pos == ISpielerPosition.rightBack || pos == ISpielerPosition.leftBack) {
//					
//					calcPosition = getCalcPosition(positions, ISpielerPosition.BACK);
//						
//				} else {
//				
//					calcPosition = getCalcPosition(positions, ISpielerPosition.MIDFIELDER);
//				
//				}
//				
//				
//				calcPosition.count += 1;
//				
//				if (pos != ISpielerPosition.keeper)
//					addCalcTactic(calcPosition, position.tacticId);
//			
////			} else if (pos == ISpielerPosition.substDefender)  {
////				
////				subDefender += 1;
////			
////			} else if (pos == ISpielerPosition.substForward) {
////				
////				subForward += 1 ;
////			
////			} else if (pos == ISpielerPosition.substInnerMidfield) {
////			
////				subMidfielder += 1;
////			
////			} else if (pos == ISpielerPosition.substKeeper ) {
////			
////				subKeeper += 1;
////			
////			} else if (pos == ISpielerPosition.substWinger) {
////				
////				subWinger += 1;
////			
//			}
//			
//		}
		
		// TODO count subs somehow
	}
	
	private static void addCalcTactic(CalcPosition position, byte tactic) {
		
		switch (tactic) {
			
			case ISpielerPosition.NORMAL :
				position.normal += 1;
				break;
				
			case ISpielerPosition.OFFENSIVE :
				position.offensive += 1;
				break;
				
			case ISpielerPosition.DEFENSIVE :
				position.defensive += 1;
				break;
				
			case ISpielerPosition.TOWARDS_MIDDLE :
				position.toMiddle += 1;
				break;
				
			case ISpielerPosition.TOWARDS_WING :
				position.toWing += 1;
				break;
		}
		
		return;	
	}
	
	private static CalcPosition getCalcPosition(HashMap<Byte, CalcPosition> positions, byte role) {
		
		if (positions.containsKey(role)) {
			
			return positions.get(role);
		
		} else {
			
			CalcPosition newPos = new CalcPosition();
			
			newPos.role = role;
			positions.put(role, newPos);
			
			return newPos;
			
		}
	}
	
	private static class CalcPosition {
		
		public byte role;
		
		public int count;
		
		public int offensive;
		public int defensive;
		public int normal;
		public int toWing;
		public int toMiddle;
		
		
	}
	
}
