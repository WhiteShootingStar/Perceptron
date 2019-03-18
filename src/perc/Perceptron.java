package perc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.AcceptPendingException;
import java.util.Arrays;
import java.util.Scanner;

import objects.Point;

public class Perceptron {
	public double[] weights;

	public static double THRESHOLD = 0.1;
	public static double LEARNING_RATE;
	public static String ACTIVATION = null;
	public static String NON_ACTIVATION = null;
	public final double ERROR_THRESHHOLD = 0.06; // found through testing
	private Scanner scan = new Scanner(System.in);

	public Perceptron() {

		System.out.println("Please enter the learning rate");
		LEARNING_RATE = scan.nextDouble();

	}

	public void learn(String file) {
		try {

			String line;
			int err = 0;
			int lines_count = 0; // count total lines read
			int EPOCH = 0; // count epoches( amount of times testing file has been iterated);

			do {
				BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
				while ((line = reader.readLine()) != null) {
					String[] arr = line.split(",");

					Point point = makePoint(arr, 1); // create point
					if (weights == null) { // initialize weights
						weights = new double[point.value_vector.length];
						for (int i = 0; i < point.value_vector.length; i++) {
							weights[i] = 0.1;
						}
						weights[weights.length - 1] = THRESHOLD;
					}

					if (NON_ACTIVATION == null) { // find value (type) which is a non-activating ( I assume it is the
													// first one I read)
						NON_ACTIVATION = arr[arr.length - 1];
					}
					if (ACTIVATION == null && NON_ACTIVATION != null) { // find value (type) which is activating
																		// (meaning other than non-activating)
						String t = arr[arr.length - 1];
						if (!t.equals(NON_ACTIVATION)) {
							ACTIVATION = arr[arr.length - 1];
						}

					}

					err += error(point);// add errors

					modifyWeight(point); // modify weights

					lines_count++;

				}
				reader.close();
				System.out.println(Arrays.toString(weights) + " weight wector after " + EPOCH + "  epoch");
				System.out.println("Error is " + (double) err / lines_count);
				EPOCH++;

			} while ((double) err / lines_count > ERROR_THRESHHOLD);
			System.out.println(ACTIVATION + " is activation value  and " + NON_ACTIVATION + " is non-activation");
			System.out.println("Erroe is  " + (double) err / lines_count);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test(String file) {

		int a = -1;
		System.out.println("Enter 1 to perform on test set, enter 0 to perofrm on your value");
		a = scan.nextInt();

		if (a == 0) {
			System.out.println("please enter " + (weights.length - 1) + " values in one raw with comas");
			String[] arr = scan.next().split(",");
			Point p = makePoint(arr, a);
			System.out.println(printGood(p, a));
		} else {
			int line_count = 0;
			int guessed_activation = 0;
			int guessed_non_activation = 0;
			int activation_count = 0;
			int non_activation_count = 0;

			String line;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
				
				while ((line = reader.readLine()) != null) {
					line_count++;
					String[] arr = line.split(",");
					Point point = makePoint(arr, a);
					System.out.println(printGood(point, a));
					if (point.type.equals(ACTIVATION)) { // calculate guesses
						activation_count++;
						if (calculateActualOutput(point) == 1) {
							guessed_activation++;
						}
					} else {
						non_activation_count++;
						if (calculateActualOutput(point) == 0) {
							guessed_non_activation++;
						}
					}

				}
				System.out.println("Out of  " + line_count + " lines , there was guessed " + guessed_activation
						+ " out of " + activation_count + " activations and " + guessed_non_activation + " out of "
						+ non_activation_count + " non-activations" + " .The accuracy is " + (double)(guessed_activation+guessed_non_activation)/line_count);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void modifyWeight(Point inputVector) { // modify weight by the formula Weight_vector = Weight_vector + (desired output
									// - actual output)*lerning_rate* value_vector
		// int desiredOutput = Integer.parseInt(inputVector.type);
		int desiredOutput = 0;
		int actualOutput = calculateActualOutput(inputVector);
		if (inputVector.type.equals(ACTIVATION)) {
			desiredOutput = 1;
		}
		for (int i = 0; i < inputVector.value_vector.length; ++i) {
			weights[i] += (desiredOutput - actualOutput) * LEARNING_RATE * inputVector.value_vector[i];
		}
	}

	int calculateActualOutput(Point inputVector) { // calculate output by formula W^T *X >=0? 1:0 where W^T is transposition of
											// weight vector and X - values vector
		double sum = 0;
		for (int i = 0; i < inputVector.value_vector.length; i++) {
			sum += inputVector.value_vector[i] * weights[i];
		}
		
		return sum > 0 ? 1 : 0;
	}

	int error(Point inputVector) { // calculate part error by formula |Desired_output-Actual_output|
		int a = 0;
		if (inputVector.type.equals(ACTIVATION)) {
			a = 1;
		}
		return Math.abs(a - calculateActualOutput(inputVector));
	}

	Point makePoint(String[] from_inputVector, int msg_type) { // extracted method to create Point from text file
		double[] values = null;
		if (msg_type == 1) {
			values = new double[from_inputVector.length];
			for (int i = 0; i < from_inputVector.length - 1; i++) {
				values[i] = Double.parseDouble(from_inputVector[i]);
			}
			values[values.length - 1] = -1;
		} else {
			values = new double[from_inputVector.length + 1];
			for (int i = 0; i < from_inputVector.length; i++) {
				values[i] = Double.parseDouble(from_inputVector[i]);
			}
			values[values.length - 1] = -1;
		}
		if (from_inputVector[from_inputVector.length - 1] != null) {
			return new Point(values, from_inputVector[from_inputVector.length - 1]); // create point
		} else
			return new Point(values, "");
	}

	String printGood(Point inputVector, int type_mes) { // print beautifully
		int activated = calculateActualOutput(inputVector);
		String output = null;
		if (activated == 1) {
			output = ACTIVATION;
		} else
			output = NON_ACTIVATION;
		System.out.println(Arrays.toString(inputVector.value_vector));
		if (type_mes == 0) {
			return "I guess the type is " + output;
		} else
			return "Supposed output should be " + inputVector.type + " and actual output is " + output;

	}

}
