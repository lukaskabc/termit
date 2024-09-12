package cz.cvut.kbss.termit.util.throttle;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executor;

/**
 * Executes the runnable in a transaction synchronously.
 *
 * @see Transactional
 */
@Component
public class SynchronousTransactionExecutor implements Executor {

    @Transactional
    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
