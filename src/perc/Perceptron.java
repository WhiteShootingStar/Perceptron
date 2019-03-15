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
	public final double ERROR_THRESHHOLD = 0.4; // found through testing

	public Perceptron() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter the learning rate");
		LEARNING_RATE = scan.nextDouble();
		scan.close();
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

					Point point = makePoint(arr); // create point
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
					// Point point = new Point(values, arr[arr.length - 1]); // create point
					err += error(point);

					modifyWeight(point);

					lines_count++;
					// System.out.println(Arrays.toString(weights));
					// System.out.println("Erroe is " + (double) err / lines_count);
				}
				reader.close();
				System.out.println(Arrays.toString(weights) + " weight wector after " + EPOCH + "  epoch");
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
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				Point point = makePoint(arr);
				int activated = calculateActualOutput(point);
				System.out.println(Arrays.toString(point.value_vector));
				System.out.println("Supposed output should be " + point.type + " and actual output is " + activated);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void modifyWeight(Point what) { // modify weight by the formula Weight_vector = Weight_vector + (desired output
									// - actual output)*lerning_rate* value_vector
		int desiredOutput = Integer.parseInt(what.type);

		int actualOutput = calculateActualOutput(what);
		// if (what.type.equals(ACTIVATION)) {
		// desiredOutput = 1;
		// }
		for (int i = 0; i < what.value_vector.length; ++i) {
			weights[i] += (desiredOutput - actualOutput) * LEARNING_RATE * what.value_vector[i];
		}
	}

	int calculateActualOutput(Point what) { // calculate output by formula W^T *X >=0? 1:0 where W^T is transposition of
											// weight vector and X - values vector
		double sum = 0;
		for (int i = 0; i < what.value_vector.length; i++) {
			sum += what.value_vector[i] * weights[i];
		}
		// System.out.println(sum + " " + what.toString());
		return sum > 0 ? 1 : 0;
	}

	int error(Point what) { // calculate part error by formula |Desired_output-Actual_output|
		return Math.abs(Integer.parseInt(what.type) - calculateActualOutput(what));
	}

	Point makePoint(String[] from_what) { // extracted method to create Point from text file

		double[] values = new double[from_what.length];
		for (int i = 0; i < from_what.length; i++) {
			values[i] = Double.parseDouble(from_what[i]);
		}
		values[values.length - 1] = -1;

		return new Point(values, from_what[from_what.length - 1]); // create point
	}
}
