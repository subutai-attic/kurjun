package ai.subut.kurjun.quota;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.disk.DiskQuota;
import ai.subut.kurjun.quota.disk.DiskQuotaController;


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
}

