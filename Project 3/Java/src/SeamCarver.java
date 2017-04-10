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
class SeamCarver {

	static PMGImage seamCarve(PMGImage image, int vertical, int horizontal) {

		for (int i = 0; i < vertical; i++) {
			int[][] energy = calculateEnergyGrid(image);
			image = carveVertical(image, energy);
		}

		for (int i = 0; i < horizontal; i++) {
			int[][] energy = calculateEnergyGrid(image);
			image = carveHorizontal(image, energy);
		}

		return image;
	}

	private static PMGImage carveHorizontal(PMGImage image, int[][] energy) {

		int[][] energyPath = new int[image.getHeight()][image.getWidth()];

		for (int i = 0; i < image.getHeight(); i++) {
			energyPath[i][0] = energy[i][0];
		}

		for (int col = 1; col < image.getWidth(); col++) {
			for (int row = 0; row < image.getHeight(); row++) {

				int d1, d2, d3;
				try {
					d1 = energyPath[row - 1][col - 1];
				} catch (Exception e) {
					d1 = Integer.MAX_VALUE;
				}
				try {
					d2 = energyPath[row][col - 1];
				} catch (Exception e) {
					d2 = Integer.MAX_VALUE;
				}
				try {
					d3 = energyPath[row + 1][col - 1];
				} catch (Exception e) {
					d3 = Integer.MAX_VALUE;
				}

				int nrg = energy[row][col];
				nrg += Math.min(Math.min(d1, d2), d3);

				energyPath[row][col] = nrg;
			}
		}

		// We get the min energy
		int min = Integer.MAX_VALUE;
		int row = 0;
		int col = image.getWidth() - 1;
		for (int j = 0; j < image.getHeight(); j++) {
			if (energyPath[j][col] < min) {
				min = energyPath[j][col];
				row = j;
			}
		}

		int[][] newData = new int[image.getHeight() - 1][image.getWidth()];

		while (true) {

			for (int i = 0, j = 0; j < image.getHeight(); i++, j++) {
				if (j == row) {
					j++;
					if (j >= image.getHeight()) {
						break;
					}
				}
				newData[i][col] = image.data[j][col];
			}

			if (col == 0) {
				break;
			}

			int d1, d2, d3;
			int tcol;
			try {
				d1 = energyPath[row - 1][col - 1];
			} catch (Exception e) {
				d1 = Integer.MAX_VALUE;
			}
			try {
				d2 = energyPath[row][col - 1];
			} catch (Exception e) {
				d2 = Integer.MAX_VALUE;
			}
			try {
				d3 = energyPath[row + 1][col - 1];
			} catch (Exception e) {
				d3 = Integer.MAX_VALUE;
			}
			int pathMin = Math.min(Math.min(d1, d2), d3);
			if (pathMin == d1) {
				row = row - 1;
			} else if (pathMin == d3) {
				row = row + 1;
			}
			col--;
		}

		return new PMGImage(newData, image.getMaxValue());

	}

	private static PMGImage carveVertical(PMGImage image, int[][] energy) {

		int[][] energyPath = new int[image.getHeight()][image.getWidth()];

		for (int i = 0; i < image.getWidth(); i++) {
			energyPath[0][i] = energy[0][i];
		}

		for (int row = 1; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				int d1, d2, d3;
				try {
					d1 = energyPath[row - 1][col - 1];
				} catch (Exception e) {
					d1 = Integer.MAX_VALUE;
				}
				try {
					d2 = energyPath[row - 1][col];
				} catch (Exception e) {
					d2 = Integer.MAX_VALUE;
				}
				try {
					d3 = energyPath[row - 1][col + 1];
				} catch (Exception e) {
					d3 = Integer.MAX_VALUE;
				}

				int nrg = energy[row][col];
				nrg += Math.min(Math.min(d1, d2), d3);

				energyPath[row][col] = nrg;
			}
		}

		// We get the min energy
		int min = Integer.MAX_VALUE;
		int row = image.getHeight() - 1;
		int col = 0;
		for (int j = 0; j < image.getWidth(); j++) {
			if (energyPath[row][j] < min) {
				min = energyPath[row][j];
				col = j;
			}
		}

		int[][] newData = new int[image.getHeight()][image.getWidth() - 1];

		while (true) {

			for (int i = 0, j = 0; j < image.getWidth(); i++, j++) {
				if (j == col) {
					j++;
					if (j >= image.getWidth()) {
						break;
					}
				}
				newData[row][i] = image.data[row][j];
			}

			if (row == 0) {
				break;
			}

			int d1, d2, d3;
			try {
				d1 = energyPath[row - 1][col - 1];
			} catch (Exception e) {
				d1 = Integer.MAX_VALUE;
			}
			try {
				d2 = energyPath[row - 1][col];
			} catch (Exception e) {
				d2 = Integer.MAX_VALUE;
			}
			try {
				d3 = energyPath[row - 1][col + 1];
			} catch (Exception e) {
				d3 = Integer.MAX_VALUE;
			}
			int pathMin = Math.min(Math.min(d1, d2), d3);
			if (pathMin == d1) {
				col = col - 1;
			} else if (pathMin == d3) {
				col = col + 1;
			}
			row--;
		}

		return new PMGImage(newData, image.getMaxValue());
	}

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

		return energy;
	}


}
