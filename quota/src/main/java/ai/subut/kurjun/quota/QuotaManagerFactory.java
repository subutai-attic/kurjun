package ai.subut.kurjun.quota;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.disk.DiskQuotaManager;
import ai.subut.kurjun.quota.transfer.TransferQuotaManager;


/**
 * Factory class for quota managers.
 *
 */
public interface QuotaManagerFactory
{

    /**
     * Creates disk quota manager.
     *
     * @param context context for which quota controlling is done
     * @return
     */
    DiskQuotaManager createDiskQuotaManager( KurjunContext context );


    /**
     * Creates transfer quota manager.
     *
     * @param context context for which quota controlling is done
     * @return
     */
    TransferQuotaManager createTransferQuotaManager( KurjunContext context );
}

