import java.io.*;
import java.util.Scanner;

public class PMGImage {

	final private String MAGIC_NUMBER = "P2";
	final private String NEW_LINE = "\r\n";

	protected int[][] data;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private int width;
	private int height;

	public int getMaxValue() {
		return maxValue;
	}

	private int maxValue;

	PMGImage(int[][] obj, int maxValue) {
		this.data = obj;
		this.height = data.length;
		this.width = data[0].length;
		this.maxValue = maxValue;
	}

	public static PMGImage createFromFile(File file) throws FileNotFoundException {
		FileInputStream fStream = new FileInputStream(file);
		Scanner scanner = new Scanner(fStream);

		scanner.nextLine(); // Skip magic number
		scanner.nextLine(); // Skip comment

		int picWidth = scanner.nextInt();
		int picHeight = scanner.nextInt();
		int maxValue = scanner.nextInt();

		int[][] pixelData = new int[picHeight][picWidth];
		for (int row = 0; row < picHeight; row++) {
			for (int col = 0; col < picWidth; col++) {
				pixelData[row][col] = scanner.nextInt();
			}
		}
		return new PMGImage(pixelData, maxValue);
	}

	public void writeToFileName(String fName) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(fName);
		DataOutputStream dataStream = new DataOutputStream(fileOutputStream);

		// Header info
		dataStream.writeBytes(MAGIC_NUMBER);
		dataStream.writeBytes(NEW_LINE);
		dataStream.write('#');
		dataStream.writeBytes(NEW_LINE);
		dataStream.writeBytes(String.valueOf(width));
		dataStream.write(' ');
		dataStream.writeBytes(String.valueOf(height));
		dataStream.writeBytes(NEW_LINE);
		dataStream.writeBytes(String.valueOf(maxValue));
		dataStream.writeBytes(NEW_LINE);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				dataStream.writeBytes(String.valueOf(data[row][col]));
				dataStream.write(' ');
			}
			if (row < height - 1) {
				dataStream.writeBytes(NEW_LINE);
			} // TODO: Find more elegant way to do this
		}

	}


}