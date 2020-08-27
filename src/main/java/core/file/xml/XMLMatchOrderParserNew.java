package core.file.xml;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLMatchOrderParserNew {

	private Document document;
	private Map<Integer, MatchRoleID> positions;
	private List<Substitution> orders;

	public XMLMatchOrderParserNew(Document doc) {
		this.document = doc;
		this.positions = new HashMap<Integer, MatchRoleID>();
		this.orders = new ArrayList<Substitution>();
		parse();
	}

	private void parse() {
		Element root = this.document.getDocumentElement();
		Element lineupElement = (Element) root.getElementsByTagName("Lineup").item(0);
		NodeList players = lineupElement.getElementsByTagName("Player");
		for (int i = 0; i < players.getLength(); i++) {
			MatchRoleID position = getPosition(players.item(i));
			this.positions.put(position.getId(), position);
		}

		NodeList nl = root.getElementsByTagName("PlayerOrders");
		if (nl != null && nl.getLength() > 0) {
			NodeList orders = ((Element) nl.item(0)).getElementsByTagName("PlayerOrder");
			for (int i = 0; i < orders.getLength(); i++) {
				this.orders.add(getOrder(orders.item(i)));
			}
		}
	}

	private Substitution getOrder(Node orderNode) {
		return new Substitution(
				Integer.parseInt(getChildValue(orderNode, "PlayerOrderID")),
				Integer.parseInt(getChildValue(orderNode, "ObjectPlayerID")),
				Integer.parseInt(getChildValue(orderNode, "SubjectPlayerID")),
				Byte.parseByte(getChildValue(orderNode, "OrderType")),
				Byte.parseByte(getChildValue(orderNode, "MatchMinuteCriteria")),
				Byte.parseByte(getChildValue(orderNode, "NewPositionId")),
				Byte.parseByte(getChildValue(orderNode, "NewPositionBehaviour")),
				RedCardCriteria.getById(Byte.parseByte(getChildValue(orderNode,"RedCardCriteria"))),
				GoalDiffCriteria.getById(Byte.parseByte(getChildValue(orderNode,"GoalDiffCriteria")))
		);
	}

	private MatchRoleID getPosition(Node playerNode) {
		int playerId = Integer.parseInt(getChildValue(playerNode, "PlayerID"));
		int roleId = Integer.parseInt(getChildValue(playerNode, "RoleID"));
		byte behaviourId = 0;

		// Behaviour is optional
		String behaviour = getChildValue(playerNode, "Behaviour");
		if (behaviour != null) {
			behaviourId = Byte.parseByte(behaviour);
		}

		return new MatchRoleID(roleId, playerId, behaviourId);
	}

	/**
	 * FOR TSTING ONLY, WILL BE REMOVED LATER.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei);
		File file = new File(
				"/home/chr/tmp/matchorders_version_1_8_matchID_353869167_isYouth_false.xml");
		new XMLMatchOrderParserNew(XMLManager.parseFile(file));
	}

	private String getChildValue(Node parent, String childTagName) {
		String value = null;
		Node node = getChild(parent, childTagName);
		if (node != null) {
			Node valNode = node.getFirstChild();
			if (valNode != null) {
				value = valNode.getNodeValue();
			}
		}
		return value;
	}

	private Node getChild(Node node, String tagName) {
		Node child = null;
		NodeList nl = ((Element) node).getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			child = nl.item(0);
		}
		return child;
	}
}
