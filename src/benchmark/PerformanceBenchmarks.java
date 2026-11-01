package benchmark;

import model.Cart;
import model.UserManager;

/**
 * Runs the legacy timing exercises separately from the desktop application.
 *
 * <p>These timings are educational smoke checks, not statistically rigorous
 * microbenchmarks. In particular, the user benchmark reads and writes the
 * configured users file and should only be run against disposable data.</p>
 */
public final class PerformanceBenchmarks {
    private PerformanceBenchmarks() {
    }

    public static void main(String[] args) {
        Cart.performanceTest(100);
        Cart.performanceTest(150);

        if (args.length > 0 && "--include-user-persistence".equals(args[0])) {
            UserManager.performanceTest(100);
            UserManager.performanceTest(150);
        } else {
            System.out.println(
                "Skipped user persistence timings. Pass --include-user-persistence "
                    + "only when data/users.txt is disposable."
            );
        }
    }
}
