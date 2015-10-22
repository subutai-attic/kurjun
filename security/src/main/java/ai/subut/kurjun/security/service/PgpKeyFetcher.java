package ai.subut.kurjun.security.service;


import org.bouncycastle.openpgp.PGPPublicKey;


/**
 * Interface to fetch PGP keys from HKP compatible keyservers. HKP stands for HTTP Key server Protocol which defines
 * conventional ways to get, index, and upload PGP keys in a key store.
 * <p>
 * Find more info at http://tools.ietf.org/html/draft-shaw-openpgp-hkp-00
 *
 */
public interface PgpKeyFetcher
{

    /**
     * Gets key from key server by supplied fingerprint.
     *
     * @param fingerprint key fingerprint to look for
     * @return PGP key if found and successfully parsed; {@code null} otherwise
     */
    PGPPublicKey get( String fingerprint );

}

