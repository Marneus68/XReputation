package persons;
import java.util.ArrayList;
import java.util.List;


public class Person {
	protected String firstName;
	protected String lastName;

	public List<Person> friends;
	public List<Person> foes;
	
	public Person(String fname, String lname) {
		firstName = fname;
		lastName = lname;
		
		friends = new ArrayList<Person>();
		foes = new ArrayList<Person>();
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
}
