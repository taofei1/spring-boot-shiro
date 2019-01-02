package proxy;

import proxy.annon.AnnoInjection;

public class Test {

	public static void main(String[] args) {
		DogImp dogImp = new DogImp();
		dogImp= (DogImp) AnnoInjection.getBean(dogImp);
		System.out.println(dogImp.getName());
		dogImp.getProperty();
	}
}
