package doVui;

import static org.junit.Assert.assertNotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ListCellRenderer;

import org.apache.log4j.net.SocketServer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DoVuiServer {
	
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(7111);
		System.out.println("Server is started");
		Socket socket;
		while(true) {
			socket = server.accept();
			new ThreadSocket(socket).start();
		}
	}

}
class Questions {
	String question;
	String answer_a;
	String answer_b;
	String answer_c;
	String answer_d;
	String correct_answer;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer_a() {
		return answer_a;
	}
	public void setAnswer_a(String answer_a) {
		this.answer_a = answer_a;
	}
	public String getAnswer_b() {
		return answer_b;
	}
	public void setAnswer_b(String answer_b) {
		this.answer_b = answer_b;
	}
	public String getAnswer_c() {
		return answer_c;
	}
	public void setAnswer_c(String answer_c) {
		this.answer_c = answer_c;
	}
	public String getAnswer_d() {
		return answer_d;
	}
	public void setAnswer_d(String answer_d) {
		this.answer_d = answer_d;
	}
	public String getCorrect_answer() {
		return correct_answer;
	}
	public void setCorrect_answer(String correct_answer) {
		this.correct_answer = correct_answer;
	}

		
	}

class ThreadSocket extends Thread {
	boolean state,readFile;
	Socket socket;
	DataInputStream din;
	DataOutputStream dout;
	ArrayList<Questions>questions;
	public ThreadSocket(Socket socket) {
		this.socket = socket;
		this.state = true;
		this.readFile = true;
	}
	public  ArrayList<Questions> readQuestion(){
		try {
		  FileInputStream excelFile = new FileInputStream(new File("file\\Book1.xlsx"));
		  Workbook workbook = new XSSFWorkbook(excelFile);
	      Sheet datatypeSheet = workbook.getSheetAt(0);
	      DataFormatter fmt = new DataFormatter();
	      Iterator<Row> iterator = datatypeSheet.iterator();
	      Row firstRow = iterator.next();
	      Cell firstCell = firstRow.getCell(0);
	      ArrayList<Questions> listOfQuestions = new ArrayList<Questions>();
	      String question,answer_a,answer_b,answer_c,answer_d,correct_answer;
	      while (iterator.hasNext()) {
	        Row currentRow = iterator.next();
		    Questions Questions = new Questions();
		    question= fmt.formatCellValue(currentRow.getCell(0));
		    answer_a = fmt.formatCellValue(currentRow.getCell(1));
		    answer_b = fmt.formatCellValue(currentRow.getCell(2));
	        answer_c = fmt.formatCellValue(currentRow.getCell(3));
	        answer_d = fmt.formatCellValue(currentRow.getCell(4));
			correct_answer = fmt.formatCellValue(currentRow.getCell(5));
			if(question.equals("") && answer_a.equals("") && answer_b.equals("") && answer_c.equals("") && answer_d.equals("") && correct_answer.equals("")) break;
			else {
	        	Questions.setQuestion(question);
		        Questions.setAnswer_a(answer_a);
		        Questions.setAnswer_b(answer_b);
		        Questions.setAnswer_c(answer_c);
		        Questions.setAnswer_d(answer_d);
		        Questions.setCorrect_answer(correct_answer);
		        listOfQuestions.add(Questions);
		    }      
		        
		  }

		     workbook.close();
			 return listOfQuestions;
		   } catch (FileNotFoundException e) {
		      e.printStackTrace();
			    } catch (IOException e) {
		      e.printStackTrace();
		    }
		return null;
	  }

	public synchronized void question(int id,Statement stm) throws IOException {
		try {
			if(!readFile) {
				String sql2="select * from Do_vui where Id="+id;
				ResultSet rs=stm.executeQuery(sql2);
				ResultSetMetaData rsmd= rs.getMetaData();
				int t=rsmd.getColumnCount();
				while(rs.next()) {
					for(int j=2;j<=t-1;j++) {
						dout.writeUTF(String.valueOf(rs.getObject(j)));
						dout.flush();
					}	
				}
			}
			else {
				dout.writeUTF(questions.get(id).getQuestion());
				dout.flush();
				dout.writeUTF(questions.get(id).getAnswer_a());
				dout.flush();
				dout.writeUTF(questions.get(id).getAnswer_b());
				dout.flush();
				dout.writeUTF(questions.get(id).getAnswer_c());
				dout.flush();
				dout.writeUTF(questions.get(id).getAnswer_d());
				dout.flush();
			}			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void answer(int id,String x,Statement stm) throws SQLException, IOException {
		if(!readFile) {
			String sql3="select * from DOVUI where Id="+id;
			ResultSet rs1=stm.executeQuery(sql3);
			while(rs1.next()) {
				if(x.equals(rs1.getObject("DapAn"))) {
					state = true;
					dout.writeUTF("true");
					dout.flush();
				}
				else {
					state = false;
					dout.writeUTF("false");
					dout.flush();
				}
			}
		}
		else {
			if(x.equals(questions.get(id).getCorrect_answer())) {
				state = true;
				dout.writeUTF("true");
				dout.flush();
			}
			else {
				state = false;
				dout.writeUTF("false");
				dout.flush();
			}
		
		}
	}
	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			while(true) {
				String x = din.readUTF();
				state=true;
				if(x.equals("play")) {	
					Statement stm;
					if(!readFile) {
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						java.sql.Connection con=DriverManager.getConnection("jdbc:sqlserver://DESKTOP-NLC0EVM\\\\SQLEXPRESS:1433;databaseName=DoVui;integratedSecurity=true");
						stm=con.createStatement();
					}
					else {
						questions = readQuestion();
						stm = null;
					}
					ArrayList<Integer>id_question = new ArrayList<Integer>();
					Random random = new Random();
					String y;
					int stt=1;
					while(state&&stt<=5) {
						int id;
						do {
							id = random.nextInt(101);
						}while(id_question.contains(id));
						id_question.add(id);
						question(id,stm);
						y = din.readUTF();
						answer(id, y, stm);
						stt++;
					}
				}
				else {
					din.close();
					dout.close();
					break;
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}