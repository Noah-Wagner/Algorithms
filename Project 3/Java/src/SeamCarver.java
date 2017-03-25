/**
 * Created by noah on 3/19/17.
 */
class SeamCarver {

	static class PathObj {
		int energy;
		PATH path;

		PathObj(int energy, PATH path) {
			this.energy = energy;
			this.path = path;
		}
	}


	static PMGImage seamCarve(PMGImage image, int vertical, int horizontal) {

		int[][] energy = calculateEnergyGrid(image);

		for (int i = 0; i < vertical; i++) {
			image = carveVertical(image, energy);
		}

		for (int i = 0; i < horizontal; i++) {
			image = carveHorizontal(image, energy);
		}

		return image;
	}

	private static PMGImage carveHorizontal(PMGImage image, int[][] energy) {
		return null;
	}

	private static PMGImage carveVertical(PMGImage image, int[][] energy) {

		PathObj[][] energyPath = new PathObj[image.getHeight()][image.getWidth()];

		for (int i = 0; i < image.getWidth(); i++) {
			energyPath[0][i] = new PathObj(energy[0][i], null);
		}

		for (int row = 1; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				int d1, d2, d3;
				try {
					d1 = energyPath[row - 1][col - 1].energy;
				} catch (Exception e) {
					d1 = Integer.MAX_VALUE;
				}
				try {
					d2 = energyPath[row - 1][col].energy;
				} catch (Exception e) {
					d2 = Integer.MAX_VALUE;
				}
				try {
					d3 = energyPath[row - 1][col + 1].energy;
				} catch (Exception e) {
					d3 = Integer.MAX_VALUE;
				}

				int nrg = energy[row][col];
				PATH path;
				if (d1 <= d2 && d1 <= d3) {
					nrg += d1;
					path = PATH.UPLEFT;
				} else if (d2 <= d3 && d2 <= d1) {
					nrg += d2;
					path = PATH.UP;
				} else {
					nrg += d3;
					path = PATH.UPRIGHT;
				}
				energyPath[row][col] = new PathObj(nrg, path);
			}
		}

		// We get the min energy
		int min = Integer.MAX_VALUE;
		int row = image.getHeight() - 1;
		int col = 0;
		for (int j = 0; j < image.getWidth(); j++) {
			if (energyPath[row][j].energy < min) {
				min = energyPath[row][j].energy;
				col = j;
			}
		}

		int[][] newImage = new int[image.getHeight()][image.getWidth() - 1];

		PATH iter = energyPath[row][col].path;

		for (int i = 0, j = 0; j < image.getWidth(); i++, j++) {
			if (i == col) {
				j ++;
			}
			newImage[row][i] = image.data[row][j];
		}
		while (iter != null) {
			switch (iter) {
				case UP: {
					--row;
					break;
				}
				case UPLEFT: {
					--row;
					--col;
					break;
				}
				case UPRIGHT: {
					--row;
					++col;
					break;
				}
			}
			System.out.println(row);
			for (int i = 0, j = 0; j < image.getWidth(); i++, j++) {

				if (i == col) {
					j ++;
				}
				newImage[row][i] = image.data[row][j];
			}
			iter = energyPath[row][col].path;
		}


		return new PMGImage(newImage, image.getMaxValue());
	}

	// TODO: Clean this up
	private static int[][] calculateEnergyGrid(PMGImage image) {

		int[][] energy = new int[image.getHeight()][image.getWidth()];
		int v;
		int h = image.getHeight();
		int w = image.getWidth();

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				v = image.data[row][col];
				int d1, d2, d3, d4;

				try {
					d1 = Math.abs(v - image.data[row - 1][col]);
				} catch (ArrayIndexOutOfBoundsException e) {
					d1 = 0;
				}
				try {
					d2 = Math.abs(v - image.data[row + 1][col]);
				} catch (ArrayIndexOutOfBoundsException e) {
					d2 = 0;
				}
				try {
					d3 = Math.abs(v - image.data[row][col + 1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					d3 = 0;
				}
				try {
					d4 = Math.abs(v - image.data[row][col - 1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					d4 = 0;
				}

				energy[row][col] = d1 + d2 + d3 + d4;
			}
		}


//    	energy[0][0] = Math.abs(v - image.data[0][1]) + Math.abs(v - image.data[1][0]);
//    	for (int i = 1; i < w - 1; i++) {
//    		v = image.data[0][i];
//    		energy[0][i] = Math.abs(v - image.data[0][i + 1]) + Math.abs(v - image.data[0][i - 1]) + Math.abs(v - image.data[1][i]);
//		}
//		v = image.data[0][w - 1];
//		energy[0][w - 1] = Math.abs(v - image.data[0][w - 2]) + Math.abs(v - image.data[1][w - 1]);
//		for (int i = 1; i < h - 1; i++) {
//			v = image.data[i][w - 1];
//			energy[i][w - 1] = Math.abs(v - image.data[i][w - 2]) +
//											  Math.abs(v - image.data[i + 1][w - 1]) +
//											  Math.abs(v - image.data[i - 1][w - 1]);
//		}
//		v = image.data[h - 1][w - 1];
//		energy[h - 1][w - 1] = Math.abs(v - image.data[h - 2][w - 1]) +
//															  Math.abs(v - image.data[h - 1][w - 2]);
//		for (int i = 1; i < h - 1; i++) {
//			v = image.data[i][0];
//			energy[i][0] = Math.abs(v - image.data[i][1]) + Math.abs(v - image.data[i + 1][0]) + Math.abs(v - image.data[i - 1][0]);
//		}
//		v = image.data[h - 1][0];
//		energy[h - 1][0] = Math.abs(v - image.data[h - 2][0]) + Math.abs(v - image.data[h - 1][1]);
//		for (int i = 1; i < w - 1; i++) {
//			v = image.data[h - 1][i];
//			energy[h - 1][i] = Math.abs(v - image.data[h - 2][i]) +
//												Math.abs(v - image.data[h - 1][i + 1]) +
//												Math.abs(v - image.data[w - 1][i - 1]);
//		}
//
//		for (int i = 1; i < h - 1; i++) {
//			for (int j = 1; j < w - 1; j++) {
//				v = image.data[i][j];
//				energy[i][j] = Math.abs(v - image.data[i - 1][j]) + Math.abs(v - image.data[i + 1][j]) +
//							   Math.abs(v - image.data[i][j + 1]) + Math.abs(v - image.data[i][j - 1]);
//			}
//		}
		return energy;
	}

//	private static int getPixelEnergy(PMGImage image, int x, int y) {
//    	int v = image.data[x][y];
//		return Math.abs(v - image.data[x-1][y]) + Math.abs(v - image.data[x + 1][y]) +
//				Math.abs(v - image.data[x][y - 1]) + Math.abs(v - image.data[x][y + 1]);
//
//	}

	enum PATH {
		UP,
		UPLEFT,
		UPRIGHT,
	}

}
