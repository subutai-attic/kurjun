package ai.subut.kurjun.quota;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.disk.DiskQuotaController;
import ai.subut.kurjun.quota.transfer.TransferQuotaController;


/**
 * Factory class for quota controllers.
 *
 */
public interface QuotaControllerFactory
{

    /**
     * Creates disk quota controller.
     *
     * @param context context for which quota controlling is done
     * @return
     */
    DiskQuotaController createDiskQuotaController( KurjunContext context );


    /**
     * Creates transfer quota controller.
     *
     * @param context context for which quota controlling is done
     * @return
     */
    TransferQuotaController createTransferQuotaController( KurjunContext context );
}

