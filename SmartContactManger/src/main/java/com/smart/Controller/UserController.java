package com.smart.Controller;

import java.io.File;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.mysql.cj.Session;
import com.smart.dao.CityRepositoy;
import com.smart.dao.CountryRepository;
import com.smart.dao.GPSRepository;
import com.smart.dao.StateRepository;
import com.smart.dao.UserRepository;
import com.smart.dao.addemployeeRepository;
import com.smart.dao.addcustomerRepository;

import com.smart.dao.addstudentRepository;
import com.smart.dao.contactRepository;
import com.smart.entity.Cities;
import com.smart.entity.Addcustomer;
import com.smart.entity.Addemployee;
import com.smart.entity.Addstudent;
import com.smart.entity.Contact;
import com.smart.entity.GPS;
import com.smart.entity.States;
import com.smart.entity.User;

import javassist.expr.NewArray;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private CityRepositoy cityRepository;
	@Autowired
	private contactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bcPasswordEncoder;

	@Autowired
	private addemployeeRepository addemployeeRepository;

	@Autowired
	private addcustomerRepository addcustomerRepository;

	@Autowired
	private addstudentRepository addstudentRepository;

	private Object email;
	private int studentid;	
	private int employeeid;
	private Addcustomer id;

	// method adding common data to response
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();
		// getuser details
		User user = userRepository.getUserByUserName(username);
		model.addAttribute("user", user);

	}
	
	
	// home dashboard
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
        model.addAttribute("heading", "User Dashboard ");
		String username = principal.getName();
		System.err.println("username = " + username);

		// getuser details

		User user = userRepository.getUserByUserName(username);

		model.addAttribute("user", user);
		return "normal/user_dashboard";
	}

	@GetMapping("/add_contact")
	public String addContact(Model model) {
		model.addAttribute("title", "About");
		model.addAttribute("contact", new Contact());
		model.addAttribute("countryList", countryRepository.findAll());
		model.addAttribute("heading", "Add Contact ");

		return "normal/add_contact_form"; 
	}
	
	@GetMapping("/add_customer")
	public String addCustomer(Model model) {
		model.addAttribute("title", "Add customer");
		model.addAttribute("customer", new Addcustomer());
		model.addAttribute("heading", "Add Customer");

		return "normal/add_customer"; 
	}
	
	
	@PostMapping("/process_customer")
	public String customer_process(@ModelAttribute Addcustomer addcustomer,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			
			addcustomer.setUser(user);
			user.getAddecustomer().add(addcustomer);
			userRepository.save(user);
			System.err.println("user Data " + addcustomer);
			session.setAttribute("message", new com.smart.helper.Message("Your customer added", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new com.smart.helper.Message("something went wrong", "danger"));
		}
		return "normal/add_customer";
	}

	// show customer
	@GetMapping("/show_customer/{page}")
	public String showCustomer(@PathVariable(name = "page") Integer page,
	                          @RequestParam(name = "customername", required = false) String customerName,
	                          Model model, Principal principal) {
	    model.addAttribute("title", "View Customer");
	    model.addAttribute("heading", "View Customer");
	    String userName = principal.getName();
	    User user = userRepository.getUserByUserName(userName);

  

	    Pageable pageable = PageRequest.of(page, 2);

	    Page<Addcustomer> addcustomer;
	    if (customerName != null) {
	    	addcustomer = addcustomerRepository.findByNameContainAndUser(customerName, user, pageable);
	    } else {
	    	addcustomer = addcustomerRepository.findCustomerByUser(user.getId(), pageable);
	    }

	    model.addAttribute("customer", addcustomer.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", addcustomer.getTotalPages());

	    return "normal/show_customer";
	}
	
	//update employee
    @RequestMapping("/{employeeid}/edit1")
    public String showUpdateForm1(@PathVariable Integer employeeid, Model model ) {
        Addemployee employee= addemployeeRepository.findById(employeeid).orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + employeeid));
        
     
        model.addAttribute("employee",employee);
        model.addAttribute("heading", "Update Employee");
        return "/normal/update_emp";
  
    }

    
    
    
    
    @RequestMapping("/update_customer")
    public String updatecust( @ModelAttribute Addcustomer newcustomer,@RequestParam Integer customerid, Principal principal) {
    	Addcustomer customer = addcustomerRepository.findById(employeeid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + employeeid));

        String name = principal.getName();
    		User user = userRepository.getUserByUserName(name);
    			
        
    		customer.setName(newcustomer.getName());
    		customer.setEmail(newcustomer.getEmail());
    		customer.setName(newcustomer.getName());
    		customer.setMobileno(newcustomer.getMobileno());
    		
       addcustomerRepository.save(newcustomer);
        
       customer.setUser(user);
        userRepository.save(user);
        
        
        return "redirect:/user/show_customer/0";
    }

	
	// process to add contact
	@PostMapping("/process_contact")
	public String contact_process(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			if (!file.isEmpty()) {

				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			contact.setUser(user);
			user.getContact().add(contact);
			userRepository.save(user);
			System.err.println("user Data " + contact);
			session.setAttribute("message", new com.smart.helper.Message("Your Contact added", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new com.smart.helper.Message("something went wrong", "danger"));
		}
		return "normal/add_contact_form";
	}

	// show contact handler
	// per page =5[n]
	// current page=0[page]

//	@GetMapping("/show_contact/{page}")
//	public String showContact(@PathVariable("page") Integer page,
//			@RequestParam(name = "contact_name", required = false) String contactName, Model model,
//			Principal principal) {
//		model.addAttribute("title", "View Contacts");
//		model.addAttribute("heading", "View Contacts");
//		String userName = principal.getName();
//		User user = userRepository.getUserByUserName(userName);
//
//		Pageable pageable = PageRequest.of(page, 2);
//
//		Page<Contact> contacts;
//		if (contactName != null) {
//			contacts = contactRepository.findByNameContainingAndUser(contactName, user, pageable);
//		} else {
//			contacts = contactRepository.findContactByUser(user.getId(), pageable);
//		}
//
//		model.addAttribute("contacts", contacts.getContent());
//		model.addAttribute("currentPage", page);
//		model.addAttribute("totalPages", contacts.getTotalPages());
//
//		return "normal/show_contacts";
//	}
	


	
	
	
	@GetMapping("/show_contact/{page}")
	public String showContact(@PathVariable(name = "page") Integer page,
	                          @RequestParam(name = "contact_name", required = false) String contactName,
	                          Model model, Principal principal) {
	    model.addAttribute("title", "View Contacts");
	    model.addAttribute("heading", "View Contacts");
	    String userName = principal.getName();
	    User user = userRepository.getUserByUserName(userName);

  

	    Pageable pageable = PageRequest.of(page, 2);

	    Page<Contact> contacts;
	    if (contactName != null) {
	        contacts = contactRepository.findByNameContainingAndUser(contactName, user, pageable);
	    } else {
	        contacts = contactRepository.findContactByUser(user.getId(), pageable);
	    }

	    model.addAttribute("contacts", contacts.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", contacts.getTotalPages());

	    return "normal/show_contacts";
	}

	

	// show specific contact
	@RequestMapping("/{cId}/contact")
	public String showPerticularContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}

	// show specific contact by name
	@RequestMapping("/serach_contact_name")
	public String listofContactByName(Model model, Principal principal,
			@RequestParam("contact_name") String contactName) {
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		List<Contact> contactList = contactRepository.findByNameContainingAndUser(contactName, user);
		model.addAttribute("contacts", contactList);
		return "normal/show_contacts";
	}

	// delete contact

	@GetMapping("/{cId}/delete")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal,
			HttpSession session) {

		Contact contact = this.contactRepository.findById(cId).get();
		contact.setUser(null);
		this.contactRepository.delete(contact);

		session.setAttribute("message", new com.smart.helper.Message("Contact Deleted Successfuly", "success"));

		return "redirect:/user/show_customer/0";
	}

	// Update contact Data
	@PostMapping("/{cId}/update")
	public String contactData(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("title", "Update Contacts");
		Contact contact = contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		model.addAttribute("countryList", countryRepository.findAll());
		model.addAttribute("cityList", cityRepository.getAllCity(contact.getStateId()));
		model.addAttribute("stateList", stateRepository.getAllState(contact.getCountryId()));
		return "normal/updata_form";
	}

	// process update contact
	@PostMapping("/process_update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		// get old contact detail
		Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();
		try {
			if (!file.isEmpty()) {

				// deleting old contact detail image
				try {
					File deleteFile = new ClassPathResource("static/images").getFile();
					File confirmDelete = new File(deleteFile, oldContactDetails.getImage());
					confirmDelete.delete();
				}
				catch(Exception x) {
					
				}
				
				// adding new contact detail image
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				User user = userRepository.getUserByUserName(principal.getName());
				contact.setUser(user);
				contactRepository.save(contact);
				session.setAttribute("message", new com.smart.helper.Message("Update Successfuly", "success"));
			} else {
				User user = userRepository.getUserByUserName(principal.getName());
				contact.setImage(oldContactDetails.getImage());
				contact.setUser(user);
				contactRepository.save(contact);
				session.setAttribute("message", new com.smart.helper.Message("Update Successfuly", "success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getcId() + "/contact";
	}
	
	// View Your Profile
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("heading", "View Profile");
		return "normal/profile";
	}
	
	
	
		
	
	//show Employee
		@GetMapping("/show_employee/{page}")
		public String showEmployee(@PathVariable("page") Integer page,
				@RequestParam(name = "employee_name", required = false) String employeeName, Model model,
				Principal principal) {
			model.addAttribute("title", "View Employees");
			model.addAttribute("heading", "Employee Details ");
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			
		//	 int userId = 5;
			
			Pageable pageable = PageRequest.of(page, 2);

			Page<Addemployee> employees;
			if (employeeName != null) {
				
				employees= addemployeeRepository.findByNameContainAndUser(employeeName, user, pageable);
			} else {
				
				employees= addemployeeRepository.findEmployeeByUser(user.getId(), pageable);
				
			}

			//List<Addemployee> employee = addemployeeRepository.findAll();
			//model.addAttribute("employees",employee);
              
			model.addAttribute("employees", employees.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", employees.getTotalPages());

			return "normal/show_employee";
		}

		 
		
		

		
		
		
		
	  
	  //delete employee	
		@GetMapping("/{employeeid}/deleteemp")
		public String deleteEmployee(@PathVariable("employeeid") Addemployee employeeid) {
			 addemployeeRepository.delete(employeeid);
			return "redirect:/user/show_employee/0";
		}
	   
	
		
		
		
		// show specific employee by name
		@RequestMapping("/search_employee_name")
		public String listofEmployeeByName(Model model, Principal principal,
				@RequestParam("employee_name") String employeeName) {
			String username = principal.getName();
			User user = userRepository.getUserByUserName(username);
			
			List<Addemployee > employeeList= addemployeeRepository.findByNameContainAndUser(employeeName, user);
			model.addAttribute("employees", employeeList);
			
			return "normal/show_employee";
		}
	
		

		
		
//		// show specific employee
//		@RequestMapping("/{Id}/employee")
//		public String showPerticularEmployee(@PathVariable("Id") Integer Id, Model model, Principal principal) {
//		
//			Optional<Addemployee> employeeOptional =addemployeeRepository.findById(Id);
//			Addemployee employee = employeeOptional.get();
//			String username = principal.getName();
//			User user = userRepository.getUserByUserName(username);
//			if (user.getId() == employee.getUser().getId()) {
//				model.addAttribute("employee", employee);
//			}
//			return "normal/employee_detail";
//		}
	
	
	
//	// add employee
//		@GetMapping("/addemployee")
//		public String addemployee1(Model model) {
//			model.addAttribute("title", "About");
//			model.addAttribute("employee", new Addemployee());
//			model.addAttribute("heading", "Add Employee ");
//			return "normal/addemployeeform";
//		}
//
//
//
//	@PostMapping("/process_addemployee")
//	public String addemployee_process(@ModelAttribute Addemployee addemployee, Principal principal, HttpSession session,
//			Model model) {
//		String name = principal.getName();
//		User user = userRepository.getUserByUserName(name);
//		System.out.println("checking == " + addemployee.getEmail());
//
//		try {
//		
//			boolean checkDuplicate = true;
//			List<Addemployee> addemployee1 = addemployeeRepository.findAll();
//			
//			if (addemployee1 != null) {
//				System.out.println("checking the list is null");
//			}
//			for (Addemployee add : addemployee1) {
//				if (add.getEmail() == null) {
//					System.out.println("this is null email");
//				}
//				if (add.getEmail().equals(addemployee.getEmail())) {
//					model.addAttribute("emailerror", "duplicate email id");
//					System.out.println("Email ID '" + addemployee.getEmail() + "' already exists!");
//					System.out.println("Try another email address");
//					checkDuplicate = false;
//					System.err.println("error 470");
//
//					return "normal/addemployeeform";
//
//				} else {
//					checkDuplicate = true;
//					System.err.println("error 475");
//				}
//
//			}
//System.err.println(checkDuplicate);
//			if (checkDuplicate) {
//				addemployee.setUser(user);
//				addemployeeRepository.save(addemployee);
//			addemployee.setUser(user);
//			System.err.println("error 484");
//
//			addemployeeRepository.save(addemployee);
//				
//			}
//
//		} catch (Exception e) {
//			System.out.println("Ok");
//			System.err.println(addemployee.getName());
//			e.printStackTrace();
//
//		}
//
//		return "normal/addemployeeform";
//	}
//
		
		
		
		
		// add employee
				@GetMapping("/addemployee")
				public String addemployee1(Model model) {
					model.addAttribute("title", "About");
					model.addAttribute("employee", new Addemployee());
					model.addAttribute("heading", "Add Employee ");
					return "normal/addemployeeform";
				}



			@PostMapping("/process_addemployee")
			public String addemployee_process(@ModelAttribute Addemployee addemployee, Principal principal, HttpSession session,
					Model model) {
				String name = principal.getName();
				User user = userRepository.getUserByUserName(name);
				
				addemployee.setUser(user);
				user.getAddemployee().add(addemployee);
				userRepository.save(user);
				
				
				
				try {
				
					boolean checkDuplicate = false;
					List<Addemployee> addemployee1 = addemployeeRepository.findAll();
					
					for (Addemployee add : addemployee1) {
						if (add.getEmail() == null) {
							System.out.println("this is null email");
						}
						if (add.getEmail().equals(addemployee.getEmail())) {
							model.addAttribute("emailerror", "duplicate email id");
							checkDuplicate = false;
							return "normal/addemployeeform";

						} else {
							checkDuplicate = true;
						}

					}

					if (checkDuplicate) {
						addemployeeRepository.save(addemployee);

					}

				} catch (Exception e) {
					
					e.printStackTrace();

				}

				return "normal/addemployeeform";
			}
			
			
			// delete emp

			@GetMapping("/{Id}/delete")
			public String deleteEmp(@PathVariable("Id") Integer cId, Model model, Principal principal,
					HttpSession session) {

				Addemployee addemploye = this.addemployeeRepository.findById(cId).get();
				addemploye.setUser(null);
				this.addemployeeRepository.delete(addemploye);

				session.setAttribute("message", new com.smart.helper.Message("Employee Deleted Successfuly", "success"));

				return "redirect:/user/show_employe/0";
			}
		
		
		
		
		
		
		
		
		
	
	//show student
	@GetMapping("/show_student/{page}")
	public String showStudent(@PathVariable("page") Integer page,
			@RequestParam(name = "student_name", required = false) String studentName, Model model,
			Principal principal) {
		model.addAttribute("title", "View Students");
		model.addAttribute("heading", "Student Details ");
		User user = userRepository.getUserByUserName(principal.getName());
		Pageable pageable = PageRequest.of(page, 2);
		

		
		  Page<Addstudent> students;
		  if (studentName != null)
		  { students =
		  addstudentRepository.findByNameContainingAndUser(studentName, user,
		  pageable); } 
		  else 
		  { students =
		  addstudentRepository.findStudentByUser(user.getId(), pageable); 
		  }
		 
		//Page<Addstudent> students=null;
		System.out.println(addstudentRepository.findAll());
		List<Addstudent> student = addstudentRepository.findAll();

		model.addAttribute("students", students.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", students.getTotalPages());
		
		

		return "normal/show_student";
	}

	  //update student
    @RequestMapping("/{studentid}/edit")
    public String showUpdateForm(@PathVariable Integer studentid, Model model) {
        Addstudent student = addstudentRepository.findById(studentid).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentid));
        model.addAttribute("student", student);
	 model.addAttribute("heading", "Update Student");
        return "/normal/update_stud";
    }

    @PostMapping("/update_student")
    public String updateStuden( @ModelAttribute Addstudent newStudent, Principal princpal) {
      //  Addstudent oldStudent = addstudentRepository.findById(studentid)
                //.orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentid));

	User user = userRepository.getUserByUserName(princpal.getName());
	newStudent.setUser(user);
	user.getStudent().add(newStudent);
	
	userRepository.save(user);
//		  oldStudent.setName(newStudent.getName());
//		  oldStudent.setEmail(newStudent.getEmail());
//		  oldStudent.setAddress(newStudent.getAddress());
//		  oldStudent.setMobileno(newStudent.getMobileno());
		 
		  

  	  return "redirect:/user/show_student/0";
  
    }	
	//delete student	
	@GetMapping("/{studentid}/deleteStud")
	public String deleteStudent(@PathVariable("studentid") Addstudent studentid) {
		 addstudentRepository.delete(studentid);
		return "redirect:/user/show_student/0";
	}
	
	
	@GetMapping("/addstudent")
	public String addstudent1(Model model) {
		model.addAttribute("title", "About");
		model.addAttribute("addstudent", new Addstudent());
		model.addAttribute("heading", "Add Student ");

		return "normal/addstudent";
	}



	@RequestMapping("/process_addstudent1")
	public String addstudent_process(@ModelAttribute Addstudent addstudent, Principal principal, HttpSession session,
			Model model) {
		
		String originalTimestamp = addstudent.getDateandtime();
		System.out.println("timeanddate="+addstudent.getDateandtime());
		LocalDateTime dateTime = LocalDateTime.parse(originalTimestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a ");
		String formattedTimestamp = dateTime.format(customFormatter);
		 System.out.println("Original timestamp: " + originalTimestamp);
	        System.out.println("Formatted timestamp: " + formattedTimestamp);
	        
	        String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			
			addstudent.setUser(user);
			user.getStudent().add(addstudent);
			userRepository.save(user);	
			
	        
	        addstudent.setDateandtime(formattedTimestamp);

		System.out.println("checking == " + addstudent.getEmail());

		try {
			boolean checkDuplicate = false;
			List<Addstudent> addstudent1 = addstudentRepository.findAll(); // Fetch existing students
			if (addstudent1 != null) {
				System.out.println("checking the list is null");
			}

			for (Addstudent add : addstudent1) {
				if (add.getEmail() == null) {
					checkDuplicate = false;
				} else if (add.getEmail().equals(addstudent.getEmail())) {
					model.addAttribute("emailerror", "duplicate email id");
					System.out.println("Email ID '" + addstudent.getEmail() + " already exists!");
					System.out.println("Try another email address");
					checkDuplicate = false;
					return "normal/addstudent";
				} else {
					checkDuplicate = true;
				}
			}

			if (checkDuplicate) {
				
				
				  addstudent.setUser(user); user.getStudent().add(addstudent);
				  addstudentRepository.save(addstudent);
			}

		} catch (Exception e) {
			System.out.println("Ok");
			System.err.println(addstudent.getName());
			e.printStackTrace();
		}

		return "normal/addstudent";
	}
	
	
	
	
	

	@GetMapping("/changePassword")
	public String openSetting(Model model) {
		model.addAttribute("heading", "Change PassWord ");
		return "normal/ChangePassword";
	}

	// change password
	@GetMapping("/changePasswordProcess")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("NewPassword") String NewPassword, Principal principal, HttpSession session, Model model) {
		String userName = principal.getName(); // this pass the login user email id
		User user = userRepository.getUserByUserName(userName);
		System.err.println("oldPassword" + oldPassword);
		System.err.println("NewPassword" + NewPassword);

		System.out.println(user.getPassword());
		if (bcPasswordEncoder.matches(oldPassword, user.getPassword())) {

			user.setPassword(bcPasswordEncoder.encode(NewPassword));
			userRepository.save(user);
			System.err.println("enter correct password");
			session.setAttribute("message", new com.smart.helper.Message("Password Changed Successfuly", "success"));
			return "redirect:/user/index";

		} else {
			session.setAttribute("message", new com.smart.helper.Message("Password Not Match", "danger"));
			model.addAttribute("changeError", true);
			System.err.println("enter wrong password");
			return "normal/ChangePassword";
		}

	}
	
	// get map loaction

	@GetMapping("/state/{id}")
	public ResponseEntity<?> addState(@PathVariable("id") Integer id, Model model) {
		System.err.println("country id:" + id);

		List<States> state = stateRepository.getAllState(id);
		return ResponseEntity.ok(state);
	}

	@GetMapping("/city/{id}")
	public ResponseEntity<?> addCity(@PathVariable("id") Integer id, Model model) {

		List<Cities> city = cityRepository.getAllCity(id);
		return ResponseEntity.ok(city);
	}

     
     //get map loacation 
     
     @Autowired
     private GPSRepository gpsRepository;
     
     @RequestMapping("/getMap")
     public String getMap(Model model) {
    	 List<GPS> locations = gpsRepository.findAll();
    	 model.addAttribute("heading", "Map ");
         model.addAttribute("locations", locations);
    	 return "normal/map";
     }
     
     
     // add customer

	
				@GetMapping("/addcustomer")
				public String addcustomer1(Model model) {
					model.addAttribute("title", "About");
					model.addAttribute("cutsomer", new Addcustomer());
					model.addAttribute("heading", "Add Customer");
					return "normal/addecustomerform";
				}

     




//Update customer Data
	@PostMapping("/{id}/update_customer")
	public String customerUpdate(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("title", "Update Customer");
		Addcustomer addcustomer = addcustomerRepository.findById(id).get();
		model.addAttribute("customer", addcustomer);
		return "normal/update_customer";
	}
	
	// update employee
	@RequestMapping("/{id}/update_employee")
	public String employeeUpdate(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("title", "Update employee");
		Addemployee addemploye = addemployeeRepository.findById(id).get();
		model.addAttribute("addemploye ", addemploye );
		return "normal/update_employee";
	}
	
	 @PostMapping("/process_employee")
		public String updateCustomer(@ModelAttribute Addemployee addemployee,
				Principal principal, HttpSession session) {
			try {
				String name = principal.getName();
				User user = userRepository.getUserByUserName(name);
				
				addemployee.setUser(user);
				user.getAddemployee().add(addemployee);
				userRepository.save(user);
				System.err.println("user Data " + addemployee);
				session.setAttribute("message", new com.smart.helper.Message("Your employee added", "success"));
			} catch (Exception e) {
				e.printStackTrace();
				
				session.setAttribute("message", new com.smart.helper.Message("something went wrong", "danger"));
			}
				return "redirect:/user/update_employee/0";
		}
	
	
	
	
	
	
	
	
	
	 //update customer
    @RequestMapping("/{customer}/id")
    public String showUpdateCustForm(@PathVariable Integer id, Model model) {
        Addcustomer customer = addcustomerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentid));
        model.addAttribute("customer", customer);
	 model.addAttribute("heading", "Update Customer");
        return "/normal/update_customer";
    }
    @PostMapping("/process_updateCustomer")
	public String updateCust(@ModelAttribute Addcustomer addcustomer,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			
			addcustomer.setUser(user);
			user.getAddecustomer().add(addcustomer);
			userRepository.save(user);
			System.err.println("user Data " + addcustomer);
			session.setAttribute("message", new com.smart.helper.Message("Your customer added", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new com.smart.helper.Message("something went wrong", "danger"));
		}
		return "redirect:/user/show_customer/0";
	}
    
    
    //delete customer	
	@GetMapping("/{customerid}/deletecust")
	public String cust_del(@PathVariable("customerid") Integer customerid) {
		 addcustomerRepository.deleteById(customerid);
		return "redirect:/user/show_customer/0";
	}
	
	
 	

			
//update customer
@RequestMapping("/{employee}/id")
public String showUpdateEmpForm(@PathVariable Integer id, Model model) {
    Addemployee addemploye = addemployeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentid));
    model.addAttribute("addemploye", addemploye);
 model.addAttribute("heading", "Update addemploye");
    return "/normal/update_employee";
}

@PostMapping("/process_updateemp")
public String emp_process(@ModelAttribute Addemployee emp, 
		Principal principal, HttpSession session) {
	try {
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		emp.setUser(user);
		user.getAddemployee().add(emp);
		userRepository.save(user);
		System.err.println("user Data " + emp);
		session.setAttribute("message", new com.smart.helper.Message("Your Contact added", "success"));
	} catch (Exception e) {
		e.printStackTrace();
		session.setAttribute("message", new com.smart.helper.Message("something went wrong", "danger"));
	}
	return "normal/addemployeeform";
}
}
	
			
			