package ai.subut.kurjun.security.manager.utils.pgp;


import java.math.BigInteger;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class PGPKeyUtilTest
{
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final String PUBLIC_KEY_FINGERPRINT = "8338133EF14DE47D4B1646BEE2451337C277DBF1";
    private static final String PUBLIC_KEY_ID = "e2451337c277dbf1";

    private static final String PUBLIC_KEY =
            "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" + "Version: Subutai Social v2.0.7\n"
                    + "Comment: https://subutai.io/\n" + "\n"
                    + "xsFNBFcCaGkBEADHnk3PYq4yn5MStbjg71CcBl0N3Um4D/mWQAGeMzoV1nj9\n"
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

    private static final String PRIVATE_KEY =
            "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" + "Version: Subutai Social v2.0.7\n"
                    + "Comment: https://subutai.io/\n" + "\n"
                    + "xcaGBFcCaGkBEADHnk3PYq4yn5MStbjg71CcBl0N3Um4D/mWQAGeMzoV1nj9\n"
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
                    + "SGzqgbq8OULZIlEddwjZMJu8ilGCgxIyXHbXDwARAQAB/gkDCJysOlZi8AaB\n"
                    + "YHjWpwN2UVii98OJd4Jri3kvf2FhBn43UpZVN84lmEdFgNh6CuLNiA8HRNyn\n"
                    + "Stasbl3nSX9h7zq4qaF/bI5Ov1GquUMrELWYKqevwFalmpdbqdflcHyvh9OC\n"
                    + "fUr8OV+QPs9L2kFV/KkTOd6lWgO2Z+dOjAtFCzNnkOzLeSTK8PyZApmLB3oG\n"
                    + "omZ62NGfVSIJWC5WuZZN5meMq4OhAJ19w/1iqeA/hltEyukV7M5OuiHUpqtU\n"
                    + "xwux4868IN5UFBhOZ4VcHEGWwbW/jUYtacCC3222wMNLy+Kfwa+JX73L7GZU\n"
                    + "XINEBsfazl4cBCw9pS2zCKdLGZGZWwx04Va/7LdfGhloVF+n9nM4kjNoB92V\n"
                    + "yTnzTlQsKqIDXeYe0Q2Ilz2+xQCN3w5KA8G/6VQk1JCqVvLiNQFaINkXgRVf\n"
                    + "lu0QeeDj8DuD52bbaQm7bQfqOLZwtkA4CH1w76C9pD+DL3uVcAa5AK+F1GxM\n"
                    + "bCcW2UkykLoat8TSSNFmMMOdQAlJtPIJBpsK3ck5mtExATbBXvqClFia1hI4\n"
                    + "BnvdGd/tk3inU2CvmlNqBmWGhV9XtuBS1G3b6L5bCg+iMyBnsbFHI69CsEv0\n"
                    + "4Ho5Dkc85AmGm997NboatylhtyX+7WZKmomDqU0DNSGJlDvj0NezycSWhxSp\n"
                    + "qiVrpGWHZLAzcgeAkVXkKGxazNkpSjejdf4QqPPfEChoGK2bgZD5ow6JDXUH\n"
                    + "pON2W7rJaCKnAK32SO9vEKzQtIKzwY4PrUrSEjsdcgGN3Nlf91gbMNJwO1oR\n"
                    + "pcA/IU/qiB1ePvtOLXB/Z/RI4Gs79kuyqBhlkeDSLEfTmv+N3ZtZYPUz/njn\n"
                    + "celSkI/CXiZDiiUtg6UsFETBVMa44X8kCSVcJBciQUttSxE4/a1aTE/Okhfi\n"
                    + "0wRjXGFEjStDRFrpdb+rQHdtWHd0vISvCnujzjPGXpYp6iGsD9a7xQQ1aQ82\n"
                    + "C1nhmoJ+KMdA2E86ySnBNLTFfqiu70bMT9DftP4yC+vXxXp5KvJdwHKkcnDS\n"
                    + "CTvSExMW2z/ATLEZrV/dg0fPSlPhsUlzpjSPWm1yIdbhTAiL3NYlvwU1Nwo2\n"
                    + "QqKcz1Y360qW8wj4DT1oSW7OIAh8GGEeRaJ2UhyOp2G0BVIh2NPVuealL+kz\n"
                    + "itZCYymmXEZh4m4O5Ja3h+geJSPEWFBiFRpibVPVGcvG1erOGUvtOyuMAjfI\n"
                    + "8dvtYoYU4XuqtAPswuRtM0v0CsFbLFGyfM8cRL6Zl11vJIXNf0CfAM89ZdR1\n"
                    + "c3wT8/dRHXEwnayP7FkwYuldddMnGHEDSXOhTG3vAYOu5WpM+S8AVw8WDuna\n"
                    + "PJijBHX+XcMEUb2ghf/oKyU7mNm/yiyOXS1uVN+ywhVLC9qz812Z7kO2LvPT\n"
                    + "kfpc8G4FjUGO7jzxGfMj1zfhagX93subMSOeEfVH5E+Om5SSKFnmEMFpxlv9\n"
                    + "HJLvCiXxoaJp8oN5WrjiR6lwBhxxw0inPUeva1gVANlmd0/SY3ajEdTmXVif\n"
                    + "VEO19tEpdZOrI82qIc1+HJh+L4wEAInyT3NnEaoZzZW0UCJio4P3yZaPg858\n"
                    + "koxEWLJqrjAzxmBrDXzNcXoealbylCxabzZTYQpvm6ZhH1MYvNUiOO5bKliG\n"
                    + "+QvLLbkcoUmuOUgv/5LkxD4o0rDFIoReq/iX84Lgya0oxpS+KnujmF6yj2dM\n"
                    + "ga4ob2kjyKgf9w2ht7XWQKN9UZD9tsjDRvfMmFAe2mv7SvXSsIKmk2ELJ8I9\n"
                    + "Ee9LoWk4CfBMIHv49cARxqqIiObNKSJlcm1lay5hYnlzaGV2IiA8ZXJtZWsu\n"
                    + "YWJ5c2hldkBnbWFpbC5jb20+wsF1BBABCAApBQJXAmhsBgsJCAcDAgkQ1I50\n"
                    + "4YMz66MEFQgCCgMWAgECGQECGwMCHgEAANepEACX1Y2MvLhAtBkKxLYgumus\n"
                    + "vBGHSVuzlCJZw4NLRlOK0xMcPPfFo5jbU8A6h8l01M7rlgb/swu+I5/VFZpq\n"
                    + "XjIca+BMuMnnRXwbED3sRYqnUsWPPvUfhRgsv+rGfxRGSg+IH+kb3pbSEKOA\n"
                    + "E8YzoQMx/MorDXBgJ9LvtBlM5MwauKhGPkEOTE+QJySYU9YwE8awNRQ3kG6c\n"
                    + "WwpGWCQP7uibkOJncjUsw0JUWNkFr9g3ECq+ZH04LzPZthWuRoyHGig0zRSP\n"
                    + "sCYyf7cTBLABnKUg7eMzGB0aNb5ocDHH5PdxorcZ73I8TI2whfNueBumwkHR\n"
                    + "vpUUb31eXVUYQIOcjCkMdCuQ3H361LVh2BwhaiAMHTf1c4ko7hO4fQZSsEe3\n"
                    + "5A2PQRNdlT3wsKV9JEG5OED2C2cHaKnec4uLeG8X0dIVNLo0m4SurzKhVX3s\n"
                    + "N+hCqG/dJBgzU5s04Yamwsvhtq1mEzxSChwH9H439Hjii8k5JQBdApfH9Z6U\n"
                    + "0+ZjUCsYY3krpnkNmlj1zJEtpsFBrkfNZBjmm6Ep5fEjla2fK/FmpvCAKCvL\n"
                    + "eyQucQT++xl/Csac7/7bgVl3EYp685sQh2HDNIXDwSck8Gifqwwgl5qNjB3R\n"
                    + "3HqjU5fwNTE3W71atYDfpePkxO5Nj/MJTZ7GWbY2Qok/0Hkr9waZZh3v+ckZ\n"
                    + "CsfGhgRXAmhpARAAt6awuN5uIVRljTVT2B6DpQO/bmkeE/gGORdV9cCrdp6Z\n"
                    + "/Vn3N9Lr/Hwv7IeS+TdeXSm3555ELlxgwbIEaTMu+bRjmfFYt2N52Ymryycn\n"
                    + "ovMTG9peSLZfOLgQ4puvmiljCQlXN9GXE41OZoOrmz+W3yhYRL0Bw6vuJcBg\n"
                    + "+HbD/OB2lwKCM7OfuNnm/19IV3a19qQB3imTBu0++3fcAfPTcsrVQLn+PfDE\n"
                    + "DHz9krl65h6EFP7rzCQn7qdzPSzZr/yzUxXvAynN/t9YALdo4HPWJDO4tAVv\n"
                    + "Yo4Vj18B2hfyqBitraiPZSzzLy2wEHtRDVj1NKwPL6m55fpK4gK4R/G3+ktI\n"
                    + "eO2o50VmY2ENjDwJtG4Grm+IPjPTqCVvWOuliKd+DkiC7/fvJtzKyPwFXHn/\n"
                    + "3PFNjAqQDSXqUqzkvYjInksWoXOr5st+IEyBLjTzT42dX9ajvRVrFJ1xXUor\n"
                    + "BOPpTfEi24UMXX96FuNS4p5lxWiWZGiKZZPpj5WwvOeOrERqtdnKKswpLzF4\n"
                    + "HNkn+xV+jBpw/Yz2YeSlSH9t+5OCQ9bVk/iK/O19oPWh4Zwk0KueYzF6FvHi\n"
                    + "3o+GvmyhvfuBQ7WR/P4W9MPAbhyF1zTBw+P207/WrCsexOEtCrZ8Li4OMeWC\n"
                    + "bMvhlYrzSd3PdH8qhSID/CTjpFAvEk7evn58IKsAEQEAAf4JAwg6YloN8VMK\n"
                    + "r2B/agaFTEdiYXUijZGjToddzYsXILUwDSqEjTUiwOwpbKBJPJjPY8KlvcA2\n"
                    + "gQIo8exffd0ioWICCxobvh6hI6xxIfLL+pMhF/MNnKAB8m8g37FfE52dnPyt\n"
                    + "cYWCmarHsVAwu0j0UEz3fRqsZsp2QcPugYNYEQIzcjwqWCR7zZH/0C2/JzER\n"
                    + "9pI9CeqMJqB6Bwlhz/FsbcKZ7mJvxrteJdDePXHCWh8R3nW+LOx570KJpRtb\n"
                    + "Vym4d6hmZGHP3/cAi0sBx+w4hVOD5Hii9IzgIp0mFpiw9WY62Vk0mqBaOhmr\n"
                    + "RYZg2JSsJRauiq4jGoaDil9MFWpIIWBU15iORvAZhc8FLZdQqj3Q9sSkMOWz\n"
                    + "+EGkeUuJkUMP1pYD+8BgAR40v0xW0D9z1qrrm9P+bE7Mng09eT/4fJx47tje\n"
                    + "ZygxyBbWE6bvSP7YTkVAAmYrEx2GASzcWkQ7eqKImRP5qFitXxAyrdKDp/4Q\n"
                    + "vXD2e0zuFXX1ltMA+G9ynsAFJm0VRhhREtmXV15vA4lQvtK8jmHdHC4O2F2c\n"
                    + "b1scDp2EKMEqOfWoeW4CrVrCr3k9EwevxP1j8DEwdMwU/WSMqBzXa53pTpq5\n"
                    + "iILjHmOH841G9CjmWSkoPLflHar/0oWiT2sIYrzMNcbavAZAZjfDdhJXO3u4\n"
                    + "79GuzCoB758Fx6TqclVg5PNmIc6LX3e1aPd7tMzuh3tC6TSYqYCk7Lgd/FY9\n"
                    + "v1iPV79drXl6G5icpm9bdEpIa4ojnEvAfTxcmZXEFNEWFRWI41Udd84cmw3Q\n"
                    + "JxegodfHBEGo+7w3eNQD6PO9VXHLR9YR5wWAamhhpCm8D5xS18EXmxrF4ApH\n"
                    + "M+guZSfr2maL3Xrl8p12TT7B8dW2cOUrk6/YgYYM54UqYjlr7GgpZ/df7/Y3\n"
                    + "mWScfMHqBlKavhQq0G/vTlATr6joWBtfgxYm0NbVCUUSWt/acNqpwFLd3Ks1\n"
                    + "pxzg+bkHrr/a7xUC+hdDEGS3Z3YlzCbemTNV6PsT49wxMC3dd10TyJ1xUH+H\n"
                    + "FMTdIRlaqQWRJbncT48jD82qxOHUSpc229gVbAR57vJZzo3TrlocmGaXMlzf\n"
                    + "3eroABaVPKvtqVqH/P1tiFyWTLBY8ziAqKLe7YpHULnQW1G2gMGDlYseSyad\n"
                    + "9U9F8hRDH1K7/o5EZOHuepKCtSYnDh8iSKVQjfJeVjzZKGLAi7HHI15tHhxH\n"
                    + "+56WA5qNaf4Kadch42OJDlCEbqE3UUL0IHw5xf7od1HMagKNK5Zkl2IVdSkk\n"
                    + "obcKx97/zT0gnHtxBFttEd+jEKdvT/rQSQzeu9zomjCHX0yO8NufmLFU2v0c\n"
                    + "eVlBo6lRhmsyHT1rsaW0z/jGOb47z75p2y+TkglAYbDraqkZBjj8lrfMh3sL\n"
                    + "+SoCyYlOG7bD9Rhi9gvLe3kHeH5PxAu/ej90xVqpGvKZ5dQ+Uw2uST5xjYBW\n"
                    + "GMX2MvFQzYEmJQiHtTp/ZVMHOWPbNortkcVUl1481ePZJi5zK6XcJf21i8hE\n"
                    + "ESNQIgRs9gWUg9H4MQlNyFvIsXKmLrkj8x8VB5JdHozEvwCvXzdau/suynLV\n"
                    + "gtUBLorDIlR5rmBh7h3HhxysH8g80LxUK980XvL3+U5GrZaRShZ8oa80vkdp\n"
                    + "rGfdnEJPPG7PrGnlva1Om81E5IwLiVWpU/IuOg76Equm08ZVJ5qlyVCrW7of\n"
                    + "CxLpSqrBoiQJdj4WIMQrG1tyBuL/+G5r13PIyGeVPl04nFtJpFxaCfNdhapw\n"
                    + "KZ/iMPtLrZsUQQ9b3p7YE5lLG2rowsFfBBgBCAATBQJXAmhuCRDUjnThgzPr\n"
                    + "owIbDAAA0WsP/2wtj6Mo6XOwc0zXBpUNdvYyGAzJUbpd9ojUryjV99UKpc97\n"
                    + "rmgjtshtb+5+/ZxBJVZEI/RaBSE2WWawifhxDfvxk8lJWZz90pHKvpdBLggj\n"
                    + "8UfeGlP3WlmR8kxdkZE2IbWWTgVT4qIkWGkswK6CkcLs2B7VLRRxmXQGXCnL\n"
                    + "ccAFKTzOCVUCr3wWV6J3YbljsZxaA/7hs6AawObl/+JR9jCMPM3BbD+kCHCR\n"
                    + "GFfF2/jtATE8q1ISYNoV030MewXUM5CVaXvlTx29SZj7aDD7XHbPbIP45bQc\n"
                    + "xVOi52AWzMhFPtI/JyH3nHcI5x/uOHLtEh9sfAnO8DHBNtFO66HKX7Xf4TXX\n"
                    + "uAa1weWv8vhiNeLj10aQkLimK5s6vhPoL0RhtXMqpLpI6dB0E+MF7Lt0O/Di\n"
                    + "DfeDQQOYgwTeNxnQ2TqiJ1rTDcEeXLYVK5PIf49y/0vZEV4gHfw1cw3boOTa\n"
                    + "KX+LakIApj3afjdEGcVGs0UX8ysBriKSqFEjC5Gna+f45o9Wikoj5NUrRBjy\n"
                    + "eSdbqLc8a/N3vLzumBieSzaK6SHGiP5IE+vxp9dGB/9sH1AY5k3vuLfb7Ny3\n"
                    + "k2L2aanh6EUStM1k6pPIBZgesjFwE3mgTaREu/x9niHLQW+D6G6W1VdZOAw9\n"
                    + "SP7BHcrQ1y0P0k2ZkMvmM19NUhGHOv1VTW97\n" + "=5lPn\n" + "-----END PGP PRIVATE KEY BLOCK-----";

    @Mock
    PGPPublicKey publicKey;

    @Mock
    PGPPublicKeyRing pgpPublicKeyRing;

    @Mock
    PGPSecretKeyRing pgpSecretKeyRing;

    @Test
    public void encodeNumericKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.encodeNumericKeyId( 5 ) );
    }


    @Test
    public void encodeNumericKeyIdShort() throws Exception
    {
        assertNotNull( PGPKeyUtil.encodeNumericKeyIdShort( 5 ) );
    }


    @Test
    public void getKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.getKeyId( PUBLIC_KEY_FINGERPRINT ) );
    }


    @Test
    public void getKeyId1() throws Exception
    {
        assertNotNull( PGPKeyUtil.getKeyId( PUBLIC_KEY_FINGERPRINT.getBytes() ) );
    }


    @Test
    public void getFingerprint() throws Exception
    {
        assertNotNull( PGPKeyUtil.getFingerprint( PUBLIC_KEY_FINGERPRINT.getBytes() ) );
    }


    @Test
    public void getShortKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.getShortKeyId( PUBLIC_KEY_FINGERPRINT ) );
    }


    @Test
    public void getShortKeyId1() throws Exception
    {
        assertNotNull( PGPKeyUtil.getShortKeyId( PUBLIC_KEY_FINGERPRINT.getBytes() ) );
    }


    @Test
    public void isFingerprint() throws Exception
    {
        assertNotNull( PGPKeyUtil.isFingerprint( PUBLIC_KEY_ID ) );
    }


    @Test
    public void isLongKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.isLongKeyId( PUBLIC_KEY_ID ) );
    }


    @Test
    public void isShortKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.isShortKeyId( PUBLIC_KEY_ID ) );
    }


    @Test
    public void isValidKeyId() throws Exception
    {
        assertNotNull( PGPKeyUtil.isValidKeyId( PUBLIC_KEY_ID ) );
        assertFalse( PGPKeyUtil.isValidKeyId( null ) );
    }


    @Test
    public void exportAscii() throws Exception
    {
        assertNotNull( PGPKeyUtil.exportAscii( PGPKeyUtil.readPublicKey( PUBLIC_KEY ) ) );
    }


    @Test
    public void readPublicKey() throws Exception
    {
        assertNotNull( PGPKeyUtil.readPublicKey( PUBLIC_KEY.getBytes() ) );
    }


    @Test
    public void readPublicKeyRing() throws Exception
    {
        assertNotNull( PGPKeyUtil.readPublicKeyRing( PUBLIC_KEY.getBytes() ) );
    }


    @Test
    public void readPublicKeyRing1() throws Exception
    {
        assertNotNull( PGPKeyUtil.readPublicKeyRing( PUBLIC_KEY ) );
    }


    @Test
    public void readSecretKeyRing() throws Exception
    {
        assertNotNull( PGPKeyUtil.readSecretKeyRing( PRIVATE_KEY ) );
    }




    @Test
    public void readSecretKeyRing1() throws Exception
    {
        assertNotNull( PGPKeyUtil.readSecretKeyRing( PRIVATE_KEY.getBytes() ) );
    }


    @Test
    public void readSecretKeyRing2() throws Exception
    {
        assertNotNull( PGPKeyUtil.readSecretKeyRingInputStream( PRIVATE_KEY.getBytes() ) );
    }


    @Test
    public void readPublicKey3() throws Exception
    {
        assertNull( PGPKeyUtil.readPublicKey( pgpPublicKeyRing ) );
    }


    @Test
    public void readSecretKey() throws Exception
    {
        assertNull( PGPKeyUtil.readSecretKey( pgpSecretKeyRing ) );
    }
}