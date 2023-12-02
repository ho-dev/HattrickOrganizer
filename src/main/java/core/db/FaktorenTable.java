package core.db;

import core.model.FactorObject;
import core.model.FormulaFactors;
import java.sql.Types;


public final class FaktorenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "FAKTOREN";
	
	FaktorenTable(ConnectionManager adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("PositionID").setGetter((o) -> ((FactorObject) o).getPosition()).setSetter((o, v) -> ((FactorObject) o).setPosition((byte)(int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GKfactor").setGetter((o) -> ((FactorObject) o).getGKfactor()).setSetter((o, v) -> ((FactorObject) o).setTorwart((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("DEfactor").setGetter((o) -> ((FactorObject) o).getDEfactor()).setSetter((o, v) -> ((FactorObject) o).setDefendingFactor((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("WIfactor").setGetter((o) -> ((FactorObject) o).getWIfactor()).setSetter((o, v) -> ((FactorObject) o).setWingerFactor((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PSfactor").setGetter((o) -> ((FactorObject) o).getPSfactor()).setSetter((o, v) -> ((FactorObject) o).setPassingFactor((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SPfactor").setGetter((o) -> ((FactorObject) o).getSPfactor()).setSetter((o, v) -> ((FactorObject) o).setSetPiecesFactor((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SCfactor").setGetter((o) -> ((FactorObject) o).getSCfactor()).setSetter((o, v) -> ((FactorObject) o).setTorschuss((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PMfactor").setGetter((o) -> ((FactorObject) o).getPMfactor()).setSetter((o, v) -> ((FactorObject) o).setPlaymakingFactor((float) v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NormalisationFactor").setGetter((o) -> ((FactorObject) o).getNormalizationFactor()).setSetter((o, v) -> ((FactorObject) o).setNormalizationFactor((float) v)).setType(Types.REAL).isNullable(false).build()
		};
	}

	@Override
	protected String createSelectStatement() {
		return createSelectStatement("");
	}

	void pushFactorsIntoDB(FactorObject fo) {
		if (fo != null) {
			store(fo);
		}
	}

	void getFaktorenFromDB() {
		var factors = load(FactorObject.class);
		if (!factors.isEmpty()) {
			for (var factor : factors) {
				FormulaFactors.instance().setPositionFactor(factor.getPosition(), factor);
			}
		} else {
			// use hardcoded values
			FormulaFactors.instance().importDefaults();
		}
	}
}
