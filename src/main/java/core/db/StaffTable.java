package core.db;

import core.model.StaffMember;
import core.model.StaffType;
import java.sql.Types;
import java.util.List;

public class StaffTable extends AbstractTable{
	/** tablename **/
	public final static String TABLENAME = "STAFF";

	protected StaffTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HrfID").setGetter((p) -> ((StaffMember) p).getHrfId()).setSetter((p, v) -> ((StaffMember) p).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("id").setGetter((p) -> ((StaffMember) p).getId()).setSetter((p, v) -> ((StaffMember) p).setId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("index").setGetter((p) -> ((StaffMember) p).getIndex()).setSetter((p, v) -> ((StaffMember) p).setIndex((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("stafftype").setGetter((p) -> ((StaffMember) p).getStaffType().getId()).setSetter((p, v) -> ((StaffMember) p).setStaffType(StaffType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("level").setGetter((p) -> ((StaffMember) p).getLevel()).setSetter((p, v) -> ((StaffMember) p).setLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("cost").setGetter((p) -> ((StaffMember) p).getCost()).setSetter((p, v) -> ((StaffMember) p).setCost((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("name").setGetter((p) -> ((StaffMember) p).getName()).setSetter((p, v) -> ((StaffMember) p).setName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build()
		};
	}

	@Override
	protected String createSelectStatement() {
		return createSelectStatement(" WHERE HrfID = ? ORDER BY index");
	}
	protected List<StaffMember> getStaffByHrfId(int hrfId) {
		return load(StaffMember.class, hrfId);
	}
	
	protected void storeStaff(int hrfId, List<StaffMember> list) {
		
		if ( list==null || hrfId < 0) {
			return;
		}

		executePreparedDelete(hrfId);
		int index = 0;
		for (StaffMember staff : list) {
			staff.setIndex(index++);
			staff.setHrfId(hrfId);
			staff.setIsStored(false);
			store(staff);
		}
	}
}
