package ai.subut.kurjun.quota;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.disk.DiskQuota;
import ai.subut.kurjun.quota.disk.DiskQuotaController;
import ai.subut.kurjun.quota.transfer.TransferQuota;
import ai.subut.kurjun.quota.transfer.TransferQuotaController;
import ai.subut.kurjun.quota.transfer.TransferredDataCounter;


/**
 * Factory class for quota controllers.
 *
 */
public interface QuotaControllerFactory
{

    /**
     * Creates disk quota controller.
     *
     * @param diskQuota disk quota info to apply
     * @param context context to which quota is applied
     * @return
     */
    DiskQuotaController createDiskQuotaController( DiskQuota diskQuota, KurjunContext context );


    /**
     * Creates transfer quota controller.
     *
     * @param quota transfer quota info to apply
     * @param dataCounter data counter that provides transferred data amounts
     * @return
     */
    TransferQuotaController createTransferQuotaController( TransferQuota quota, TransferredDataCounter dataCounter );
}

