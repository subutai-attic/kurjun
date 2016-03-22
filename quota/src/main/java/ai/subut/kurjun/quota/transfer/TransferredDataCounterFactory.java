package ai.subut.kurjun.quota.transfer;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;


/**
 * Factory class for {@link TransferredDataCounter} instances. Instances are created based on {@link KurjunContext}.
 * Subsequent retrievals by the same context return the same instance of data counter.
 *
 */
@Singleton
public class TransferredDataCounterFactory
{

    private ConcurrentMap<String, TransferredDataCounter> items = new ConcurrentHashMap<>();


    /**
     * Gets transferred data counter for the supplied context.
     *
     * @param context context for which to return transferred data counter
     * @return
     */
    public TransferredDataCounter get( KurjunContext context )
    {
        TransferredDataCounter counter = new TransferredDataCounter();
        TransferredDataCounter existing = items.putIfAbsent( context.getName(), counter );
        return existing != null ? existing : counter;
    }
}

