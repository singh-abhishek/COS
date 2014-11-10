package vendorreport;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ideas.utility.UtilFunctions;

@SuppressWarnings("deprecation")
public class CreateExcelFile {
	
	private Connection connection;
	public CreateExcelFile(Connection connection){
		this.connection = connection;
		
	}
	public void createExcel() {
		try {
			String filename = "C:/"+new UtilFunctions().generateFileName()+".xlsx";
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet[] worksheet = new XSSFSheet[3];
			worksheet[0] = wb.createSheet("Day View");
			worksheet[1] = wb.createSheet("Week View");
			worksheet[2] = wb.createSheet("Month View");
			Map<Integer, Object[]> dataSheet1 = new TreeMap<Integer, Object[]>();
			Statement st = connection.createStatement();
			Calendar calendar = Calendar.getInstance();
			java.sql.Date[] startDate = new java.sql.Date[3];
			java.sql.Date[] endDate = new java.sql.Date[3];
			startDate[2] = startDate[1] = startDate[0] = new java.sql.Date(
					calendar.getTimeInMillis());
			calendar.add(Calendar.DATE, 1);
			endDate[0] = new java.sql.Date(calendar.getTimeInMillis());
			calendar.add(Calendar.DATE, 5);
			endDate[1] = new java.sql.Date(calendar.getTimeInMillis());
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate[2] = new java.sql.Date(calendar.getTimeInMillis());

			for (int numSheets = 0; numSheets < 3; numSheets++) {

				ResultSet rs = st
						.executeQuery("SELECT info.username, info.address, info.mobile,travel_date,EVENT,TIME,info.name FROM employee_info info INNER JOIN employee_dashboard dashboard ON (info.username = dashboard.username) "
								+ " where travel_date between '"
								+ startDate[numSheets]
								+ "' and '"
								+ endDate[numSheets] + "'");
				ResultSet rs1 = connection
						.createStatement()
						.executeQuery(
								"SELECT travel_date FROM employee_info info INNER JOIN employee_dashboard dashboard ON (info.username = dashboard.username) "
										+ " where travel_date between '"
										+ startDate[numSheets]
										+ "' and '"
										+ endDate[numSheets]
										+ "' group by travel_date");
				int i = 3, j = 3;
				String travelDate = "";
				String time = "";

				while (rs1.next()) {
					travelDate = travelDate
							+ rs1.getDate("travel_date").toString() + "," + ""
							+ ",";
					time = time + "In-Time" + "," + "Out-Time" + ",";
					worksheet[numSheets].addMergedRegion(new CellRangeAddress(
							0, 0, j, j + 1));
					j = j + 2;
				}

				while (rs.next()) {

					dataSheet1
							.put(new Integer(i++),
									new Object[] {
											rs.getString("username"),
											rs.getString("name"),
											rs.getString("address"),
											rs.getString("mobile"),
											rs.getDate("travel_date")
													.toString(),
											rs.getString("event"),
											rs.getString("time") });

				}
				String objCol1 = "" + "," + "" + "," + "" + "," + travelDate;
				String objCol2 = "Name" + "," + "Address" + "," + "Mobile"
						+ "," + time;
				String[] str1 = objCol1.split(",");
				String[] str2 = objCol2.split(",");
				dataSheet1.put(1, str1);
				dataSheet1.put(2, str2);

				XSSFCellStyle my_style = wb.createCellStyle();
				my_style.setAlignment(XSSFCellStyle.ALIGN_CENTER);

				int cellNum = 0, rowNum = 0;

				for (i = 1; i < 3; i++) {
					Row row = worksheet[numSheets].createRow(rowNum++);
					Object[] objArr = dataSheet1.get(i);
					cellNum = 0;
					for (Object obj : objArr) {
						Cell cell = row.createCell(cellNum++);
						cell.setCellStyle(my_style);
						cell.setCellValue((String) obj);
					}
				}
				String lastUserName = "";
				for (i = 3; i < dataSheet1.keySet().size() - 2; i++) {
					Object[] objArr = dataSheet1.get(i);
					if (!lastUserName.equalsIgnoreCase((String) objArr[0])) {
						Row row = worksheet[numSheets].createRow(rowNum++);
						lastUserName = (String) objArr[0];

						for (int column = 0; column < 3; column++) {
							Cell cell = row.createCell(column);
							cell.setCellValue((String) objArr[column+1]);
						}

					}

					for (int column = 3; column < cellNum; column = column + 2) {
						Row firstRow = worksheet[numSheets].getRow(0);
						Row secondRow = worksheet[numSheets].getRow(1);
						Row row = worksheet[numSheets].getRow(rowNum - 1);
						Cell cell = null;
						try {

							if (firstRow.getCell(column).getStringCellValue()
									.equalsIgnoreCase((String) objArr[4])) {
								for (int subColumn = column; subColumn <= column + 1; subColumn++) {
									if (row.getCell(subColumn) != null)
										cell = row.getCell(subColumn);
									else
										cell = row.createCell(subColumn);
									if (secondRow
											.getCell(subColumn)
											.getStringCellValue()
											.equalsIgnoreCase(
													(String) objArr[5])) {
										cell.setCellValue((String) objArr[6]);
									}
								}
							}

						}

						catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
				}

			}

			FileOutputStream fileOut = new FileOutputStream(filename);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();;

		}
	}
	
}
