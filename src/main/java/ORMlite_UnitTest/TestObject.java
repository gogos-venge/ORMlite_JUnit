package ORMlite_UnitTest;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TestObject {
	
	@DatabaseField
	private String testColumn;

	public String getTestColumn() {
		return testColumn;
	}

	public void setTestColumn(String testColumn) {
		this.testColumn = testColumn;
	}

}
