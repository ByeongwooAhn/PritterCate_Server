package com.example.demo.cage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.database.Cage;
import com.example.demo.utils.JwtUtil;

@RestController
public class DeleteCage {
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String dbUsername;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
	
	// 케이지 삭제
	@DeleteMapping("/cages")
	public ResponseEntity<String> deleteCage(@RequestHeader("Authorization") String requestHeader, @RequestBody Cage cageSerialNumber) {
		
		if(requestHeader != null) {
			
			String token = requestHeader;
			String username = JwtUtil.extractUsername(token);
			
			String cage_serial_number = cageSerialNumber.getCage_serial_number();
			
			String sql = "DELETE FROM cages WHERE username = ? AND cage_serial_number = ?;";
			
			try(Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
					PreparedStatement pstmt = con.prepareStatement(sql)) {
				
				pstmt.setString(1,  username);
				pstmt.setString(2, cage_serial_number);
				
				int rowsAffected = pstmt.executeUpdate();
				if(rowsAffected > 0) {
					return ResponseEntity.ok("Delete Cage Successful!");
				} else {
					return ResponseEntity.status(500).body("Failed to Delete Data.");
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return ResponseEntity.status(500).body("Error during database query execution: " + e.getMessage());
			}
		} else {
			return ResponseEntity.status(400).body("Authorization header is missing or invalid");
		}
	}
}