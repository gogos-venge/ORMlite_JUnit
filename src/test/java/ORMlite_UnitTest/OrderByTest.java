package ORMlite_UnitTest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class OrderByTest {
	
	private String[] fruits = {"apple", "orange", "raspberry" };

	@Test
	public void test() throws SQLException{

        String databaseUrl = "jdbc:h2:mem:test";

        //Connect to h2
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

        //Get DAO
        Dao<TestObject, String> testDao = DaoManager.createDao(connectionSource, TestObject.class);

        //Create table for TestObject
        TableUtils.createTable(connectionSource, TestObject.class);
        
        //Create random objects
        createMockData(testDao);
        
        //Get Query builder
        QueryBuilder<TestObject, String> qBuilder = testDao.queryBuilder();
        
        //Set column name
        String column = "testColumn";
        
        //Create select arg
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
        
        //Get and append results
        String result1 = appendList(qBuilder.query());
        
        /* Results. ORDER BY <columnName> IS NULL should give the null values last. But instead this query gives us this:
         * null
         * apple
         * orange
         * raspberry
         */

        //Reset for another test query
        qBuilder.reset();

        //testColumn is now hardcoded
        qBuilder.selectColumns(column).groupBy(column).orderByRaw("`testColumn` IS NULL ASC");
        
        String result2 = appendList(qBuilder.query());
        
        /* Results. When hardcoding the query, the result takes the correct form:
         * apple
         * orange
         * raspberry
         * null
         */
        
        assertEquals(result2, result1);

	}
	
	private void createMockData(Dao<TestObject, String> dao) throws SQLException{
		/* for this test, I will create 10 new TestObjects
		 * 5 of them will have the `testColumn` as null and
		 * 5 of them, a specific string. The purpose of this,
		 * is to verify that "ORDER BY `testColumn` IS NULL ASC"
		 * will sort the null group at the end of the
		 * results.
		 */
		
		for(int i = 0; i < 5; i++) {
			TestObject randomString = new TestObject();
			randomString.setTestColumn(fruits[i % fruits.length]);
			dao.create(randomString);
			
			TestObject nullTest = new TestObject();
			nullTest.setTestColumn(null);
			dao.create(nullTest);
		}
	}
	
	private <T> String appendList(List<T> list) {
		StringBuilder sb = new StringBuilder();
		for(T t : list) {
        	sb.append(t.toString() + "\n");
        }
		return sb.toString();
	}

}
