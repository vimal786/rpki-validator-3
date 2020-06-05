package net.ripe.rpki.validator3.util;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.net.URI;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrustAnchorLocatorTest {
    @Test
    public void readStandardTrustAnchor_rsync_only() throws Exception {
        File talFile = new ClassPathResource("tals/rfc7730/ripe-rsync-only.tal").getFile();

        TrustAnchorLocator tal = TrustAnchorLocator.fromFile(talFile);

        then(tal.getCertificateLocations()).containsExactly(
            URI.create("rsync://rpki.ripe.net/ta/ripe-ncc-ta.cer")
        );

        then(tal.getPublicKeyInfo()).isEqualTo(
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0URYSGqUz2myBsOzeW1j" +
            "Q6NsxNvlLMyhWknvnl8NiBCs/T/S2XuNKQNZ+wBZxIgPPV2pFBFeQAvoH/WK83Hw" +
            "A26V2siwm/MY2nKZ+Olw+wlpzlZ1p3Ipj2eNcKrmit8BwBC8xImzuCGaV0jkRB0G" +
            "Z0hoH6Ml03umLprRsn6v0xOP0+l6Qc1ZHMFVFb385IQ7FQQTcVIxrdeMsoyJq9eM" +
            "kE6DoclHhF/NlSllXubASQ9KUWqJ0+Ot3QCXr4LXECMfkpkVR2TZT+v5v658bHVs" +
            "6ZxRD1b6Uk1uQKAyHUbn/tXvP8lrjAibGzVsXDT2L0x4Edx+QdixPgOji3gBMyL2" +
            "VwIDAQAB"
        );
    }

    @Test
    public void readStandardTrustAnchor_https_rsync() throws Exception {
        File talFile = new ClassPathResource("tals/rfc7730/afrinic-https-rsync.tal").getFile();

        TrustAnchorLocator tal = TrustAnchorLocator.fromFile(talFile);

        then(tal.getCertificateLocations()).containsExactly(
            URI.create("https://rpki.afrinic.net/repository/AfriNIC.cer"),
            URI.create("rsync://rpki.afrinic.net/repository/AfriNIC.cer")
        );

        then(tal.getPublicKeyInfo()).isEqualTo(
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxsAqAhWIO+ON2Ef9oRDM" +
            "pKxv+AfmSLIdLWJtjrvUyDxJPBjgR+kVrOHUeTaujygFUp49tuN5H2C1rUuQavTH" +
            "vve6xNF5fU3OkTcqEzMOZy+ctkbde2SRMVdvbO22+TH9gNhKDc9l7Vu01qU4LeJH" +
            "k3X0f5uu5346YrGAOSv6AaYBXVgXxa0s9ZvgqFpim50pReQe/WI3QwFKNgpPzfQL" +
            "6Y7fDPYdYaVOXPXSKtx7P4s4KLA/ZWmRL/bobw/i2fFviAGhDrjqqqum+/9w1hEl" +
            "L/vqihVnV18saKTnLvkItA/Bf5i11Yhw2K7qv573YWxyuqCknO/iYLTR1DToBZcZ" +
            "UQIDAQAB"
        );
    }

    @Test
    public void readStandardTrustAnchor_rsync_https() throws Exception {
        File talFile = new ClassPathResource("tals/rfc7730/afrinic-reversed-rsync-https.tal").getFile();

        TrustAnchorLocator tal = TrustAnchorLocator.fromFile(talFile);

        then(tal.getCertificateLocations()).containsExactly(
                URI.create("rsync://rpki.afrinic.net/repository/AfriNIC.cer"),
                URI.create("https://rpki.afrinic.net/repository/AfriNIC.cer")
        );

        then(tal.getPublicKeyInfo()).isEqualTo(
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxsAqAhWIO+ON2Ef9oRDM" +
                        "pKxv+AfmSLIdLWJtjrvUyDxJPBjgR+kVrOHUeTaujygFUp49tuN5H2C1rUuQavTH" +
                        "vve6xNF5fU3OkTcqEzMOZy+ctkbde2SRMVdvbO22+TH9gNhKDc9l7Vu01qU4LeJH" +
                        "k3X0f5uu5346YrGAOSv6AaYBXVgXxa0s9ZvgqFpim50pReQe/WI3QwFKNgpPzfQL" +
                        "6Y7fDPYdYaVOXPXSKtx7P4s4KLA/ZWmRL/bobw/i2fFviAGhDrjqqqum+/9w1hEl" +
                        "L/vqihVnV18saKTnLvkItA/Bf5i11Yhw2K7qv573YWxyuqCknO/iYLTR1DToBZcZ" +
                        "UQIDAQAB"
        );
    }

    @Test
    public void readStandardTrustAnchor_reject_http() throws Exception {
        File talFile = new ClassPathResource("tals/rfc7730/example-tal-with-http-and-rsync.tal").getFile();

        assertThrows(TrustAnchorExtractorException.class, () -> TrustAnchorLocator.fromFile(talFile));
    }
}