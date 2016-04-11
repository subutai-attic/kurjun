package ai.subut.kurjun.security.manager;


import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.security.manager.utils.pgp.PGPEncryptionUtil;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;

import static junit.framework.Assert.assertTrue;


public class PGPEncryptionUtilTest
{
    private static final Logger logger = LoggerFactory.getLogger( PGPEncryptionUtilTest.class );

    private static final String MESSAGE = "hello";
    private static final String PUBLIC_KEYRING = "dummy.pkr";
    private static final String SECRET_KEYRING = "dummy.skr";

    private static final String SECRET_PWD = "12345678";
    private static final String PUBLIC_KEY_ID = "e2451337c277dbf1";
    private static final String SECRET_KEY_ID = "d558f9a4a0b450b3";
    private static final String USER_ID = "user@mail.org";
    private static final String PUBLIC_KEY_FINGERPRINT = "8338133EF14DE47D4B1646BEE2451337C277DBF1";
    private static final String SECRET_KEY_FINGERPRINT = "3E5DB4DCF15A31C93CF3C9D8D558F9A4A0B450B3";


    @Test
    public void verifySignature() throws Exception
    {
        String message = getSignedMessage();
        PGPPublicKey pubKey = PGPKeyUtil.readPublicKey( getPublicKey() );
        PGPPublicKeyRing pubKeyRing = PGPKeyUtil.readPublicKeyRing( getPublicKey());

        assertTrue( PGPEncryptionUtil.verifyClearSign( message.getBytes(), pubKey ) );
        assertTrue( PGPEncryptionUtil.verifyClearSign( message.getBytes(), pubKeyRing ) );
    }


    @Ignore
    public void verifySignature2() throws Exception
    {
        String message = getSignedMessage();
        PGPPublicKey pubKey = PGPKeyUtil.readPublicKey( getPublicKey() );

        assertTrue( PGPEncryptionUtil.verifySign( message.getBytes(), pubKey ) );
    }




    private String getSignedMessage()
    {
        String messageSigned = "-----BEGIN PGP SIGNED MESSAGE-----\n" + "Hash: SHA1\n" + "\n" + "\n"
                + "Test Sign Message\n" + "-----BEGIN PGP SIGNATURE-----\n" + "Version: GnuPG v2.0.22 (GNU/Linux)\n"
                + "\n" + "iQIcBAEBAgAGBQJW7o6MAAoJEI3WYZebFeQJqxcQAL9XSZgyHCzRf0bgDnbKa3oh\n"
                + "VpqIn6z+OGIhRQlJAHLUJ/7ANJIARLfmZj8qcLGvgWGRnTpJVPCx6Ae+V3bPcmou\n"
                + "I6/IgMnVazu4y7z1BpV7xmO9CFgr+ppba19x4s/YuhUVV1j1GHJEk/HDjx1G0qAp\n"
                + "cYqAW/aeGECtNjwFewtyzUAB/g9kmqh+bVRHPiXUtAWWqZzOD4V2uta3cKzMjGQg\n"
                + "mJjLeuujkVvvvC097rO9D6A4IbTZ4TmlL/LJ2/iKGap/kamplBED/k4RKnANnavI\n"
                + "tNKbGBGUlgg6XGvLGpvMwa2WoJxHMGGUr8UXyl66UZXJZe7ZhTlWSSYA95YpHaGW\n"
                + "9sqfRnxEOHI/jdNKMUlRWxOB7Obx0AkG+/WmsOcslpqOT+/CtHlufyyeXFDIHZhj\n"
                + "Un86IE7OiGZM1aNMPTeouh1VI4j958DW4KpQuU9xPNb6TaSJKOHAz9zaGpomzr2e\n"
                + "ifJybiFfTFY3FNK7oQglT+rHZIah9Q/foMR3oCQ6dK70HzSzCN6ccbUGvmDIRJg9\n"
                + "KyAMEccv6R0hBfhnKTP6SBr/ifO1q2f0Xw97kxpBXbmylNKfamf7zg2XPASxTfQr\n"
                + "gDh1cAuPDTmSgE0xvUSkn8snNeqYxMIf9aXYFsQxzS6XoumHTrIa+HU0CLOmEYND\n" + "f8GiF3vyt7m1c06PBADF\n"
                + "=u+Hf\n" + "-----END PGP SIGNATURE-----";

        return messageSigned.trim();
    }


    private String getPublicKey()
    {
        String pubKeySTR = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" + "Version: Subutai Social v1.2.3.1\n"
                + "Comment: https://subutai.io/\n" + "\n"
                + "xsFNBFbtkRUBEADKd2nZt6Ii8kHYZACDxVG2SVfsSYsVGirHmP8TvqaG0HJ+\n"
                + "5stPRsb1YEGKEXIrm3xD3IdKLhUy7AQJcrVw2drP9ZApvMn6PgxoII6jY+Sc\n"
                + "C6xzHW+qJ7dgtTIQ8+1NCO3QX6kKbBxOY8hZrpy4KzZ5w0BahS12YVR4cvnL\n"
                + "5qNofu2B1vPLox8pW7XbD3KFxk+9f3lrSLl+BCorGOlskXoWspTXpXXNLSN5\n"
                + "+HLx1dSG5Shb9/LkMx8OrHg0v9jPIqOtt7YN4fcV8iK8vUzQI+kD+a14my88\n"
                + "ly0yJpq8KrMbQc3LZjTtEUmYim32uQ90Hde9XO7KLK8b03LQ0efJ7jJfQdAK\n"
                + "7xX8roRbnhXqHyO4FyEdGt3vo6vyMw4YCGnlRamLCcz4ovAD8n5VEg513wsy\n"
                + "XyECrIJz7xWTqxqotBjlQA5CKpu7fvunK/dD/Uqf/u5G70DIj0ws1nIY9nnT\n"
                + "xjx1DaT3lkyyAmLJxtQr0xI0Dt2mHa3WqY+xtfBSVjBihFh3LolSoAsM0rH7\n"
                + "GWpBVbr/0pZ/Wn44P3A6tdIMWjwnF/MW/CoaX26SKu5v7Kq2drF90Q57eR3l\n"
                + "GBw1hV1j8TZHwFWZDumEJlV7ntGOIZScFAgbn3fk\n" + "TyW3SxMiW7PlkXs8eo7b\n"
                + "QQucTbPdykU+wSoPAnmdRpdxuCF/BA9BiT3uTQARAQABzRxzYW1hbnRoYSA8\n"
                + "c2FtYW50aGFAbWFpbC5jb20+wsF1BBABCAApBQJW7ZEXBgsJCAcDAgkQjdZh\n"
                + "l5sV5AkEFQgCCgMWAgECGQECGwMCHgEAAINSD/47lLBLr4fSixOmuW4pHC5u\n"
                + "zNcdsV2YN570YizpkLX88CJnzhqsFlPzRtrPvCsOSq6syTCWZ4lOIUW/ya9J\n"
                + "V/6OiGo4CZs3Mp4KgvmLR8lFkzkaaHo6p8eAs03T1mqCX+taMN6GH0onRmJs\n"
                + "7DCk+nGvDHr3lcuBNWaoi6T4y9E94SBEFWjI9pqIn4TnS6167mzFR/rnBoDX\n"
                + "1A+6aYqBHAXlhxwySuQvpJq2OdM3TY86xEh54/1DVtumc3dUOnPsHp2IWKey\n"
                + "HgFkBTDq77aqf2eIc9INB5PlCKPTdKG0p6+D8SIcUzzLvGaDZvKUWxszmW5y\n"
                + "qfeJLIa3b8qaNp1pVKlJI75tgW+fIFNjyPBcQA45mhvqfxZnc7PSzMJnecpz\n"
                + "1sf29C7aWxwkzpC07jp24F1SG8egUbBYawD1tD/RFp9R5Sj6Pe1dEwwlL1Wq\n"
                + "0Z0UErRVlctMAW8snPoqRC2A3GXonkpAqiap0grlGR/9PU3uXsKV4l0Q7Qu/\n"
                + "yH967XTyt0HHV8apYGUWqBhdnbXZsDDIJtwvMnRX/20ShFmJx4LmpTJKBFmS\n"
                + "+IYPrSbGzDjdfSrjgfQKz3hVZ11fM/5uXantrh6fqN1km0DBdk9BvHyoMtx/\n"
                + "4719o0S5LI6I9fmHn9fORYM8TXUr6ReRJyiuTd34uLGecXpMUWot7KbuIWfE\n"
                + "Hc7BTQRW7ZEVARAAxozzn1L0vReRLrO8HAtHnp6duKn4FeTFDyoF734XV0ty\n"
                + "LVe5kWOCaOYni0kpIY8IbSB4zUbExz1iBIPpcRQ3z//hu9CpzbOG+u0YSaKn\n"
                + "acF2xm2mVrWY4L1mUObzDBEiluQIriIMneK82U1kEAEMFJ6fUYTJ1g5im+6h\n"
                + "81ffq/V2F/NjA3nZIrmeLBM6nwL0WfQG8mcY5ZLf/jAMjqyCW1Y3M/cgo06S\n"
                + "t6LybBpkTYeAB9D2VoYehM8swIumk0bpZTdRsegLgFo9Ndp6dUfSwuyrAKIb\n"
                + "GuJvxIzl8jT1CG+AR4DFdjuyv1zugFItD52NslgGsEYkhP0GuCG73XTNWN9u\n"
                + "tDtr4cgpT+Lnhtbh4pjTRo/9IeIQBSI4CIHPGrnyR1jlVFsq5ChUO8n6mQLh\n"
                + "A+3EF1unrrFXh86EgxldWOMcfkIYhp1m2er9ynbSEtvPm2NcP7xGq4yaBECu\n"
                + "QomTUTP+h5iaGvlH203Sr+vsCerKq5eFgIy4UDLgaDhdMUg+LGPRJwJ1LlwS\n"
                + "V9sJovu8sVXWbHagV0WehdHMwjCI6lnt3895iR5NTt6WsVPz6bdaNe0Fzpbm\n"
                + "EBs+F8B3WI3Kjn3QQTzVgtLFbnmRKh66oW+tXqz6MNlKXMQm+Hycfcptjo/O\n"
                + "W9xUN+ZBXd381YNXpJzYGl42AoI1pv9p0MEkW2kAEQEAAcLBXwQYAQgAEwUC\n"
                + "Vu2RGQkQjdZhl5sV5AkCGwwAAKyKD/4m8b2OjKx2wsffNV19POJKZ1PwRNKO\n"
                + "dMQcmfWLV6b13bANQ/ABSuY6ccYGtlqfwIMV9aPxPEXBy/6S5aZPPcr0/beu\n"
                + "QA9ckDK8j7pHgSWVSMiq8E1Nn2yqV76fxXa1psUHywpq3U48QYvZyIpzbg/7\n"
                + "XqTDRks6wuG6C04mdJBXUfk/D9y4yeTMjyuObw9+T7miy9PJVipYQqE3VX+A\n"
                + "m0ucuUFcZSoOphxKAg6Wj+uIiP4S4J0Lk4Zkw9tDwyerzlvtFA7gvD206Uqh\n"
                + "Z7WScJbLxBaUq6nwybKsSy91HpddEas5m9H82BNFh2Ol8j0flR9yYM3iLy59\n"
                + "BmiFH1PejoAd/mNXxcYIUWZFCDedI63y6kdf/JpTgoKykA85YGYiDxBKZHu4\n"
                + "tlWEzxkvu66XqGAOlH/OCKoGrms92o4dMrEdpp7jHydA0vkMTJlMNa94JWzc\n"
                + "DqoZy1DO6JVeZKz3wJplHRaJFYu7+AbStWOlO6l7NVqUvEDowD+OXozgYbvZ\n"
                + "sYz3nciv8ev446lzENzbERgeoT4l2vWKY2mp10OAmyHfLNnoKPgJTbO8HPVL\n"
                + "ukAQyF181NH3WpR2q6Tj8aBFzj0NVHLL3daqxC2otfQYVX/d13hpgVtCOZE0\n"
                + "SvFRO5cUEhCey8fyxu73iV5G4qVrIJdLG3n0btTv2cH3tClNjxiuyQ==\n" + "=dls4\n"
                + "-----END PGP PUBLIC KEY BLOCK-----";

      return pubKeySTR;
    }
}