package fqr.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {
	public static final String FUNC_INIT = "INIT";
	public static void main(String[] args) {
		String func = "INIT";
		if(args.length > 0) {
			func = args[0];
		}
		String user = System.getProperty("user.name");
		System.out.println("Welcome! " + user + "! It's now function '" + func + "'.");

		try { // load the driver
			Class.forName(Resources.getProperty("database.driver")).newInstance();
			System.out.println("Loaded the database driver");

			Properties props = new Properties();
			props.put("user", Resources.getProperty("database.user.name"));
			props.put("password", Resources.getProperty("database.user.password"));
			// create and connect the database named helloDB
			final Connection conn = DriverManager.getConnection(Resources.getProperty("database.url"), props);
			System.out.println("Create/Connect to " + Resources.getProperty("database.url"));
			conn.setAutoCommit(false);

			// create a table and insert two records
			Statement s = conn.createStatement();
			
			if(func.equals(FUNC_INIT)) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Resources.getInputstream("InitSQL"),"UTF-8"));

				final MutableBoolean skip = new MutableBoolean();
				bufferedReader.lines().forEach(line -> {
					if(skip.isTrue()) {
						System.out.println("** Skipping: " + line);
						return;
					}
					System.out.println("** Executing: " + line);
					try {
						s.execute(line);
					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
						skip.setTrue();
						return;
					} 
				});

				s.close();
				System.out.println("Closed SQL Statement");
				conn.commit();
				conn.close();

				if(skip.isTrue())
					System.out.println("Done the initialization. Some error occurred. Please check!");
				else
					System.out.println("Done the initialization successfully!");
				return;
			}
			// list the two records
			ResultSet rs = s
					.executeQuery("SELECT name, score FROM hellotable ORDER BY score");
			System.out.println("namettscore");
			while (rs.next()) {
				StringBuilder builder = new StringBuilder(rs.getString(1));
				builder.append("\t");
				builder.append(rs.getInt(2));
				System.out.println(builder.toString());
			}
			// delete the table
			//s.execute("drop table hellotable");
			//System.out.println("Dropped table hellotable");

			rs.close();
			s.close();
			System.out.println("Closed result set and statement");
			conn.commit();
			conn.close();
			System.out.println("Committed transaction and closed connection");

			try { // perform a clean shutdown
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} catch (SQLException se) {
				System.out.println("Database shut down normally");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("SimpleApp finished");
	}
}
