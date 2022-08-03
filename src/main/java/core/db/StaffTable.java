package core.db;

import core.model.StaffMember;
import core.model.StaffType;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StaffTable extends AbstractTable{
	/** tablename **/
	public final static String TABLENAME = "STAFF";

	protected StaffTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {

		columns = new ColumnDescriptor[7];
		columns[0] = new ColumnDescriptor("HrfID", Types.INTEGER, false);
		columns[1] = new ColumnDescriptor("index", Types.INTEGER, false);
		columns[2] = new ColumnDescriptor("id", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("stafftype", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("level", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("cost", Types.INTEGER, false);
		columns[6] = new ColumnDescriptor("name", Types.VARCHAR, false, 127);
	}

	protected List<StaffMember> getStaffByHrfId(int hrfId) {
		var list = new ArrayList<StaffMember>();
		if ( hrfId > -1) {
			String sql = "SELECT * FROM " + getTableName() + " WHERE HrfID = " + hrfId + " ORDER BY index";
			try {
				var rs = adapter.executeQuery(sql);
				if ( rs != null) {
					rs.beforeFirst();
					while (rs.next()) {
						StaffMember staff = new StaffMember();
						staff.setName(DBManager.deleteEscapeSequences(rs.getString("Name")));
						staff.setId(rs.getInt("id"));
						staff.setStaffType(StaffType.getById(rs.getInt("stafftype")));
						staff.setLevel(rs.getInt("level"));
						staff.setCost(rs.getInt("cost"));
						list.add(staff);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.getStaff Error" + e);
			}
		}
		return list;
	}
	
	protected void storeStaff(int hrfId, List<StaffMember> list) {
		
		String sql;
		
		if ( list==null || hrfId < 0) {
			return;
		}
		
		int index = 0;
		
		for (StaffMember staff : list) {
			
			try {
				sql = "INSERT INTO "
						+ getTableName()
						+ " ( HrfID, index, id, stafftype, level, cost, name ) VALUES(";
				sql += hrfId + "," + index + "," + staff.getId() + "," + staff.getStaffType().getId() + "," +
						staff.getLevel() + "," + staff.getCost() + ",'" + staff.getName() + "')";
				adapter.executeUpdate(sql);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeStaff Error " + e);
			}
		
			index++;
		}
	
	}
		
	
}
