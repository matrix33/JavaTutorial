package com.debi.streamsdemo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JavaStreamsTestMain {

	public static void main(String[] args) {
		
		Employee e1 = new Employee(1, "Employee1", new Department(1, "Dept1"));
		Employee e2 = new Employee(2, "Employee2", new Department(1, "Dept1"));
		Employee e3 = new Employee(3, "Employee3", new Department(1, "Dept1"));
		Employee e4 = new Employee(4, "Employee4", new Department(2, "Dept2"));
		Employee e5 = new Employee(5, "Employee5", new Department(2, "Dept2"));
		Employee e6 = new Employee(6, "Employee6", new Department(2, "Dept2"));
		Employee e7 = new Employee(7, "Emp7", new Department(3, "Dept3"));
		Employee e8 = new Employee(8, "Emp8", new Department(3, "Dept3"));
		Employee e9 = new Employee(9, "Emp9", new Department(3, "Dept3"));
		Employee e10 = new Employee(10, "Emp10", new Department(3, "Dept3"));
		
		System.out.println("------------------Collection-------------------------------------------------------------------------------------");
		List<Employee> employeeList = List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
		employeeList.stream().map(e -> e.getName()).filter(e->e.startsWith("Employee")).forEach(System.out::println);
		System.out.println("------------------Arrays-------------------------------------------------------------------------------------");
		
		//from an Array
		Employee[] employeesArray = {e1, e2, e3, e4, e5, e6, e7, e8, e9, e10};
		Arrays.stream(employeesArray).filter(e->!e.getName().startsWith("Employee")).forEach(System.out::println);
		
		String[] emplArr= employeeList.stream().map(Employee :: getName ).toArray(String[] :: new);
		System.out.println(emplArr.length);
		
		System.out.println("-----------------Stream.of--------------------------------------------------------------------------------------");
		
		//static factory methods 
		System.out.println(Stream.of(employeesArray).map(e->e.getId()).reduce(0,Integer::sum));
		System.out.println(Stream.of(employeeList).count());
		
		System.out.println("---------------IntStream.range----------------------------------------------------------------------------------------");
		
		System.out.println(IntStream.range(0, 100).limit(10).skip(5).count());
		System.out.println();
		IntStream.range(0, 100).limit(10).skip(5).forEach(System.out::println);
		
		System.out.println("----------------splitAsStream---------------------------------------------------------------------------------------");
		
		//Stream.iterate(null, null)
		//Split a string
		String sentence = "a quick brown fox";
		Pattern pattern = Pattern.compile(" ");	
		pattern.splitAsStream(sentence).forEach(System.out::println);
		
		System.out.println("-------------------mapToObj------------------------------------------------------------------------------------");
		
		sentence.chars().mapToObj(c->Character.toString(c)).distinct().sorted().forEach(System.out::println);
		
		System.out.println("-------------------Text File------------------------------------------------------------------------------------");
		
		Path path = Path.of("src/main/resources/LuciferNames.txt");
		try(Stream<String> lines = Files.lines(path);){
			lines.forEach(System.out::println);
		} catch (IOException io) {
			// TODO Auto-generated catch block
			io.printStackTrace();
		}
		
		
		System.out.println("-------------------Reduce with identity element------------------------------------------------------------------------------------");
	
		System.out.println(employeeList.stream().map(e->e.getId()).reduce(Integer::sum));
		System.out.println(employeeList.stream().map(e->e.getId()).reduce(0,(a,b)->a+b));
		
		System.out.println("-------------------returning Optional------------------------------------------------------------------------------------");
		//reduction operator with no identity element returns an Optional
		//reduce, min, max, average
		//get and orElseThrow is the same method with different names
		Optional<Integer> sum = employeeList.stream().map(e->e.getId()).reduce((a,b)->a+b);
		System.out.println(sum.get());
		System.out.println(sum.orElseThrow());
		
		Optional<Integer> emptyStreamSum = employeeList.stream().filter(e->e==null).map(e->e.getId()).reduce((a,b)->a+b);
		//System.out.println(emptyStreamSum.get());
		//System.out.println(emptyStreamSum.orElseThrow()); ----throws java.util.NoSuchElementException: No value present
		if(emptyStreamSum.isPresent())System.out.println(emptyStreamSum.get());
		
		System.out.println(employeeList.stream().mapToDouble(e->e.getId()).max().orElseThrow());
		DoubleSummaryStatistics stats = employeeList.stream().mapToDouble(e->e.getId()).summaryStatistics();
		System.out.println(stats);
		
		System.out.println("-------------------Collector API------------------------------------------------------------------------------------");
		//reduction in a mutable container
		List<String> collectedEmployeeNameList = employeeList.stream().map(e -> e.getName()).filter(e->e.startsWith("Employee")).collect(Collectors.toList()); //toSet()
		System.out.println(collectedEmployeeNameList);
		
		String collectedEmployeeNameCommaSep = employeeList.stream().map(e -> e.getName()).filter(e->e.startsWith("Employee")).collect(Collectors.joining(", ", "~", "~"));
		System.out.println(collectedEmployeeNameCommaSep);
		
		System.out.println("-------------------Collector API -- GroupingBy------------------------------------------------------------------------------------");
		
		Map<String, List<Employee>> empDeptMap = employeeList
													.stream()
													.collect(Collectors.groupingBy(e->e.getDept().getName()));
		System.out.println(empDeptMap);
		
		System.out.println("-------------------Collector API -- downstream collector (post process)------------------------------------------------------------------------------------");
		
		Map<String, Long> empCountPerDept = employeeList
												.stream()
												.collect(Collectors.groupingBy(e->e.getDept().getName(), Collectors.counting()));
		System.out.println(empCountPerDept);
		
		Map.Entry<String, Long> deptWithMaxEmployees = empCountPerDept
															.entrySet()
															.stream()
															//.max(Comparator.comparing(Entry::getValue))
															.max(Entry.comparingByValue())
															.orElseThrow();
		System.out.println(deptWithMaxEmployees.getKey());
		
		Integer sumOfEmployeeIdInDept1 = empDeptMap.get("Dept1")
											.stream()
											.collect(Collectors.summingInt(Employee::getId));
		System.out.println(sumOfEmployeeIdInDept1);
		
		Map<String, Integer> sumOfEmplIDPerDept = employeeList
				.stream()
				.collect(Collectors.groupingBy(e->e.getDept().getName(), Collectors.summingInt(Employee::getId)));
		System.out.println(sumOfEmplIDPerDept);
		
	}
	
	
}
