package module.opponentspy;

import core.model.player.IMatchRoleID;
//import module.opponentspy.OpponentTeam.PlayedPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RoleAssigner {
	
	public static int getOpponentPlayerRole(OpponentPlayer player) {
		
		
		HashMap<Byte, CalcPosition> positions = new HashMap<Byte, CalcPosition>();
	
		addCalcPositions(player, positions);
		
		// Count matches played (being in the lineup)
		int playedMatches = 0;
		
//		for (PlayedPosition pos : player.getPlayedPositions()) {
//			if (pos.positionId >= IMatchRoleID.keeper && pos.positionId < IMatchRoleID.startReserves)
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
			
		case IMatchRoleID.KEEPER :
			return IMatchRoleID.KEEPER;
			
		case IMatchRoleID.BACK :
			
			if (position.defensive / position.count > 0.4 )
				return IMatchRoleID.BACK_DEF;
			
			if (position.toMiddle / position.count > 0.4)
				return IMatchRoleID.BACK_TOMID;
			
			if (position.offensive / position.count > 0.4)
				return IMatchRoleID.BACK_OFF;
			
			return IMatchRoleID.BACK;
			
		case IMatchRoleID.MIDFIELDER :
			
			if (position.defensive / position.count > 0.4 )
				return IMatchRoleID.MIDFIELDER_DEF;
			
			if (position.toWing / position.count > 0.4)
				return IMatchRoleID.MIDFIELDER_TOWING;
			
			if (position.offensive / position.count > 0.4)
				return IMatchRoleID.MIDFIELDER_OFF;
			
			return IMatchRoleID.MIDFIELDER;
			
			// TODO! Not very elegant, stupid rule?
			
		default : 
			return IMatchRoleID.CENTRAL_DEFENDER;
			
		
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
//			if (pos >= IMatchRoleID.startLineup && pos < IMatchRoleID.startReserves) {
//				
//				CalcPosition calcPosition = null;
//				
//				if (pos == IMatchRoleID.keeper) {
//					getCalcPosition(positions, IMatchRoleID.KEEPER);
//			
//				} else if (pos == IMatchRoleID.rightBack || pos == IMatchRoleID.leftBack) {
//					
//					calcPosition = getCalcPosition(positions, IMatchRoleID.BACK);
//						
//				} else {
//				
//					calcPosition = getCalcPosition(positions, IMatchRoleID.MIDFIELDER);
//				
//				}
//				
//				
//				calcPosition.count += 1;
//				
//				if (pos != IMatchRoleID.keeper)
//					addCalcTactic(calcPosition, position.tacticId);
//			
////			} else if (pos == IMatchRoleID.substDefender)  {
////				
////				subDefender += 1;
////			
////			} else if (pos == IMatchRoleID.substForward) {
////				
////				subForward += 1 ;
////			
////			} else if (pos == IMatchRoleID.substInnerMidfield) {
////			
////				subMidfielder += 1;
////			
////			} else if (pos == IMatchRoleID.substKeeper ) {
////			
////				subKeeper += 1;
////			
////			} else if (pos == IMatchRoleID.substWinger) {
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
			
			case IMatchRoleID.NORMAL :
				position.normal += 1;
				break;
				
			case IMatchRoleID.OFFENSIVE :
				position.offensive += 1;
				break;
				
			case IMatchRoleID.DEFENSIVE :
				position.defensive += 1;
				break;
				
			case IMatchRoleID.TOWARDS_MIDDLE :
				position.toMiddle += 1;
				break;
				
			case IMatchRoleID.TOWARDS_WING :
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
