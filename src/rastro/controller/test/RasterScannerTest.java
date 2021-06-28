package rastro.controller.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.junit.*;

import rastro.controller.*;

public class RasterScannerTest {
	private static final boolean shape3dot[][] = { { false, false, false }, { false, true, false },
			{ false, false, false } };

	private static final boolean shape3diamond[][] = { { false, true, false }, { true, true, true },
			{ false, true, false } };

	private static final boolean shape5dot[][] = { { false, false, false, false, false },
			{ false, false, false, false, false }, { false, false, true, false, false },
			{ false, false, false, false, false }, { false, false, false, false, false } };

	@Test
	public void scanAtSingleStepShouldReturnTheSameNumberOfLines() {
		RasterScanner rs = new RasterScanner(10, 10, new int[] { 10, 10 }, shape3dot, 1);
		rs.loadImage(createImageController());
		Iterator<boolean[]> it = rs.iterator();
		ArrayList<boolean[]> result = new ArrayList<boolean[]>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		assertEquals(10, result.size());
	}

	@Test
	public void scanWithDiamondShouldReturnProperImage() {
		final int hSize = 8;
		final int vSize = 5;
		final boolean[][] testImage = { { false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, true },
				{ false, false, false, true, false, false, false, false },
				{ false, false, false, false, false, false, false, false },
				{ true, false, false, false, false, false, false, false } };

		final boolean[][] expectedImage = { { true, true, true, true, true, true, true, false },
				{ true, true, true, false, true, true, false, false },
				{ true, true, false, false, false, true, true, false },
				{ false, true, true, false, true, true, true, true },
				{ false, false, true, true, true, true, true, true } };

		RasterScanner rs = new RasterScanner(hSize, vSize, new int[] { hSize, vSize }, shape3diamond, 1);
		rs.loadImage(createImageController(testImage));
		Iterator<boolean[]> it = rs.iterator();
		ArrayList<boolean[]> result = new ArrayList<boolean[]>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.get(i).length; j++) {
				assertEquals(expectedImage[i][j], result.get(i)[j]);
			}
		}
	}

	@Test
	public void scanAtMultipleStepShouldReturnTheFractionNumberOfLines() {
		for (int stepSize = 1; stepSize < 9; stepSize++) {
			RasterScanner rs = new RasterScanner(16, 16, new int[] { 16, 16 }, shape5dot, stepSize);
			rs.loadImage(createImageController());
			Iterator<boolean[]> it = rs.iterator();
			ArrayList<boolean[]> result = new ArrayList<boolean[]>();
			while (it.hasNext()) {
				result.add(it.next());
			}
			assertEquals((int) Math.ceil(16.0f / stepSize), result.size());
		}
	}

	@Test
	public void scanAtMultipleStepShouldReturnLineOfProperLength() {
		RasterScanner rs = new RasterScanner(16, 16, new int[] { 16, 16 }, shape5dot, 4);
		rs.loadImage(createImageController());
		Iterator<boolean[]> it = rs.iterator();
		ArrayList<boolean[]> result = new ArrayList<boolean[]>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		for (boolean[] b : result) {
			assertEquals(16, b.length);
		}
	}

	@Test
	public void scanAtShouldReturnCorrectLinesWihtDotPattern() {
		final int hSize = 64;
		final int vSize = 32;
		Random rng = new Random();
		for (int stepSize = 1; stepSize < 10; stepSize++) {
			boolean[][] img = new boolean[vSize][hSize];
			for (boolean[] row : img) {
				for (int i = 0; i < row.length; i++) {
					row[i] = rng.nextBoolean();
				}
			}
			RasterScanner rs = new RasterScanner(hSize, vSize, new int[] { hSize, vSize }, shape5dot, stepSize);
			rs.loadImage(createImageController(img));
			Iterator<boolean[]> it = rs.iterator();
			ArrayList<boolean[]> result = new ArrayList<boolean[]>();
			while (it.hasNext()) {
				result.add(it.next());
			}
			for (int i = 0; i < result.size(); i++) {
				for (int j = 0; j < result.get(i).length; j++) {
					assertEquals(!img[i * stepSize][j], result.get(i)[j]);
				}
			}
		}
	}

	private ImageController createImageController() {
		return new ImageController() {
			@Override
			public boolean isBlack(float x, float y) {
				return true;
			}
		};
	}

	private ImageController createImageController(final boolean[][] image) {
		final int hSize = image[0].length;
		final int vSize = image.length;
		return new ImageController() {
			@Override
			public boolean isBlack(float x, float y) {
				final int iX = Math.round(x * hSize);
				final int iY = Math.round(y * vSize);
				if (iX >= 0 && iX < hSize && iY >= 0 && iY < vSize) {
					return image[iY][iX];
				} else {
					return false;
				}
			}
		};
	}
}
