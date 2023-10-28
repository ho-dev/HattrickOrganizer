package core.model;

import core.db.AbstractTable;

public class StaffMember extends AbstractTable.Storable implements Comparable<StaffMember> {

	private int index;

	public StaffMember(){}

	private String name;
	private int id;
	private StaffType staffType;
	private int level;
	private int cost;
	private int hrfId;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public StaffType getStaffType() {
		return staffType;
	}
	public void setStaffType(StaffType staffType) {
		this.staffType = staffType;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	@Override
	public int compareTo(StaffMember other) {
		if (this.staffType.equals(other.getStaffType())) {
			return name.compareTo(other.getName());
		} else {
			return Double.compare(staffType.getId(), other.getStaffType().getId());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getStaffType().getName() + " - " + getName() +
				" - " + HOVerwaltung.instance().getLanguageString("ls.club.staff.level") +
				": " + getLevel();
	}


	public int getHrfId() {
		return hrfId;
	}

	public void setHrfId(int hrfId) {
		this.hrfId = hrfId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
