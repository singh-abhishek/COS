package vendorreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import org.joda.time.LocalDate;

import com.ideas.domain.Employee;
import com.ideas.utility.UtilFunctions;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class RouteReport {

	public void createPDF(ArrayList<TreeMap<Time,ArrayList<ArrayList<Object>>>> timeMaplist) {
		
		Font font1 = new Font(Font.FontFamily.HELVETICA  , 12, Font.BOLD);
    	Font font2 = new Font(Font.FontFamily.COURIER    , 14,
    			Font.UNDERLINE);
    	
		try {
			OutputStream file = new FileOutputStream(new File("C:\\"+new UtilFunctions().generateFileName()+".pdf"));

			Document document = new Document();
			PdfWriter.getInstance(document, file);

			document.open();
			
	    	
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			document.add(new Paragraph(dateFormat.format(new Date().getHours()<12? new Date() : (new LocalDate()).plusDays(1).toDate()),font1));
			for (int loopOverTimeMapList=0; loopOverTimeMapList<timeMaplist.size();loopOverTimeMapList++){
				
			Iterator iterator = timeMaplist.get(loopOverTimeMapList).keySet().iterator();
			while (iterator.hasNext()) {
				Time key = (Time) iterator.next();
				String inTimeOrOutTime = (loopOverTimeMapList==0? "In-Time: " : "Out-Time: ");
				Paragraph paragraph = new Paragraph((inTimeOrOutTime  + key),font1);
				paragraph.setSpacingBefore(20);
				document.add(paragraph);

				for (int  i= 1; i <= timeMaplist.get(loopOverTimeMapList).get(key).size(); i++) {
					Paragraph cabPara = new Paragraph(("Cab" + i),font2);
					cabPara.setSpacingBefore(15);
					document.add(cabPara);

					for (int j = 0; j < timeMaplist.get(loopOverTimeMapList).get(key).get(i - 1).size() - 1; j++) {

						String pickUpOrDrop = (loopOverTimeMapList==0?"Pick-Up Address ":"Drop Address ");
						document.add(new Paragraph(pickUpOrDrop 
								+ (j + 1)
								+ ": "
								+ ((Employee) timeMaplist.get(loopOverTimeMapList).get(key).get(i - 1)
										.get(j)).getAddress()
										.getPickUpLocation()));

						Paragraph para = new Paragraph("Contact: "
								+ ((Employee) timeMaplist.get(loopOverTimeMapList).get(key).get(i - 1)
										.get(j)).getName()
								+ " ("
								+ ((Employee) timeMaplist.get(loopOverTimeMapList).get(key).get(i - 1)
										.get(j)).getMobile() + ")");
						para.setIndentationLeft(53);
						document.add(para);

					}

				}

			}
			}
			document.close();
			file.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}