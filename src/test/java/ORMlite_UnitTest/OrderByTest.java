package ORMlite_UnitTest;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class OrderByTest {

	@Test
	public void test() throws SQLException{

        String databaseUrl = "jdbc:h2:mem:test";

        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

        Dao<TestObject, String> testDao = DaoManager.createDao(connectionSource, TestObject.class);

        TableUtils.createTable(connectionSource, TestObject.class);
        
        QueryBuilder<TestObject, String> qBuilder = testDao.queryBuilder();
        
        String column = "testColumn";
        
        SelectArg selectArg = new SelectArg(SqlType.STRING, column);
        
        /* JavaDoc for orderByRaw: "args Optional arguments that correspond to any ? specified in the rawSql.
         * Each of the arguments must have the sql-type set."
         * 
         * passing "? IS NULL ASC" as a rawSql and selectArg as an argument should, based on the javadocs, produce
         * the query below:
         * "SELECT `testColumn` FROM `testobject` GROUP BY `testColumn` ORDER BY `testColumn` IS NULL ASC"
         * 
         * Instead the query still contains the ? character:
         * SELECT `testColumn` FROM `testobject` GROUP BY `testColumn` ORDER BY ? IS NULL ASC
         */
        qBuilder.selectColumns(column).groupBy(column).orderByRaw("? IS NULL ASC", selectArg);
        
        assertEquals("SELECT `testColumn` FROM `testobject` GROUP BY `testColumn` ORDER BY `testColumn` IS NULL ASC", qBuilder.prepareStatementString());
	}

}
