import org.openwearos.server.modes.ModeEngine;
import java.util.ArrayList;
import java.util.List;

public class ModeEngineTest {
	static void eq(Object want, Object got, String msg) {
		if (!want.equals(got))
			throw new AssertionError(msg + ": want " + want + " got " + got);
	}

	public static void main(String[] a) {
		ModeEngine e = new ModeEngine();
		List<Object> applied = new ArrayList<>();
		e.addFeature("ambient", true,
				ModeEngine.forcedWhenAny(false, "battery_saver", "bedtime_mode", "talkback"), applied::add);
		eq(List.of(true), applied, "initial apply");

		e.setMode("bedtime_mode", true);
		eq(List.of(true, false), applied, "bedtime forces off");
		e.setMode("battery_saver", true);
		eq(List.of(true, false), applied, "second mode: no re-apply");
		e.setMode("bedtime_mode", false);
		eq(List.of(true, false), applied, "still forced by battery saver");
		e.setMode("battery_saver", false);
		eq(List.of(true, false, true), applied, "own value restored");

		e.setMode("talkback", true);
		e.setOwn("ambient", false);
		e.setMode("talkback", false);
		eq(false, e.effective("ambient"), "user's off survives the mode ending");
		eq(List.of(true, false, true, false), applied, "no spurious apply");

		e.setMode("unrelated", true);
		eq(4, applied.size(), "unrelated mode ignored");
		try {
			e.effective("nope");
			throw new AssertionError("no exception");
		} catch (IllegalArgumentException ok) {
		}
		System.out.println("ModeEngineTest: OK");
	}
}
