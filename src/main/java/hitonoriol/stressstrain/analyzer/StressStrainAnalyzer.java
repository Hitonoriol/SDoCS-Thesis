package hitonoriol.stressstrain.analyzer;

import java.util.function.BiConsumer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import hitonoriol.stressstrain.util.Array2d;

/*
 * Wrapper for `StressStrainLibrary`.
 * 		the interface itself can't be implemented directly because it's bound to native library calls.
 */
public class StressStrainAnalyzer {
	/* Will search for:
	 * 		`stress-strain.dll` on Windows
	 * 		`libstress-strain.so` on Linux
	 */
	private final StressStrainLibrary library = Native.load("stress-strain", StressStrainLibrary.class);

	public Array2d getStressStrainTable() {
		return getTable(library.getOutTablePtr(), library.outTableSize(), library.outTableWidth());
	}

	public Array2d getCouplingErrorTable() {
		return getTable(library.getBiasTablePtr(), library.biasTableSize(), library.biasTableWidth());
	}

	private Array2d getTable(Pointer arrPtr, int size, int width) {
		float[] tableArr = arrPtr.getFloatArray(0, size);
		return new Array2d(tableArr, width);
	}

	public void forEachZOPoint(BiConsumer<Float, Float> pointConsumer) {
		forEachPlotPoint(library.getZOXPtr(), library.getZOYPtr(), pointConsumer);
	}

	public void forEachDZOPoint(BiConsumer<Float, Float> pointConsumer) {
		forEachPlotPoint(library.getDZOXPtr(), library.getDZOYPtr(), pointConsumer);
	}

	private void forEachPlotPoint(Pointer xPtr, Pointer yPtr, BiConsumer<Float, Float> pointConsumer) {
		final int ptCount = library.getPlotPoints();
		float x[] = xPtr.getFloatArray(0, ptCount);
		float y[] = yPtr.getFloatArray(0, ptCount);
		for (int i = 0; i < ptCount; ++i)
			pointConsumer.accept(x[i], y[i]);
	}

	public void calcStressStrainState(int n, float q1, float q2, float kpi, boolean usl1, boolean usl2) {
		library.calcStressStrainState(n, q1, q2, kpi, toInt(usl1), toInt(usl2));
	}

	public void dispose() {
		library.dispose();
	}

	public StressStrainLibrary getLibrary() {
		return library;
	}

	private static int toInt(boolean b) {
		return b ? 1 : 0;
	}
}
