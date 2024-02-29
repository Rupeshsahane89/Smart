package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.dao.contactRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;

@RestController //this is use to show response body
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private contactRepository contactRepository;
	//search Handler
	@GetMapping("/serach/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contact=this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contact);
		
	}
	


}
