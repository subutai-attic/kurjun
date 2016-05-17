package ai.subut.kurjun.identity;


import java.util.Iterator;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.io.output.StringBuilderWriter;

import ai.subut.kurjun.model.identity.UserToken;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DefaultUserTest
{
    public static final String KEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" + "Version: Subutai Social v2.0.6\n"
            + "Comment: https://subutai.io/\n" + "\n" + "xsFNBFcCaGkBEADHnk3PYq4yn5MStbjg71CcBl0N3Um4D/mWQAGeMzoV1nj9\n"
            + "kWyk9r2cKwmxPe1Tnn1I277cWQdnu/ymoTZZ7xTq189RAWKCawmKKSC2Dw6w\n"
            + "8k/jhOls9AtoYMDuIy2FUNbRzwtNPHuAcKc0+KMCbpVCedQp81zxJ1fa/lFV\n"
            + "SWTLwch0KJupINfUxfAbJFrQgqobNXPqcrhevSJXQoOAKdS0JOpLHMTg+qhV\n"
            + "52s3QkeTq7Mhhp3LJ6vZHFvhC8Xg4iBH+E2c3VGsIU3aafLq4qnRfMOsuCvR\n"
            + "BqwsLLRPjZUWkXfzKvhoWjFNvZIHf/qlv6IcbVt0mUg7m77EM7yZrOdzohaN\n"
            + "77J7DcGGP76v/MYB8fChM1aDc9PcpYoUb+cvUZDIK+bew5AXaSjQmPpgUFcd\n"
            + "LYTBvAGGVT9g519/v8iFxuPx4B/yEGGG7qW24XDRhk04wNF7X7l+o5fkgx3x\n"
            + "IOs2b7fwEVXhNo/JCPf7yhKU+x72Gez1CopLylaKYpwo2ZyTjA+FDTR4+JYu\n"
            + "Ln85ju9bmJw/TAWVn12kU0522HSHDXgYUDaJPugfbbNQ4hulv5aoxPL3M8p8\n"
            + "gyc7M0rdF1QEmA/egP0yjm06E6lPGXCUGMWhCLdqnSD4MII2zKgFjuJPYDN6\n"
            + "SGzqgbq8OULZIlEddwjZMJu8ilGCgxIyXHbXDwARAQABzSkiZXJtZWsuYWJ5\n"
            + "c2hldiIgPGVybWVrLmFieXNoZXZAZ21haWwuY29tPsLBdQQQAQgAKQUCVwJo\n"
            + "bAYLCQgHAwIJENSOdOGDM+ujBBUIAgoDFgIBAhkBAhsDAh4BAADXqRAAl9WN\n"
            + "jLy4QLQZCsS2ILprrLwRh0lbs5QiWcODS0ZTitMTHDz3xaOY21PAOofJdNTO\n"
            + "65YG/7MLviOf1RWaal4yHGvgTLjJ50V8GxA97EWKp1LFjz71H4UYLL/qxn8U\n"
            + "RkoPiB/pG96W0hCjgBPGM6EDMfzKKw1wYCfS77QZTOTMGrioRj5BDkxPkCck\n"
            + "mFPWMBPGsDUUN5BunFsKRlgkD+7om5DiZ3I1LMNCVFjZBa/YNxAqvmR9OC8z\n"
            + "2bYVrkaMhxooNM0Uj7AmMn+3EwSwAZylIO3jMxgdGjW+aHAxx+T3caK3Ge9y\n"
            + "PEyNsIXzbngbpsJB0b6VFG99Xl1VGECDnIwpDHQrkNx9+tS1YdgcIWogDB03\n"
            + "9XOJKO4TuH0GUrBHt+QNj0ETXZU98LClfSRBuThA9gtnB2ip3nOLi3hvF9HS\n"
            + "FTS6NJuErq8yoVV97DfoQqhv3SQYM1ObNOGGpsLL4batZhM8UgocB/R+N/R4\n"
            + "4ovJOSUAXQKXx/WelNPmY1ArGGN5K6Z5DZpY9cyRLabBQa5HzWQY5puhKeXx\n"
            + "I5WtnyvxZqbwgCgry3skLnEE/vsZfwrGnO/+24FZdxGKevObEIdhwzSFw8En\n"
            + "JPBon6sMIJeajYwd0dx6o1OX8DUxN1u9WrWA36Xj5MTuTY/zCU2exlm2NkKJ\n"
            + "P9B5K/cGmWYd7/nJGQrOwU0EVwJoaQEQALemsLjebiFUZY01U9geg6UDv25p\n"
            + "HhP4BjkXVfXAq3aemf1Z9zfS6/x8L+yHkvk3Xl0pt+eeRC5cYMGyBGkzLvm0\n"
            + "Y5nxWLdjedmJq8snJ6LzExvaXki2Xzi4EOKbr5opYwkJVzfRlxONTmaDq5s/\n"
            + "lt8oWES9AcOr7iXAYPh2w/zgdpcCgjOzn7jZ5v9fSFd2tfakAd4pkwbtPvt3\n"
            + "3AHz03LK1UC5/j3wxAx8/ZK5euYehBT+68wkJ+6ncz0s2a/8s1MV7wMpzf7f\n"
            + "WAC3aOBz1iQzuLQFb2KOFY9fAdoX8qgYra2oj2Us8y8tsBB7UQ1Y9TSsDy+p\n"
            + "ueX6SuICuEfxt/pLSHjtqOdFZmNhDYw8CbRuBq5viD4z06glb1jrpYinfg5I\n"
            + "gu/37ybcysj8BVx5/9zxTYwKkA0l6lKs5L2IyJ5LFqFzq+bLfiBMgS4080+N\n"
            + "nV/Wo70VaxSdcV1KKwTj6U3xItuFDF1/ehbjUuKeZcVolmRoimWT6Y+VsLzn\n"
            + "jqxEarXZyirMKS8xeBzZJ/sVfowacP2M9mHkpUh/bfuTgkPW1ZP4ivztfaD1\n"
            + "oeGcJNCrnmMxehbx4t6Phr5sob37gUO1kfz+FvTDwG4chdc0wcPj9tO/1qwr\n"
            + "HsThLQq2fC4uDjHlgmzL4ZWK80ndz3R/KoUiA/wk46RQLxJO3r5+fCCrABEB\n"
            + "AAHCwV8EGAEIABMFAlcCaG4JENSOdOGDM+ujAhsMAADRaw//bC2Poyjpc7Bz\n"
            + "TNcGlQ129jIYDMlRul32iNSvKNX31Qqlz3uuaCO2yG1v7n79nEElVkQj9FoF\n"
            + "ITZZZrCJ+HEN+/GTyUlZnP3Skcq+l0EuCCPxR94aU/daWZHyTF2RkTYhtZZO\n"
            + "BVPioiRYaSzAroKRwuzYHtUtFHGZdAZcKctxwAUpPM4JVQKvfBZXondhuWOx\n"
            + "nFoD/uGzoBrA5uX/4lH2MIw8zcFsP6QIcJEYV8Xb+O0BMTyrUhJg2hXTfQx7\n"
            + "BdQzkJVpe+VPHb1JmPtoMPtcds9sg/jltBzFU6LnYBbMyEU+0j8nIfecdwjn\n"
            + "H+44cu0SH2x8Cc7wMcE20U7rocpftd/hNde4BrXB5a/y+GI14uPXRpCQuKYr\n"
            + "mzq+E+gvRGG1cyqkukjp0HQT4wXsu3Q78OIN94NBA5iDBN43GdDZOqInWtMN\n"
            + "wR5cthUrk8h/j3L/S9kRXiAd/DVzDdug5Nopf4tqQgCmPdp+N0QZxUazRRfz\n"
            + "KwGuIpKoUSMLkadr5/jmj1aKSiPk1StEGPJ5J1uotzxr83e8vO6YGJ5LNorp\n"
            + "IcaI/kgT6/Gn10YH/2wfUBjmTe+4t9vs3LeTYvZpqeHoRRK0zWTqk8gFmB6y\n"
            + "MXATeaBNpES7/H2eIctBb4PobpbVV1k4DD1I/sEdytDXLQ/STZmQy+YzX01S\n" + "EYc6/VVNb3s=\n" + "=Kwc8\n"
            + "-----END PGP PUBLIC KEY BLOCK-----";

    private DefaultUser defaultUser;

    @Mock
    UserToken userToken;

    @Mock
    PGPPublicKey pgpPublicKey;

    @Mock
    Iterator iterator;


    @Before
    public void setUp() throws Exception
    {
        defaultUser = new DefaultUser( KEY );

        defaultUser.setUserToken( userToken );
        defaultUser.setType( 1 );
    }


    @Test
    public void testContructor()
    {
        defaultUser = new DefaultUser();

        byte[] fingerprint = { 0, 5 };
        when( pgpPublicKey.getFingerprint() ).thenReturn(fingerprint);
        when( pgpPublicKey.getUserIDs() ).thenReturn( iterator );
        when( iterator.next() ).thenReturn( "test" );

        defaultUser = new DefaultUser( pgpPublicKey );
    }


    @Test
    public void setKeyId() throws Exception
    {
        defaultUser.setKeyId( "keyId" );
    }


    @Test
    public void getKeyId() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getKeyId() );
    }


    @Test
    public void getKeyFingerprint() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getKeyFingerprint() );
    }


    @Test
    public void setKeyFingerprint() throws Exception
    {
        defaultUser.setKeyFingerprint( "fingerprint" );
    }


    @Test
    public void getDate() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getDate() );
    }


    @Test
    public void getEmailAddress() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getEmailAddress() );
    }


    @Test
    public void getSignature() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getSignature() );
    }


    @Test
    public void setSignature() throws Exception
    {
        defaultUser.setSignature( "signature" );
    }


    @Test
    public void getKeyData() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getKeyData() );
    }


    @Test
    public void setKeyData() throws Exception
    {
        defaultUser.setKeyData( "keyData" );
    }


    @Test
    public void getUserToken() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getUserToken() );
    }


    @Test
    public void getType() throws Exception
    {
        // asserts
        assertNotNull( defaultUser.getType() );
    }


    @Test
    public void equals() throws Exception
    {
        defaultUser.equals( new Object() );
        defaultUser.equals( defaultUser );
        defaultUser.hashCode();
    }
}