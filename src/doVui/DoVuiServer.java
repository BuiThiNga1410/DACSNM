package doVui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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
class ThreadSocket extends Thread {
	boolean state;
	Socket socket;
	DataInputStream din;
	DataOutputStream dout;
	public ThreadSocket(Socket socket) {
		this.socket = socket;
		this.state = true;
	}
	public synchronized void question(int id,Statement stm) throws IOException {
		try {

			String sql2="select * from DOVUI where Id='"+String.valueOf(id)+"'";
			ResultSet rs=stm.executeQuery(sql2);
			ResultSetMetaData rsmd= rs.getMetaData();
			int t=rsmd.getColumnCount();
			while(rs.next()) {
				for(int j=2;j<=t-1;j++) {
					dout.writeUTF(String.valueOf(rs.getObject(j)));
					dout.flush();
				}
				
			}
			
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void answer(int id,String x,Statement stm) throws SQLException, IOException {
		String sql3="select * from DOVUI where Id='"+String.valueOf(id)+"'";
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
	public void run() {
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			while(true) {
				String x = din.readUTF();
				state=true;
				if(x.equals("play")) {	
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					java.sql.Connection con=DriverManager.getConnection("jdbc:sqlserver://DESKTOP-NLC0EVM\\\\SQLEXPRESS:1433;databaseName=DoVui;integratedSecurity=true");
					Statement stm=con.createStatement();
					int id;
					String y;
					int stt=1;
					while(state&&stt<=5) {
						id=1;
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
