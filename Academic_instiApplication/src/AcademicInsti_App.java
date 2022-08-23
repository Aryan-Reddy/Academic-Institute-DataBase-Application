import java.sql.*;
import java.util.*;
public class AcademicInsti_App {
	Connection con;
	Statement st;
	Scanner sc;
	public void SQLError(String q)
	{
		System.out.println("SQL Query Error");
		System.out.println("The Query was: " + q);
		System.exit(1);
	}
	public int isValidDept(String deptId){
        ResultSet res;
        String query = "SELECT d.deptId FROM department as d where d.deptId = ('" + deptId + "')";
        
        try{
            res = st.executeQuery(query);
            if(res.next()) return 0;
            else{
                return -1;
            }
        }
        catch(Exception e){
            SQLError(query + e);
        }
        return 0;

    }

    public int isValidProf(String empId){
        ResultSet res;
        String query = "SELECT p.empId FROM professor as p where p.empId = ('" + empId + "')";
        try{
            res = st.executeQuery(query);
            if(res.next()) return 0;
            else{
                return -1;
            }
        }
        catch(Exception e){
            SQLError(query);
        }
        return 0;
    }

    public int isValidRollNo(String rollNo){
        ResultSet res;
        String query = "SELECT s.rollNo FROM student as s where s.rollNo = ('" + rollNo + "')";
        
        try{
            res = st.executeQuery(query);
            if(res.next()) return 0;
            else{
                return -1;
            }
        }
        catch(Exception e){
            SQLError(query);
        }
        return 0;
    }

    public int isValidCourse(String courseId){
        ResultSet res;
        String query = "SELECT t.courseId FROM teaching as t where t.courseId = ('" + courseId + "') and t.sem = 'Even' and t.year = 2006";
        
        try{
            res = st.executeQuery(query);
            if(res.next()) return 0;
            else{
                return -1;
            }
        }
        catch(Exception e){
            SQLError(query);
        }
        return 0;
    }

    int checkPreReq(String rollNo, String courseId){
        ResultSet res;
        String query = "select p.preReqCourse from prerequisite as p where p.courseId = ('" + courseId + "') and p.preReqCourse not in (select e.courseId from enrollment as e where e.year <=2006 and e.rollNo = ('" + rollNo + "') )";
        
        try{
            res = st.executeQuery(query);
            if(res.next()){
                System.out.println("Student with roll number ('" + rollNo + "') has not done the listed prerequisite courses : ");
                String coursename;
                coursename = res.getString("preReqCourse");
                System.out.println("-> " + coursename);
                while(res.next()){
                    coursename = res.getString("preReqCourse");
                    System.out.println("-> " + coursename);
                }
                System.out.println("SINCE THE STUDENT HAS NOT DONE THE ABOVE LISTED COURSES HE CANNOT BE ENROLLED :(");
                return -1;
            }
            else{
                return 0;
            }
        }
        catch(Exception e){
        	SQLError(query);
        }
        return 0;
    }
	public int modifyTeaching(String courseId,String empId,String clsrm)
	{
		String q = "select t.courseId, t.classRoom as aaa, t.empId as bbb from teaching t where t.sem =  'Even' and t.year = '2006' and t.courseId = '" + courseId + "'" ;
		ResultSet rs;
		try {
		rs = st.executeQuery(q);
		if(rs.next())
		{
			System.out.println("Teaching already present, do you want to overwrite? (Y/N)");
			String ln = sc.nextLine();
			if(ln.equals("Y"))
			{
				String updateQuery = "update teaching set classRoom = '" + clsrm + "' where empId = '" + empId + "' and courseId = '" + courseId + "' and sem = 'Even' and year = 2006";
				st.executeUpdate(updateQuery);
			}
			else if (ln.equals("N"))
			 {
				 return 0;
			 }
			 else 
			 {
				 System.out.println("Unknown Character,Exiting without changes...");
				 return 0;
			 }				

			
		}
		else
		{
			//String checkQuery = "select p.name from teaching t,professor p where t.empId = p.empId and t.empId = '" + empId + "' and courseId = '" + courseId + "' and year = '2006' and sem = 'Even'";
			String insertQuery = "insert into teaching values ('" + empId + "','" + courseId + "','Even',2006,'" + clsrm + "')";		
			try
			{
				st.executeUpdate(insertQuery);
			}
			catch(Exception e)
			{
				SQLError(insertQuery);
			}
		}
		}
		catch(Exception e)
		{
			SQLError(q + e);
		}
		return 0;
	}
	 public void addCourse(String deptId, String courseId, String empId, String classroom){
	
			
	        if(isValidProf(empId) == -1) {
	            System.out.println("There is no teacher with id = " + empId);
	            return;
	        }
	
	        String courseName, credits;
	        System.out.print("Enter Name of the course: ");
	        
	        courseName = sc.nextLine();
	        
	
	        System.out.print("Enter course credits: ");
	        
	        credits = sc.nextLine();
	        
	
	
	        String query1 = "INSERT INTO course VALUES ((\'" + courseId + "\'), (\'" + courseName + "\'), (\'" + credits + "\'), (\'" + deptId + "\'));";
	        String query2 = "INSERT INTO teaching VALUES ((\'" + empId + "\'), (\'" + courseId + "\'), 'Even', 2006, (\'" + classroom + "\'));";
	        
	        try{
	            st.executeUpdate(query1);
	            st.executeUpdate(query2);
	
	            System.out.println("New course added successfully");
	        }
	        catch (Exception e){
	            SQLError(query1 + "\n\n" + query2);
	        } 
	    }
	public void getCourseInfo()
	{
		System.out.println("Department Id: ");
		String deptId;
		while(true)
		{
			sc = new Scanner(System.in);
			deptId = sc.nextLine();
			if(isValidDept(deptId)==-1)
			{
				System.out.println("Department doesn't exist:Try again: ");
				continue;
			}
			break;
		}
		System.out.println("Course Id: ");
		
		String courseId = sc.nextLine();
		
		String checkQuery = "select * from course where courseId = '" + courseId + "'";
		try 
		{
			ResultSet res = st.executeQuery(checkQuery);
			if(res.next())
			{
				String actualDepartment = res.getString("deptNo");
				System.out.println("Course is " + res.getString("cname") );
				if(!actualDepartment.equals(deptId))
				{
					System.out.println("This course is not in department " + deptId + "\nWhat do you want to do?");
					return;
				}
				String empId;
				System.out.println("Teacher Id (Employee Id):");
				while(true)
				{
					empId = sc.nextLine();
					if(isValidProf(empId)==-1)
					{
						System.out.println("Professor doesn't Exist,Try again:");
						continue;
					}
					break;
				}
				String clsrm;
				System.out.println("ClassRoom: ");
				
				clsrm = sc.nextLine();
				updateCourse(deptId,courseId,empId,clsrm);
			}
			else
			{
				System.out.println("Adding New Course...Provide these details:");
				String empId;
				System.out.println("Teacher Id (Employee Id):");
				while(true)
				{
					empId = sc.nextLine();
					if(isValidProf(empId)==-1)
					{
						System.out.println("Professor doesn't Exist,Try again:");
						continue;
					}
					break;
				}
				String clsrm;
				System.out.println("ClassRoom: ");
				
				clsrm = sc.nextLine();
				
				addCourse(deptId,courseId,empId,clsrm);
			}
		}
		catch (Exception e)
		{
			SQLError(checkQuery + e);
		}
	}
	public void updateCourse(String deptId, String courseId, String empId, String classroom){
	    
		
	    if(isValidProf(empId) == -1) {
	        System.out.println("There is no teacher with id = " + empId);
	        return;
	    }
	    System.out.println("Do you want to modify course details too?(Y/N)");
		String ln = sc.nextLine();
		if(ln.equals("Y"))
		{
		    String courseName, credits;
		    System.out.print("Enter the updated name of the course having ID = " + courseId + " : ");
		    
		    courseName = sc.nextLine();
		    

		    System.out.print("Enter updated course credits: ");
		    
		    credits = sc.nextLine();
		    


		    String query = "UPDATE course SET cname = ('" + courseName + "'), credits = ('" + credits + "') Where courseId = ('" + courseId + "') and deptNo = ('" + deptId + "');";			
		    try{
		        st.executeUpdate(query);
		        modifyTeaching(courseId, empId, classroom);
		        System.out.println("Course details updated successfully");
		    }
		    catch (Exception e){
		    	
		        SQLError(query);
		    }
		}
		else if (ln.equals("N"))
		 {
			modifyTeaching(courseId,empId,classroom);
		 }
		 else 
		 {
			 System.out.println("Unknown Character,Exiting without changes...");
			 return;
		 }	
	    
	}
	public void enrollStudent() {
		
		
        String courseID, rollNo;
        System.out.print("Enter Student RollNo: ");
        sc = new Scanner(System.in);
		while(true)
		{
			rollNo = sc.nextLine();
			if(isValidRollNo(rollNo)==-1)
			{
				System.out.println("Student doesn't exist with that rollNo,Try again:");
				continue;
			}
			break;
		}

        System.out.print("Enter ID of course to be enrolled: ");
     
		while(true)
		{
			courseID = sc.nextLine();
			if(isValidCourse(courseID)==-1)
			{
				System.out.println("CourseId doesn't exist,Try again:");
				continue;
			}
			break;
		}
        if(checkPreReq(rollNo,courseID) == -1) {
            System.out.print("Prerequisites not met");
            return;
        }

        String query ="INSERT INTO enrollment (rollNo, courseId, sem, year) VALUES ('"+ rollNo +"', '"+ courseID +"', 'Even', 2006);";
        System.out.print("Student enrolled succesfully");

        try{
            st.executeUpdate(query);
        }
        catch (Exception e){
            System.out.print("Enrollment Failed");
            SQLError(query);
        }
        return;
    }
	public static void main(String args[])
	{
		new AcademicInsti_App().starter();
	}
	public void starter()
    {
		sc = new Scanner(System.in);
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/academic_insti","root","rootpassword");
            st = con.createStatement();
            System.out.println("HELLO WORLD!");
            System.out.println("I am AcademicInsti_App the database.");
            while(true){
                System.out.println("\nBelow are the services provided by our application : ");
                System.out.println("1 -> Add or update course details");
                System.out.println("2 -> Enroll student(s) to courses");
                System.out.println("3 -> EXIT :(");
                System.out.print("Select option (1/2/3) - ");
                int flag;
                flag = sc.nextInt();
                if(flag == 1){
                    while(true){
                        System.out.println("\nYou can now enter the details to add/update course. Please enter carefully");
                        getCourseInfo();
                        System.out.println("\nTo continue add/update course, please enter (1)");
                        System.out.println("To check the services list, please enter (0)");
                        System.out.print("Select option (1/0) - ");
                        
                        int kk = sc.nextInt();
                        
                        if(kk == 0) break;
                    }
                }
                else if(flag == 2){
                    while(true){
                        System.out.println("\nYou can now enter the details to enroll student(s). Please enter carefully");
                        enrollStudent();
                        System.out.println("\nTo continue enrolling, please enter (1)");
                        System.out.println("To check the services list, please enter (0)");
                        System.out.print("Select option (1/0) - ");
                        
                        int kk = sc.nextInt();
                        
                        if(kk == 0) break;
                    }
                }
                else {
                    System.out.println("\n THANK YOU! \n Please give your valuable feedback before leaving :)");
                    System.out.println("Rate our application on a scale of 10");
                    System.out.print("Your rating - ");
                    
                    int review = sc.nextInt();
                    

                    if(review == 10){
                        System.out.println(":)))))))) Thank you so much!");
                    }
                    else if(review > 6){
                        System.out.println(":))) Thank you!");
                    }
                    else if(review > 3){
                        System.out.println("Thank you! We'll try to improve");
                    }
                    else {
                        System.out.println("Sorry for providing such a bad experience :( anyways Thank you!");
                    }
                    break;
                }
            }

            System.out.println("TATA GOODBYE.... \nCome back soon :)");
        }
        catch(Exception e)
        {
            System.out.println("Error in Starter " + e);
        }

    }
}
