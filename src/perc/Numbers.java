package perc;

public class Numbers {
	public static void main(String[] args) {

		
		Perceptron per = new Perceptron();
		per.learn("train.txt");
		per.test("test.txt");
	}
}
