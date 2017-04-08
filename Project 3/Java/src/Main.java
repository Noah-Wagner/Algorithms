import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import Test.TestDiff;
import java.util.Scanner;

public class Main {

	//TODO: Stretch goal, add service provider implementation

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("USAGE: java seamcarve <file_name> s1 s2");
			return;
		}
		

		String fileName = args[0];
		int vertCount = Integer.parseInt(args[1]);
		int horCount = Integer.parseInt(args[2]);

//		TestDiff.compareImages(fileName, fileName);

		File file = new File(fileName);

		try {
			PMGImage image = PMGImage.createFromFile(file);
			image = SeamCarver.seamCarve(image, vertCount, 0);
			image.writeToFileName(getFileWithoutExtension(file.getName()) + "_processed.pgm");
			TestDiff.compareImages("testBalls.pgm", getFileWithoutExtension(file.getName()) + "_processed.pgm");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String getFileWithoutExtension(String fName) {
		return fName.substring(0, fName.lastIndexOf('.'));
	}

}
