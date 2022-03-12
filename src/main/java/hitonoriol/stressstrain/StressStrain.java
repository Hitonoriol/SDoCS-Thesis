package hitonoriol.stressstrain;

import static hitonoriol.stressstrain.Util.*;
import com.sun.jna.Library;
import com.sun.jna.Native;

/*
 * Binds native Fortran library (compiled from `core-lib/stress-strain.for`) to a Java interface
 */
public interface StressStrain extends Library {
	/* Will search for:
	 * 		`stress-strain.dll` on Windows
	 * 		`libstress-strain.so` on Linux
	 */
	static final StressStrain instance = Native.load("stress-strain", StressStrain.class);

	/* Native binding to Fortran library subroutine `calc_stress_strain_state`.
	 * Parameters:
	 * 		`n`    - Number of sections
	 * 		`q1`   - External normal load intensity (even shell)
	 * 		`q2`   - External normal load intensity (odd shell)
	 * 		`kpi`  - `PI` angle divisor
	 * 		`usl1` - Left symmetry 
	 * 		`usl2` - Right symmetry
	 */
	void calcStressStrainState(int n, float q1, float q2, float kpi, int usl1, int usl2);
	
	/* Static wrapper for `calcStressStrainState` */
	static void calcStressStrainState(int n, float q1, float q2, float kpi, boolean usl1, boolean usl2) {
		instance.calcStressStrainState(n, q1, q2, kpi, toInt(usl1), toInt(usl2));
	}
}
