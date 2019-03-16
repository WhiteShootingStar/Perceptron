package perc;

public class Flowers {
	public static void main(String[]args) {
		Perceptron perceptron = new Perceptron();
		perceptron.learn("trainingf.txt");
		perceptron.test("testf.txt");
	}
}
