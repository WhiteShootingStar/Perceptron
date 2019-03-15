package perc;

public class Numbers {
	public static void main(String[] args) {

		// for(int i =0;;i++) {
		// System.out.println((double) i/(i+1));
		// } proof that my error calculation works (it doesn't converge to 1)
		Perceptron per = new Perceptron();
		per.learn("train.txt");
		per.test("test.txt");
	}
}
