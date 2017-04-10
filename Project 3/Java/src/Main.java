/*
 * Copyright (c) 2017 Noah Wagner.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import Test.TestDiff;

import java.io.File;
import java.io.IOException;

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
			image = SeamCarver.seamCarve(image, vertCount, horCount);
			image.writeToFileName(getFileWithoutExtension(file.getName()) + "_processed.pgm");
			TestDiff.compareImages("finalTest.pgm", getFileWithoutExtension(file.getName()) + "_processed.pgm");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String getFileWithoutExtension(String fName) {
		return fName.substring(0, fName.lastIndexOf('.'));
	}

}
