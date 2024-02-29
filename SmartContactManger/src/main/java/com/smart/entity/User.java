package com.smart.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	@Column(unique = true)
	private String email;
	private String password;
	@Column(length = 500)
	private String about;
	private String role;
	private boolean enabled;
	private String imageUrl;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "user")
	private List<Contact>  contact=new ArrayList<>(); 
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "user")
	private List<Addemployee>  addemployee=new ArrayList<>(); 
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "user")
	private List<Addcustomer>  addecustomer=new ArrayList<>(); 
	
	
	
	
	
	public List<Addcustomer> getAddecustomer() {
		return addecustomer;
	}
	public void setAddecustomer(List<Addcustomer> addecustomer) {
		this.addecustomer = addecustomer;
	}
	public List<Contact> getContact() {
		return contact;
	}
	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "user")
	private List<Addstudent>  student=new ArrayList<>(); 
	
	public List<Addstudent> getStudent() {
		return student;
		
	}
	public void setStudent(List<Addstudent> student) {
		this.student = student;
	}
	
	
	
	
	public List<Addemployee> getAddemployee() {
		return addemployee;
	}
	public void setAddemployee(List<Addemployee> addemployee) {
		this.addemployee = addemployee;
	}
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(int id, String name, String email, String password, String about, String role, boolean enabled,
			String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.about = about;
		this.role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", about=" + about
				+ ", role=" + role + ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", contact=" + contact + "]";
	}
	
	
	
	
}
